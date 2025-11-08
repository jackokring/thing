package uk.co.kring.thing;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.ItemLike;
import java.util.function.Function;

class ModItems {
    // hey fatty
    static final FoodProperties EDIBLE = new FoodProperties.Builder().alwaysEdible().build();
    static final Consumable OH_MY_TUMMY = Consumables.defaultFood()
            // The duration is in ticks, 20 ticks = 1 second
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.POISON,
                    6 * 20, 1), 1.0f))
            .build();

    // all items in the reduce, reuse, recycle mindset
    static final Item SUSPICIOUS_SUBSTANCE = register(
            "suspicious_substance", Item::new, new Item.Properties().food(EDIBLE, OH_MY_TUMMY));

    static void initialize() {
        // Just say no to custom item groups as the botchy big G says
        compostAndFuel(SUSPICIOUS_SUBSTANCE, 0.1f, 5, CreativeModeTabs.INGREDIENTS);
        compostAndFuel(ModBlocks.SUSPICIOUS_DIRT, 0.1f * 9, 5 * 9, CreativeModeTabs.NATURAL_BLOCKS);
    }

    static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Thing.identify(name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    static void compostAndFuel(ItemLike item,
                                      float compostChance, int fuelSeconds, ResourceKey<CreativeModeTab> whereGUI) {
        ItemGroupEvents.modifyEntriesEvent(whereGUI).register(group -> {
            group.accept(item);
        });
        if(compostChance > 0.0f)
            // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
            CompostingChanceRegistry.INSTANCE.add(item, compostChance);

        // Add the suspicious substance to the registry of fuels, with a burn time of 30 seconds.
        // Remember, Minecraft deals with logical based-time using ticks.
        // 20 ticks = 1 second.
        if(fuelSeconds > 0)
            FuelRegistryEvents.BUILD.register((builder, context) ->
                    builder.add(item, fuelSeconds * 20));

    }
}
