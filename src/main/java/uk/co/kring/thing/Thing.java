package uk.co.kring.thing;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.entity.Entity;
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

    static Component usePlaceholders(Component in, MinecraftServer server) {
        if(server == null) return in;
        // Server side replacements (global context)
        return Placeholders.parseText(in, PlaceholderContext.of(server));
    }

    static Component usePlaceholders(Component in, ServerPlayer serverPlayer) {
        if(serverPlayer == null) return in;
        // Server side replacements (single player centric)
        return Placeholders.parseText(in, PlaceholderContext.of(serverPlayer));
    }

    static Component usePlaceholders(Component in, Entity entity) {
        if(entity == null) return in;
        // Server side replacements (any entity centric)
        return Placeholders.parseText(in, PlaceholderContext.of(entity));
    }

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModItems.initialize();
        ModBlocks.initialize();

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("test_command").executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Called /test_command."), false);
                return 1;
            }));
        });

        // register mod placeholders TODO
        Placeholders.register(Thing.identify("placeholder"), (ctx, /* @Nullable */ arg) -> {
            // ctx always has server
            // others need PlaceholderContext.of(...) and ctx.hasXXX() test
            return PlaceholderResult.value(arg);// or .invalid([out]);
        });

        // Style up the ops posts using this
        // Modifying actual text content will cause warning (signature changes)
        // ServerMessageDecoratorEvent.CONTENT_PHASE is BAD and NAUGHTY
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
                ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE,
                (sender, message) -> {
            if(sender != null) {
                NameAndId player = sender.nameAndId();
                ServerOpListEntry op = server.getPlayerList().getOps().get(player);
                if(op != null) {
                    switch (op.getLevel()) {
                        // owner
                        case 4: return message.copy().withStyle(style ->
                                style.withColor(ChatFormatting.getByName("gold")).withBold(true));
                        // administrator
                        case 3: return message.copy().withStyle(style ->
                                style.withColor(ChatFormatting.getByName("gold")));
                        // gamemaster
                        case 2: return message.copy().withStyle(style ->
                                style.withColor(ChatFormatting.getByName("red")).withBold(true));
                        // moderator
                        case 1: return message.copy().withStyle(style ->
                                style.withColor(ChatFormatting.getByName("red")));
                        default: break; // maybe ??
                    }
                }
            }
            return message;
        }));
    }
}