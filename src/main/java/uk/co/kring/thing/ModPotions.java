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
    static Holder<Potion> make(String name, Holder<MobEffect> effect, Modify kind) {
        if(kind == Modify.BOTH) throw new IllegalArgumentException("Making potion kind error");
        return BuiltInRegistries.POTION.wrapAsHolder(Registry.register(
                BuiltInRegistries.POTION,
                Thing.identify(name),
                new Potion(name,
                        new MobEffectInstance(
                                effect,
                                // balancing, although 4800 isn't naturally produced
                                kind == Modify.LONGER ? 9600 : (kind == Modify.STRONGER ? 1800 : 3600),
                                kind == Modify.STRONGER ? 1 : 0))));
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

    static Holder<Potion> registerTriad(String name, Holder<MobEffect> effect,
                                       Holder<Potion> input, ItemLike add, Modify kind) {
        Holder<Potion> wrap = make(name, effect, Modify.NORMAL);
        regHelper(input, add, wrap);
        if(kind.includes(Modify.LONGER)) { // long duration ...
            Holder<Potion> wrapLong = make("long_" + name, effect, Modify.LONGER);
            regHelper(wrap, Items.REDSTONE, wrapLong);
        }
        if(kind.includes(Modify.STRONGER)) { // strong power ...
            Holder<Potion> wrapLong = make("strong_" + name, effect, Modify.STRONGER);
            regHelper(wrap, Items.GLOWSTONE_DUST, wrapLong);
        }
        return wrap;//for further brewing
    }

    static void corrupt(Holder<Potion> given, Holder<Potion> made) {
        regHelper(given, Items.FERMENTED_SPIDER_EYE, made);
    }

    static void initialize() {
        // can use the return as another input
        // glowstone basis
        // leave mundane as failed
        // fermented spider eye as corrupt
        registerTriad("test", MobEffects.POISON, Potions.THICK, Items.POTATO, Modify.BOTH);
    }

    enum Modify {
        NORMAL,// just normal
        LONGER,// also longer
        STRONGER,// also stronger
        BOTH;// all three includes both "regular" modifications
        boolean includes(Modify thing) {
            return switch(this) {
                case NORMAL -> thing == NORMAL;
                case LONGER -> thing == LONGER || thing == NORMAL;
                case STRONGER -> thing == STRONGER || thing == NORMAL;
                case BOTH -> thing == LONGER || thing == STRONGER || thing == NORMAL;
                //default -> throw new IllegalArgumentException("Making potion kind error");
            };
        }
    }
}