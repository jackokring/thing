package uk.co.kring.thing;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import eu.pb4.placeholders.api.parsers.TagParser;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;

public class ThingClient implements ClientModInitializer {

    // tooltip naming assist
    static String tooltipKey(ItemLike item) {
        return item.asItem().getDescriptionId() + ".tooltip";
    }

    // config container static
    static ModConfig CONFIG;

    // encrypted chat static data and methods
    static SecretKey KEY;
    static byte[] SALT = Thing.MOD_ID.getBytes();

    static void getKeyFromPassword(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, SALT, 65536, 256);
        KEY = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    // key bind assistance methods
    static String keyName(String name) { return "key." + Thing.MOD_ID + "." + name; }

    static class Holder<T> {
        public Holder(T in) {
            value = in;
        }
        public T value;
    }

    static void debounce(Holder<Boolean> held, KeyMapping key, Minecraft client, Runnable run) {
        if(!held.value && key.isDown() && client.player != null) {
            run.run();
        }
        held.value = key.isDown();
    }

    static KeyMapping keyBinding_R;
    static final Holder<Boolean> held_R = new Holder<>(false);

    static KeyMapping keyBinding_Y;
    static final Holder<Boolean> held_Y = new Holder<>(false);

	@Override
	public void onInitializeClient() {
        // decide any tool tip types for things
        // this is for the simple registration of a tip per item or block item
        tipSimple(ModItems.SUSPICIOUS_SUBSTANCE);

		// This entrypoint is suitable for setting up client-specific logic, such as rendering
        // the tooltip entry callback for adding in any registered tooltips
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext,
                                            tooltipType, list) -> {
            MutableComponent tip = tipMap.get(itemStack.getItem());
            if(tip != null)
                list.add(tip);
        });

        // Config init
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        // commands
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) ->
                        dispatcher.register(
                        ClientCommandManager.literal("client_thing").executes(context -> {
                    context.getSource().sendFeedback(Component.literal("Called /client_thing with no arguments."));
                    return Command.SINGLE_SUCCESS;
                })));

        // Chat interceptor
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ClientReceiveMessageEvents.ALLOW_CHAT.register(this::decryptChatMessage);
            ClientReceiveMessageEvents.MODIFY_GAME.register(this::modifyGameMessage);
            ClientSendMessageEvents.MODIFY_CHAT.register(this::encryptChatMessage);
        });

        // key binds for all interactivity
        keyBinding_R = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                keyName("spook"), // The translation key of the keybinding's name
                //InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                KeyMapping.Category.MISC // The category of the key
        ));
        keyBinding_Y = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                keyName("types"),
                GLFW.GLFW_KEY_Y, // The keycode of the key
                KeyMapping.Category.MISC // The category of the key
        ));

        // KEYS
        // Free: `0-=uio[]
        // Free: ghjk;'
        // Free: zbnm,.
        // Fn Free: 8 9 10 12
        // Intl: \#
        // Used: ry
        // Other: v (voice chat)
        // No: Super/Alt/Menu
        // Maybe: RCtrl/BS/Return

        // key actions
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            debounce(held_R, keyBinding_R, client, () -> {
                CONFIG.cryptEnabled ^= true;
                // and ENCRYPTION OFF or hidden + ENCRYPTION OFF !! :)
                // this unexpected line is needed to make Java sane
                // seems it can't prove in the closure
                assert client.player != null;
                client.player.displayClientMessage(Thing.withColorBold(Component.literal(
                        encryptChatMessage(Component.translatable(encryptKey).getString())),
                        ChatFormatting.DARK_AQUA, true), true);
            });
            debounce(held_Y, keyBinding_Y, client, () -> {
                CONFIG.typeEnabled ^= true;
                // types in chat as prefix messages
            });
        });
    }

    // tooltip optimizer for speed with large number of tips
    HashMap<ItemLike, MutableComponent> tipMap = new HashMap<>();

    // the simple static tooltip kind
    void tipSimple(ItemLike is) {
        tipMap.put(is, Component.translatable(tooltipKey(is)));
    }

    // MiniMessage to formatted converter
    // argument wrapper is ':<>% ' for any of special 5 characters in arg
    // \< is tag as literal text
    static String useSimpleText(String in) {
        in = in.replaceAll(ChatFormatting.PREFIX_CODE + ".", "");
        return TagParser.SIMPLIFIED_TEXT_FORMAT_SAFE.parseNode(in).toText().getString();
    }

    // basic chat crypto channels selected by key with suitable intercept options

    static final String hidden = ChatFormatting.PREFIX_CODE + "k";

    // The following is a mashed up version of similar to
    // https://git.brn.systems/BRNSystems/chatencryptor/src/branch/main/src/main/java/systems/brn/chatencryptor/SecureChat.java
    // Under MIT Licence, but with some adaptations to use cloth config, 1.21.10 and kind of MiniMessage formatting

    static String getTypeKey(String type) {
        return "chat.type." + type;
    }

    // basic chat keys
    // N.B. only public simple chatting is encrypted. PMs are "private" and hence not of encrypted need beyond
    // gaming provision. Generally you'd assume it's more of a public watershed technology and NOT a reason
    // to be creepy to someone who'd have the key anyway, if it were such as it would've been.

    static final String typeKey = getTypeKey("type");// { \"translate\": \"%s\" } -- OK, writes above message type applies to
    static final String adminKey = getTypeKey("admin");// [%s: %s]
    static final String challengeKey = getTypeKey("advancement.challenge");// %s has completed the challenge %s
    static final String goalKey = getTypeKey("advancement.goal");// %s has reached the goal %s
    static final String taskKey = getTypeKey("advancement.task");// %s has made the advancement %s
    static final String announceKey = getTypeKey("announcement");// [%s] %s
    static final String emoteKey = getTypeKey("emote");// * %s %s
    static final String chatKey = getTypeKey("text");// <%s> %s -- OK
    static final String talkKey = getTypeKey("text.narrate");// %s says %s
    static final String galacticKey = getTypeKey("talk.galactic");// something encrypted
    static final String exceptionKey = getTypeKey("exception");// Encryption exception: %s
    static final String encryptKey = getTypeKey("encrypt");// ENCRYPTION OFF -- OK

    static void printType(TranslatableContents message) {
        if(CONFIG.typeEnabled)
            Minecraft.getInstance().gui.getChat().addMessage(
                    Thing.withColorBold(Component.translatable(typeKey, message.getKey()), ChatFormatting.DARK_AQUA, false));
    }

    boolean decryptChatMessage(Component component, @Nullable PlayerChatMessage playerChatMessage,
                               @Nullable GameProfile gameProfile, ChatType.Bound bound, Instant instant) {
        if(component.getContents() instanceof TranslatableContents msg) {
            // as I think other typing is of various formats
            // and default chat.type.text is "<%s> %s" translatable with 2 args
            if(msg.getKey().equals(chatKey) || msg.getKey().equals(talkKey)) {
                Style style = component.getStyle();
                String message_content = msg.getArgument(1).getString();
                // leaving it as formatted text allows hover etc., to work still
                FormattedText player_name = msg.getArgument(0);
                if (message_content.startsWith(hidden)) {
                    try {
                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                        cipher.init(Cipher.DECRYPT_MODE, KEY);
                        String strippedMessage = message_content.substring(2);
                        byte[] decodedMessage = Base64.getDecoder().decode(strippedMessage);
                        cipher.update(decodedMessage);
                        // I just like being explicit and bad UTF-8 is wrong key indicator
                        CharsetDecoder cd = StandardCharsets.UTF_8.newDecoder();
                        String decryptedMessage = cd.decode(ByteBuffer.wrap(cipher.doFinal())).toString();
                        // That was a bit of a find in the Mojang mappings
                        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable(msg.getKey(),
                                player_name, decryptedMessage).setStyle(style)); // maintain style of component
                        return false;
                    } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException |
                             NoSuchPaddingException | CharacterCodingException | // Less bad decode prints
                             InvalidKeyException | IllegalArgumentException e) {// Also base64 decode fails
                        if(msg.getKey().equals(talkKey) && CONFIG.galacticEnabled) {
                            Minecraft.getInstance().gui.getChat().addMessage(Component.translatable(talkKey,
                                    msg.getArgument(0), Component.translatable(galacticKey))); // avoid spaz speech
                            return false;
                        }
                        return CONFIG.galacticEnabled;//remove some chatter
                    }
                }
            }

            // show message formats not intercepted
            printType(msg);
        }
        return true;
    }

    String encryptChatMessage(String message) {
        message = useSimpleText(message);// MiniMessage
        if(CONFIG.cryptEnabled){
            String encodedMessage;
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, KEY);
                cipher.update(message.getBytes(StandardCharsets.UTF_8));
                byte[] encryptedMessage = cipher.doFinal();
                encodedMessage = Base64.getEncoder().encodeToString(encryptedMessage);
            } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                     NoSuchAlgorithmException | InvalidKeyException e) {
                return Thing.withColorBold(Component.translatable(exceptionKey, message),
                        ChatFormatting.AQUA, false).getString();
            }
            return hidden + encodedMessage;
        } else {
            return message;
        }
    }

    Component modifyGameMessage(@NotNull Component component, boolean overlayInBar) {
        // to modify game messages
        if(component.getContents() instanceof TranslatableContents msg) {
            // as I think other typing is of various formats
            // and default chat.type.text is "<%s> %s" translatable with 2 args
            // likely "multiplayer. ..." or in assets/minecraft/lang/en_us.json
            if(msg.getKey().equals(encryptKey)) {
                // this type is from the mod so it doesn't need a print out of type
                return component;
            }
            // show message formats not intercepted
            printType(msg);
        }
        return component;
    }
}