package uk.co.kring.thing;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;

// Section Marker § for formatting codes allowed
// JSON may have some style keys on some resources
// colours before formatting
// 0-9a-f hex in colour nibble <bright><red><green><blue>
// g-j
// k obfuscated
// l bold
// m strikethrough
// n underline
// o italic
// p-q
// r reset
// s-z

class LangProviderEnglish extends FabricLanguageProvider {
    protected LangProviderEnglish(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_gb", registryLookup);
    }

    void generateItem(TranslationBuilder builder, ItemLike item, String translation, String tooltip) {
        builder.add(item.asItem().getDescriptionId(), translation);
        if(tooltip != null)
            builder.add(ThingClient.tooltipKey(item), tooltip);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder builder) {
        // ModMenu language adaptation
        builder.add("modmenu.nameTranslation." + Thing.MOD_ID, "Menu o' mods!");
        builder.add("modmenu.descriptionTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");
        builder.add("modmenu.summaryTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");

        // Items and Blocks
        generateItem(builder, ModItems.SUSPICIOUS_SUBSTANCE, "Suspicious Substance", "A powerful substance");
        generateItem(builder, ModBlocks.SUSPICIOUS_DIRT, "Suspicious Dirt", "Very, very suspicious dirt");
    }

    Component useSimpleText(String in) {
        // V1 deprecated
        //@SuppressWarnings("all")
        NodeParser parser = NodeParser.merge(TagParser.SIMPLIFIED_TEXT_FORMAT,
                Placeholders.DEFAULT_PLACEHOLDER_PARSER);
        // toText accepts PlaceholderContext and ParserContext etc.
        // So this is apparently how to get a V1 MiniMessage format with placeholder insertions
        // The documentation appears out of date, and well, who knows?

        // I suppose it's not helped by the source not being compiled against Mojang base
        // and so the .class file has to be decompiled with parchment too
        // but, yes, that should be it for %placeholder% as default
        return parser.parseNode(in).toText();
    }
}
