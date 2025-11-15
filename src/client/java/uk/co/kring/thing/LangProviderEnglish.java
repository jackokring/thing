package uk.co.kring.thing;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ItemLike;
import java.util.concurrent.CompletableFuture;

// Section Marker § for formatting codes allowed -- Yellow Alert §6 shout out to the Aldgate East ASS Freedom peeps
// JSON may have some style keys on some resources
// colours before formatting
// 0-9a-f hex in colour nibble <bright><red><green><blue>
// ========== N.B. can use ThingClient.useSimpleText() for any MiniMessage style formatting =========
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
        super(dataOutput, "en_us", registryLookup);
    }

    void generateItem(TranslationBuilder builder, ItemLike item, String translation, String tooltip) {
        // automatically does blocks as ItemLike
        builder.add(item.asItem().getDescriptionId(), translation);
        if(tooltip != null)
            builder.add(ThingClient.tooltipKey(item), tooltip);
    }

    void generateConfig(TranslationBuilder builder, String key,  String translation, String tooltip) {
        builder.add("text.autoconfig." + Thing.MOD_ID + "." + key, translation);
        if(tooltip != null)
            builder.add("text.autoconfig." + Thing.MOD_ID + "." + key + ".@Tooltip", tooltip);
    }

    void generateAdvancement(TranslationBuilder builder, String key, String title, String description) {
        builder.add("advancement." + Thing.MOD_ID + "." + key + ".title", title);
        builder.add("advancement." + Thing.MOD_ID + "." + key + ".description", description);
    }

    void generateKeyMapping(TranslationBuilder builder, String key, String translation) {
        builder.add(ThingClient.keyName(key), translation);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider wrapperLookup, TranslationBuilder builder) {
        // chat provider type debugger on game messages
        builder.add(ThingClient.getTypeKey("type"), "{ \"translate\": \"%s\" }");
        builder.add(ThingClient.getTypeKey("encrypt"), "ENCRYPTION OFF");
        builder.add(ThingClient.getTypeKey("exception"), "Encryption exception: %s");
        builder.add(ThingClient.getTypeKey("talk.galactic"), "something encrypted");

        // ModMenu language adaptation (removed text from fabric.mod.json)
        builder.add("modmenu.nameTranslation." + Thing.MOD_ID, "Menu o' mods!");
        builder.add("modmenu.descriptionTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");
        builder.add("modmenu.summaryTranslation." + Thing.MOD_ID, "Menu o' mods ye installed matey!");

        // Items and Blocks
        generateItem(builder, ModItems.SUSPICIOUS_SUBSTANCE, "Suspicious Substance", "A powerful substance");
        generateItem(builder, ModBlocks.SUSPICIOUS_DIRT, "Suspicious Dirt", "Very, very suspicious dirt");

        // config
        generateConfig(builder, "title", "Thing Settings", null);
        generateConfig(builder, "category.chat", "Chat Settings", null);
        //options
        generateConfig(builder,"option.cryptEnabled", "Enable Chat Encryption", "Encrypt outgoing chat using a passphrase");
        generateConfig(builder, "option.key", "AES Cryptographic Key", "Set this to a passphrase to make a key");
        generateConfig(builder, "option.typeEnabled", "Enable Message Types", "Set to show internal translation keys");
        generateConfig(builder, "option.galacticEnabled", "Enable Undecoded Intercepts", "Set to show who sent an encrypted intercept");

        // advancements
        generateAdvancement(builder, "root", "Your First Suspicions", "Make a three by three");

        // keyBinds
        generateKeyMapping(builder, "spook", "Encrypt Chat Toggle");
        generateKeyMapping(builder, "types", "Show Type Prefix Toggle");
    }
}
