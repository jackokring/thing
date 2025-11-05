package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    protected BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, registryLookup);
    }

    public float inverse(float f) {
        return 1/f;
    }

    @Override
    public void generate() {
        // Make suspicious dirt drop
        add(ModBlocks.SUSPICIOUS_DIRT, createSingleItemTable(ModItems.SUSPICIOUS_SUBSTANCE,
                new BinomialDistributionGenerator(new ConstantValue(18), new ConstantValue(inverse(2f)))));

        // Make prismarine lamps drop themselves with silk touch only
        //createSilkTouchOnlyTable(ModBlocks.PRISMARINE_LAMP);
    }
}