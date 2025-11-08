package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
    static final TagKey<Item> SUSPICIOUS_ITEMS = TagKey.create(Registries.ITEM, Thing.identify("suspicious_items"));

    public ItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(SUSPICIOUS_ITEMS)
                .add(ModItems.SUSPICIOUS_SUBSTANCE)
                .add(ModBlocks.SUSPICIOUS_DIRT.asItem());
    }
}
