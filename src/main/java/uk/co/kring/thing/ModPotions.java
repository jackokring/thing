package uk.co.kring.thing;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

class ModPotions {
    static Potion make(String name, Holder<MobEffect> effect, boolean redstone) {
        return Registry.register(
                BuiltInRegistries.POTION,
                Thing.identify(name),
                new Potion(name,
                        new MobEffectInstance(
                                effect,
                                redstone ? 9600 : 3600,
                                0)));
    }

    static void registerPair(String name, Holder<MobEffect> effect, Holder<Potion> input, ItemLike add) {
        Holder<Potion> wrap = BuiltInRegistries.POTION.wrapAsHolder(make(name, effect, false));
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    input,
                    // Ingredient
                    Ingredient.of(add),
                    // Output potion.
                    wrap
            );
        });
        Holder<Potion> wrapLong = BuiltInRegistries.POTION.wrapAsHolder(make("long_" + name, effect, true));
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    wrap,
                    // Ingredient
                    Ingredient.of(Items.REDSTONE),
                    // Output potion.
                    wrapLong
            );
        });
    }

    static void initialize() {
        registerPair("test", MobEffects.POISON, Potions.WATER, Items.POTATO);
    }
}
