package uk.co.kring.thing;

import com.mojang.brigadier.Command;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
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

    // parse section marker strings into MutableComponents
    // maintaining accumulated Style between parts following on
    // implements a simple replace "%s" with mechanism
    // an initial Style accumulator maybe supplied
    // .getString() ?
    static MutableComponent parseStringSection(String s) {
        return parseStringSection(s, Style.EMPTY);
    }

    static MutableComponent parseStringSection(String s, Style acc) {
        return parseStringSection(s, acc, null);
    }

    static MutableComponent parseStringSection(String s, String repWith) {
        return parseStringSection(s, Style.EMPTY, repWith);
    }

    static MutableComponent parseStringSection(String s, Style acc, String repWith) {
        if(repWith != null) {
            MutableComponent rep = parseStringSection(repWith);
            String[] parts = s.split("%s");// format
            MutableComponent out = parseStringSection(parts[0], acc);
            int i = 1;
            while(i < parts.length) {
                out.append(rep);
                MutableComponent mc = parseStringSection(parts[i], acc);
                out.append(mc);
                i++;
            }
            return out;
        }
        // a rough non-robust parse
        String[] p = s.split("§");
        acc = acc == null ? Style.EMPTY : acc;
        MutableComponent out = Component.literal(p[0]).withStyle(acc);
        int i = 1;
        while(i < p.length) {
            String x = p[i];
            char code = x.charAt(0);
            MutableComponent c = Component.literal(x.substring(1));
            ChatFormatting f = ChatFormatting.getByCode(code);
            acc = f == null ? acc : acc.applyFormat(f);
            i++;
            out = out.append(c.withStyle(acc));
        }
        return out;
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

    static MutableComponent withColorBold(MutableComponent message, ChatFormatting color, boolean bold) {
        return message.withStyle(style -> style.withColor(color).withBold(bold));
    }

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
        ModItems.initialize();
        ModBlocks.initialize();

        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) ->
            dispatcher.register(
                    Commands.literal("test_command").executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Called /test_command."), false);
                return Command.SINGLE_SUCCESS;
            }))
        );

        // register mod placeholders (the literal %arg)
        // lucky that text translation is done client side
        Placeholders.register(Thing.identify("percent"), (ctx, /* @Nullable */ arg) -> {
            // ctx always has server
            // others need PlaceholderContext.of(...) and ctx.hasXXX() test
            if(arg == null) arg = "";
            return PlaceholderResult.value("%" + arg);// or .invalid([out]);
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
                        case Commands.LEVEL_OWNERS: return withColorBold(message.copy(), ChatFormatting.GOLD, true);
                        // administrator
                        case Commands.LEVEL_ADMINS: return withColorBold(message.copy(), ChatFormatting.GOLD, false);
                        // gamemaster
                        case Commands.LEVEL_GAMEMASTERS: return withColorBold(message.copy(), ChatFormatting.RED, true);
                        // moderator
                        case Commands.LEVEL_MODERATORS: return withColorBold(message.copy(), ChatFormatting.RED, false);
                        default: break; // maybe ??
                    }
                }
            }
            return message;
        }));
    }
}