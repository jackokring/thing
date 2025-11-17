package uk.co.kring.thing;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

class ModMobEffects {
    // inner classes extending MobEffect (for potions)
    Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Thing.identify(name), effect);
    }

    static void initialize() {

    }
}
