package uk.co.kring.thing;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ItemLike;

import javax.swing.*;
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
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
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
}