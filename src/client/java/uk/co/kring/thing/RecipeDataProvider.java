package uk.co.kring.thing;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.RecipeProvider;

public class RecipeDataProvider extends FabricRecipeProvider {
    protected RecipeDataProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, registryLookup);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                //HolderLookup.RegistryLookup<Item> itemLookup = registries.getOrThrow(Registries.ITEM);
                shaped(RecipeCategory.MISC, ModBlocks.SUSPICIOUS_DIRT)
                        .pattern("sss")
                        .pattern("sss")
                        .pattern("sss")
                        .define('s', ModItems.SUSPICIOUS_SUBSTANCE)
                        .save(exporter);
                shapeless(RecipeCategory.MISC, ModItems.SUSPICIOUS_SUBSTANCE, 9)
                        .requires(ModBlocks.SUSPICIOUS_DIRT)
                        .save(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "RecipeProvider";
    }
}