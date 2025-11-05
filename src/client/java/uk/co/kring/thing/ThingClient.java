package uk.co.kring.thing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ItemLike;

import javax.swing.*;
import java.util.HashMap;

public class ThingClient implements ClientModInitializer {

    public static String tooltipKey(ItemLike item) {
        return item.asItem().getDescriptionId() + ".tooltip";
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
	}

    HashMap<ItemLike, MutableComponent> tipMap = new HashMap<>();

    public void tipSimple(ItemLike is) {
        tipMap.put(is, Component.translatable(tooltipKey(is)));
    }
}