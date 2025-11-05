package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LangProviderEnglish extends FabricLanguageProvider {
    protected LangProviderEnglish(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_uk", registryLookup);
    }

    void generateItem(TranslationBuilder builder, ItemConvertible item, String translation, String tooltip) {
        builder.add(item.asItem().getTranslationKey(), translation);
        if(tooltip != null)
            builder.add(ThingClient.tooltipKey(item), tooltip);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        // ModMenu language adaptation
        builder.add("modmenu.nameTranslation." + Thing.MOD_ID, "Menu o' mods!");
        builder.add("modmenu.descriptionTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");
        builder.add("modmenu.summaryTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");

        // Items and Blocks
        generateItem(builder, ModItems.SUSPICIOUS_SUBSTANCE, "Suspicious Substance", "A powerful substance");
        generateItem(builder, ModBlocks.SUSPICIOUS_DIRT, "Suspicious Dirt", "Very, very Suspicious dirt");
    }
}
