package uk.co.kring.thing;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.InputConstants;
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

    static String tooltipKey(ItemLike item) {
        return item.asItem().getDescriptionId() + ".tooltip";
    }

    static String keyName(String name) { return "key." + Thing.MOD_ID + "." + name; }

    static KeyMapping keyBinding;
    static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Thing.identify("key"));

    static ModConfig CONFIG;
    static SecretKey KEY;
    static byte[] SALT = Thing.MOD_ID.getBytes();

    static void getKeyFromPassword(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, SALT, 65536, 256);
        KEY = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

	@Override
	public void onInitializeClient() {
        tipSimple(ModItems.SUSPICIOUS_SUBSTANCE);

		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext,
                                            tooltipType, list) -> {
            MutableComponent tip = tipMap.get(itemStack.getItem());
            if(tip != null)
                list.add(tip);
        });

        // Config init
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

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
            //ClientSendMessageEvents.MODIFY_COMMAND
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                keyName("spook"), // The translation key of the keybinding's name
                InputConstants.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                KEY_CATEGORY // The category of the key - you'll need to add a translation for this!
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(keyBinding.isDown() && client.player != null)
                client.player.displayClientMessage(Component.literal("Key 1 was pressed!"), false);
        });
    }

    HashMap<ItemLike, MutableComponent> tipMap = new HashMap<>();

    void tipSimple(ItemLike is) {
        tipMap.put(is, Component.translatable(tooltipKey(is)));
    }

    // argument wrapper is ':<>% ' for any of special 5 characters in arg
    // \< is tag as literal text
    static String useSimpleText(String in) {
        in = in.replaceAll(ChatFormatting.PREFIX_CODE + ".", "");
        return TagParser.SIMPLIFIED_TEXT_FORMAT_SAFE.parseNode(in).toText().getString();
    }

    static final String hidden = ChatFormatting.PREFIX_CODE + "k";

    // The following is a mashed up version of similar to
    // https://git.brn.systems/BRNSystems/chatencryptor/src/branch/main/src/main/java/systems/brn/chatencryptor/SecureChat.java
    // Under MIT Licence, but with some adaptations to use cloth config, 1.21.10 and kind of MiniMessage formatting

    // basic chat key
    static final String chatKey = "chat.type.text";

    boolean decryptChatMessage(Component component, @Nullable PlayerChatMessage playerChatMessage, @Nullable GameProfile gameProfile, ChatType.Bound bound, Instant instant) {
        if(component.getContents() instanceof TranslatableContents msg) {
            // as I think other typing is of various formats
            // and default chat.type.text is "<%s> %s" translatable with 2 args
            if(msg.getKey().equals(chatKey)) {
                Style style = component.getStyle();
                String message_content = msg.getArgument(1).getString();
                String player_name = msg.getArgument(0).getString();
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
                        Minecraft.getInstance().gui.getChat().addMessage(Component.translatable(chatKey,
                                player_name, decryptedMessage).setStyle(style)); // maintain style of component
                        return false;
                    } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException |
                             NoSuchPaddingException | CharacterCodingException | // Less bad decode prints
                             InvalidKeyException | IllegalArgumentException e) {// Also base64 decode fails
                        return true;
                    }
                }
            }
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
                return "";
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
            if(msg.getKey().equals(chatKey)) {
                // could modify this based on a lang file key for a game message that was used
                return component;
            }
        }
        return component;
    }
}