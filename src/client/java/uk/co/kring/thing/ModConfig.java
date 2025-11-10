package uk.co.kring.thing;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "thing")
class ModConfig implements ConfigData {
    @ConfigEntry.Category("chat")
    @ConfigEntry.Gui.Tooltip()
    boolean cryptEnabled = true;
    @ConfigEntry.Category("chat")
    @ConfigEntry.Gui.Tooltip()
    String key = "";

    @Override
    public void validatePostLoad() throws ValidationException {
        ConfigData.super.validatePostLoad();
        try {
            ThingClient.getKeyFromPassword(key.toCharArray());
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    /*
    @ConfigEntry.Gui.CollapsibleObject
    InnerStuff stuff = new InnerStuff();

    @ConfigEntry.Gui.Excluded
    InnerStuff invisibleStuff = new InnerStuff();

    static class InnerStuff {
        int a = 0;
        int b = 1;
    } */
}
