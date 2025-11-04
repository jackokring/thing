package uk.co.kring.thing;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MenuIntegration implements ModMenuApi {
    // an auto config later for client side options
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Return the screen here with the one you created from Cloth Config Builder
            return null;
        };
    }
}
