package net.justonedev.mc.warpbooks;

import net.justonedev.mc.warpbooks.resourcepack.Resourcepack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarpBooks extends JavaPlugin {

    // Idea: Simple warp book
    
    private static WarpBooks singleton;
    public static final Material PLUGIN_MATERIAL = Material.BLACK_DYE;
    
    // Recipe: Paper + Ender pearl for pages
    
    public static NamespacedKey idKey, pageCraftkey;
    public static int LevelCostPerTeleport = 3;
    
    // Resource pack stuff
    static boolean useResourcePack = false;     // Todo make true once pack is set
    
    @Override
    public void onEnable() {
        singleton = this;
        
        idKey = new NamespacedKey(this, "id");
        pageCraftkey = new NamespacedKey(this, "pageCraftRecipeKey");
        // Plugin startup logic
        WarpBook warpBook = new WarpBook();
        WarpPage warpPage = new WarpPage();
        
        Resourcepack.defaultEnabled = useResourcePack;
        new Resourcepack(this, "", "");
        
        Bukkit.getPluginManager().registerEvents(warpBook, this);
        Bukkit.getPluginManager().registerEvents(warpPage, this);
        Bukkit.getPluginManager().registerEvents(new Crafting(), this);
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, Crafting::addCraftingRecipes, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static void log(String s) {
        singleton.getLogger().info(s);
    }
}
