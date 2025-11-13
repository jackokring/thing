package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

class AdvancementsProvider extends FabricAdvancementProvider {
    protected AdvancementsProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataGenerator, registryLookup);
    }

    String named(String name) {
        return Thing.MOD_ID + "/" + name;
    }

    AdvancementHolder advancement(Consumer<AdvancementHolder> consumer, String name, AdvancementHolder parent, ItemLike icon,
                                  AdvancementType type, Criterion<?> trigger) {
        Advancement.Builder ab = Advancement.Builder.advancement();
        if(parent != null) ab = ab.parent(parent);
        ab = ab.display(
                icon,
                Component.translatable("advancement." + Thing.MOD_ID + "." + name + ".title"),
                Component.translatable("advancement." + Thing.MOD_ID + "." + name + ".description"),
                parent != null ? null :
                        ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png"),
                type,
                true, true, false
        ).addCriterion(icon.asItem().getDescriptionId(), trigger);// easy ID for criterion progression
        return ab.save(consumer, named(name));
    }
    
    @Override
    public void generateAdvancement(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
        // a simplified chain based on unique icons for criterion names and parent assignment for tree
        // flexible enough for most things I'd do
        AdvancementHolder root = advancement(consumer, "root", null, ModItems.SUSPICIOUS_SUBSTANCE,
                AdvancementType.TASK, InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.SUSPICIOUS_SUBSTANCE));
    }
}
