package uk.co.kring.thing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class ThingClient implements ClientModInitializer {
    public static String tooltipKey(ItemLike item) {
        return item.asItem().getDescriptionId() + ".tooltip";
    }

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext,
                                            tooltipType, list) -> {
                exec(ModItems.SUSPICIOUS_SUBSTANCE, itemStack, list);
        });
	}

    public static void exec(ItemLike is, ItemStack itemStack, List<Component> list) {
        if(itemStack.getItem() == is)
            list.add(Component.translatable(tooltipKey(is)));

    }
}