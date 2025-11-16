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
    static Holder<Potion> make(String name, Holder<MobEffect> effect, boolean redstone) {
        return BuiltInRegistries.POTION.wrapAsHolder(Registry.register(
                BuiltInRegistries.POTION,
                Thing.identify(name),
                new Potion(name,
                        new MobEffectInstance(
                                effect,
                                redstone ? 9600 : 3600,
                                0))));
    }

    static void regHelper(Holder<Potion> p, ItemLike i, Holder<Potion> q) {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    p,
                    // Ingredient
                    Ingredient.of(i),
                    // Output potion.
                    q
            );
        });
    }

    static Holder<Potion> registerPair(String name, Holder<MobEffect> effect,
                                       Holder<Potion> input, ItemLike add, boolean redstone) {
        Holder<Potion> wrap = make(name, effect, false);
        regHelper(input, add, wrap);
        if(!redstone) return wrap;// no long duration ...
        Holder<Potion> wrapLong = make("long_" + name, effect, true);
        regHelper(wrap, Items.REDSTONE, wrapLong);
        return wrap;//for further brewing
    }

    static void initialize() {
        // can use the return as another input
        // glowstone basis
        // leave mundane as failed
        // fermented spider eye as corrupt
        registerPair("test", MobEffects.POISON, Potions.THICK, Items.POTATO, true);
    }
}