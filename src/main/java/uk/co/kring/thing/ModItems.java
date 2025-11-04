package uk.co.kring.thing;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class ModItems {
    // hey fatty
    public static final FoodComponent EDIBLE = new FoodComponent.Builder().alwaysEdible().build();
    public static final ConsumableComponent OH_MY_TUMMY = ConsumableComponents.food()
            // The duration is in ticks, 20 ticks = 1 second
            .consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.POISON,
                    6 * 20, 1), 1.0f))
            .build();

    // all items in the reduce, reuse, recycle mindset
    public static final Item SUSPICIOUS_SUBSTANCE = register(
            "suspicious_substance", Item::new, new Item.Settings().food(EDIBLE, OH_MY_TUMMY),
            0.1f, 5);

    public static void initialize() {
        // Just say no to custom item groups as the botchy big G says
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup -> {
            itemGroup.add(SUSPICIOUS_SUBSTANCE);
            // ...
        });
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings,
                                float compostChance, int fuelSeconds) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Thing.identify(name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        if(compostChance > 0.0f)
            // Add the suspicious substance to the composting registry with a 30% chance of increasing the composter's level.
            CompostingChanceRegistry.INSTANCE.add(item, compostChance);

        // Add the suspicious substance to the registry of fuels, with a burn time of 30 seconds.
        // Remember, Minecraft deals with logical based-time using ticks.
        // 20 ticks = 1 second.
        if(fuelSeconds > 0)
            FuelRegistryEvents.BUILD.register((builder, context) ->
                    builder.add(item, fuelSeconds * 20));

        return item;
    }
}
