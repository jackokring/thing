package uk.co.kring.thing;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

class ModMobEffects {
    // inner classes extending MobEffect (for potions)
    // resources/assets/<mod>/textures/mob_effect/<name>.png (18 by 18 pixel) for HUD display
    Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Thing.identify(name), effect);
    }

    static void initialize() {

    }
}
