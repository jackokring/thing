package uk.co.kring.thing;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class RecipeProvider extends FabricRecipeProvider {
    protected RecipeProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, registryLookup);
    }

    @Override
    protected net.minecraft.data.recipes.@NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new net.minecraft.data.recipes.RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                //HolderLookup.RegistryLookup<Item> itemLookup = registries.getOrThrow(Registries.ITEM);
                shaped(RecipeCategory.MISC, ModBlocks.SUSPICIOUS_DIRT)
                        .pattern("sss")
                        .pattern("sss")
                        .pattern("sss")
                        .define('s', ModItems.SUSPICIOUS_SUBSTANCE)
                        .unlockedBy(getHasName(ModItems.SUSPICIOUS_SUBSTANCE), has(ModItems.SUSPICIOUS_SUBSTANCE))
                        .save(exporter);
                shapeless(RecipeCategory.MISC, ModItems.SUSPICIOUS_SUBSTANCE, 9)
                        .requires(ModBlocks.SUSPICIOUS_DIRT)
                        .unlockedBy(getHasName(ModBlocks.SUSPICIOUS_DIRT), has(ModBlocks.SUSPICIOUS_DIRT))
                        .save(exporter);

                // craftable-nametag has a recipe of low iron https://modrinth.com/datapack/craftable-nametag
                // really just a data generator task needed
                shaped(RecipeCategory.MISC, Items.NAME_TAG)
                        .pattern(" si")
                        .pattern(" ps")
                        .pattern("p  ")
                        .define('p', Items.PAPER)
                        .define('s', Items.STRING)
                        .define('i', Items.COPPER_INGOT)
                        .unlockedBy(getHasName(Items.PAPER), has(Items.PAPER))
                        .unlockedBy(getHasName(Items.STRING), has(Items.STRING))
                        .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                        .save(exporter);
            }
        };
    }

    @Override
    public @NotNull String getName() {
        return "RecipeProvider";
    }
}