package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public static final TagKey<Item> SUSPICIOUS_ITEMS = TagKey.of(RegistryKeys.ITEM, Thing.identify("suspicious_items"));

    public ItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(SUSPICIOUS_ITEMS)
                .add(ModItems.SUSPICIOUS_SUBSTANCE)
                .add(ModBlocks.SUSPICIOUS_DIRT.asItem());
    }
}
