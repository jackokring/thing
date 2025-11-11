package uk.co.kring.thing;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.parsers.TagParser;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

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

        // Chat interceptor
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            // Register event listener for ClientTickEvents.END_CLIENT_TICK
            ClientReceiveMessageEvents.ALLOW_CHAT.register(this::decryptChatMessage);
            //ClientReceiveMessageEvents.MODIFY_GAME
            ClientSendMessageEvents.MODIFY_CHAT.register(this::encryptChatMessage);
            //ClientSendMessageEvents.MODIFY_COMMAND
        });
    }

    HashMap<ItemLike, MutableComponent> tipMap = new HashMap<>();

    void tipSimple(ItemLike is) {
        tipMap.put(is, Component.translatable(tooltipKey(is)));
    }

    static String useSimpleText(String in) {
        in = in.replaceAll("§.", "");
        return TagParser.SIMPLIFIED_TEXT_FORMAT.parseNode(in).toText().getString();
    }

    // The following is a mashed up version of similar to
    // https://git.brn.systems/BRNSystems/chatencryptor/src/branch/main/src/main/java/systems/brn/chatencryptor/SecureChat.java
    // Under MIT Licence, but with some adaptations to use cloth config, 1.21.10 and kind of MiniMessage formatting

    boolean decryptChatMessage(Component component, @Nullable PlayerChatMessage playerChatMessage, @Nullable GameProfile gameProfile, ChatType.Bound bound, Instant instant) {
        if(bound.chatType().is(ChatType.CHAT)) { // as I think other typing is of various formats
            Style style = component.getStyle();
            TranslatableContents content = (TranslatableContents) component.getContents();
            String message_content = content.getArgument(1).getString();
            String player_name = content.getArgument(0).getString();
            if (message_content.startsWith("§k")) {
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
                    Minecraft.getInstance().gui.getChat().addMessage(Component.translatable("chat.type.text",
                            player_name, decryptedMessage).setStyle(style)); // maintain style of component
                    return false;
                } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException |
                         NoSuchPaddingException | CharacterCodingException | // Less bad decode prints
                         InvalidKeyException | IllegalArgumentException e) {// Also base64 decode fails
                    return true;
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
            return "§k" + encodedMessage;
        } else {
            return message;
        }
    }
}