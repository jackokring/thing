package uk.co.kring.thing;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class SoundsProvider extends FabricSoundsProvider {
    public SoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider provider, SoundExporter soundExporter) {

    }

    @Override
    public String getName() {
        return "SoundsProvider";
    }
}
