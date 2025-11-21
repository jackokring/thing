package uk.co.kring.thing;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;

import java.util.ArrayList;

class ModPages {
    static Filterable<String> getTitle() {
        return Filterable.passThrough("The Book");
    }

    static String getAuthor() {
        return "Me";
    }

    static ArrayList<Filterable<Component>> getPages() {
        ArrayList<Filterable<Component>> pages = new ArrayList<>();

        return pages;
    }
}
