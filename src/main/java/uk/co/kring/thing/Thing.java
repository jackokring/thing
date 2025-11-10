package uk.co.kring.thing;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Thing implements ModInitializer {
	static final String MOD_ID = "thing";

    static ResourceLocation identify(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModItems.initialize();
        ModBlocks.initialize();

        // Style up the ops posts using this
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE,
                    (sender, message) -> {
                        if (sender != null && sender.server.getPlayerList().isOp(sender.nameAndId())) {
                            return message.copy().withStyle(style ->
                                    style.withColor(ChatFormatting.getByName("gold")).withBold(true));
                        }
                        return message;
            });
        });
    }
}