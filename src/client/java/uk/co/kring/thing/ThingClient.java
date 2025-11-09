package uk.co.kring.thing;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.Message;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessage;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;

public class ThingClient implements ClientModInitializer {

    static String tooltipKey(ItemLike item) {
        return item.asItem().getDescriptionId() + ".tooltip";
    }

    static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
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

        // Chat interceptor
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            // Register event listener for ClientTickEvents.END_CLIENT_TICK
            ClientReceiveMessageEvents.ALLOW_CHAT.register(this::decryptChatMessage);
            ClientSendMessageEvents.MODIFY_CHAT.register(this::encryptChatMessage);
        });
    }

    HashMap<ItemLike, MutableComponent> tipMap = new HashMap<>();

    void tipSimple(ItemLike is) {
        tipMap.put(is, Component.translatable(tooltipKey(is)));
    }

    static Component useSimpleText(String in) {
        // V1 deprecated
        //@SuppressWarnings("all")
        NodeParser parser = NodeParser.merge(TagParser.SIMPLIFIED_TEXT_FORMAT,
                Placeholders.DEFAULT_PLACEHOLDER_PARSER);
        // toText accepts PlaceholderContext and ParserContext etc.
        // So this is apparently how to get a V1 MiniMessage format with placeholder insertions
        // The documentation appears out of date, and well, who knows?

        // I suppose it's not helped by the source not being compiled against Mojang base
        // and so the .class file has to be decompiled with parchment too
        // but, yes, that should be it for %placeholder% as default
        return parser.parseNode(in).toText();
    }

    // The following is a mashed up version of similar to
    // https://git.brn.systems/BRNSystems/chatencryptor/src/branch/main/src/main/java/systems/brn/chatencryptor/SecureChat.java
    // Under MIT Licence, but with some adaptations to use cloth config, 1.21.10 an kind of MiniMessage formatting

    // TODO:
    // maybe some teams §<colour>§k crypt §r
    // maybe a decoder item
    // ummm ...

    boolean decryptChatMessage(Component component, @Nullable PlayerChatMessage playerChatMessage, @Nullable GameProfile gameProfile, ChatType.Bound bound, Instant instant) {
        TranslatableTextContent content = (TranslatableTextContent) message.getContent();
        String message_content = content.getArg(1).getString();
        String player_name = content.getArg(0).getString();
        if(message_content.startsWith("®") && message_content.endsWith("®")){
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, Config.HANDLER.instance().getRawKey(), Config.HANDLER.instance().getRawIv());
                String strippedMessage = message_content.substring(1, message_content.length() - 1);
                byte[] decodedMessage = ChatCoder.decodeFromBmp(strippedMessage);
                cipher.update(decodedMessage);
                String decryptedMessage = new String(cipher.doFinal());
                String outputMessage = "{" + player_name + "} " + decryptedMessage;
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Component.literal(outputMessage));
                return false;
            }
            catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                   InvalidKeyException e){
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of((Message) e));
                return true;
            }
        }
        return true;
    }

    String encryptChatMessage(String message) {
        if(Config.HANDLER.instance().isEnabled() ^ isShiftPressed()){
            String encodedMessage;
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, Config.HANDLER.instance().getRawKey(), Config.HANDLER.instance().getRawIv());
                cipher.update(message.getBytes(StandardCharsets.UTF_8));
                byte[] encryptedMessage = cipher.doFinal();
                encodedMessage = ChatCoder.encodeToBmp(encryptedMessage);
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                     NoSuchAlgorithmException e) {
                return "";
            }
            return '®' + encodedMessage + '®';
        }
        else {
            return message;
        }
    }
}