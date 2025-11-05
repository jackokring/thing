package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Make suspicious dirt drop its block item.
        // Also adds the condition that it survives the explosion that broke it, if applicable,
        createSingleItemTable(ModBlocks.SUSPICIOUS_DIRT);

        // Make prismarine lamps drop themselves with silk touch only
        //createSilkTouchOnlyTable(ModBlocks.PRISMARINE_LAMP);
    }
}