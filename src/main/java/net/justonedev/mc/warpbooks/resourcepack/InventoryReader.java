package net.justonedev.mc.warpbooks.resourcepack;

import org.bukkit.inventory.InventoryView;

public class InventoryReader {
    // For cross-version, since it's a different type depending on the version (class vs interface)
    public static String readTitle(InventoryView inventory) {
        return "";
    }
}
