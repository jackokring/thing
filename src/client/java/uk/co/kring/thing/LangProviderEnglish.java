package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LangProviderEnglish extends FabricLanguageProvider {
    protected LangProviderEnglish(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_uk", registryLookup);
    }

    void generateItem(TranslationBuilder builder, Item item, String translation, String tooltip) {
        builder.add(item.getTranslationKey(), translation);
        if(tooltip != null)
            builder.add(item.getTranslationKey() + ".tooltip", tooltip);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder builder) {
        builder.add("modmenu.nameTranslation." + Thing.MOD_ID, "Menu o' mods!");
        builder.add("modmenu.descriptionTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");
        builder.add("modmenu.summaryTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");
        generateItem(builder, ModItems.SUSPICIOUS_SUBSTANCE, "Suspicious Substance", "A powerful substance");
    }
}
