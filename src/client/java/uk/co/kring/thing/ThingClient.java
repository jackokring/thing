package uk.co.kring.thing;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;

public class ThingClient implements ClientModInitializer {
    public static String tooltipKey(ItemConvertible item) {
        return item.asItem().getTranslationKey() + ".tooltip";
    }

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ItemTooltipCallback.EVENT.register((itemStack, tooltipContext, tooltipType, list) -> {
            if(itemStack.isOf(ModItems.SUSPICIOUS_SUBSTANCE))
                list.add(Text.translatable(tooltipKey(itemStack.getItem())));
        });
	}
}