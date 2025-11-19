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
    static Holder<Potion> make(String name, Holder<MobEffect> effect, boolean redstone, boolean glowstone) {
        return BuiltInRegistries.POTION.wrapAsHolder(Registry.register(
                BuiltInRegistries.POTION,
                Thing.identify(name),
                new Potion(name,
                        new MobEffectInstance(
                                effect,
                                // balancing, although 4800 isn't naturally produced
                                redstone ? (glowstone ? 4800 : 9600) : (glowstone ? 1800 : 3600),
                                glowstone ? 1 : 0))));
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
                                       Holder<Potion> input, ItemLike add, boolean redstone, boolean glowstone) {
        Holder<Potion> wrap = make(name, effect, false, false);
        regHelper(input, add, wrap);
        if(redstone) { // long duration ...
            Holder<Potion> wrapLong = make("long_" + name, effect, true, false);
            regHelper(wrap, Items.REDSTONE, wrapLong);
        }
        if(glowstone) { // strong power ...
            Holder<Potion> wrapLong = make("strong_" + name, effect, false, true);
            regHelper(wrap, Items.GLOWSTONE_DUST, wrapLong);
        }
        return wrap;//for further brewing
    }

    static void initialize() {
        // can use the return as another input
        // glowstone basis
        // leave mundane as failed
        // fermented spider eye as corrupt
        registerPair("test", MobEffects.POISON, Potions.THICK, Items.POTATO, true, true);
    }
}