package net.justonedev.mc.warpbooks;

import net.justonedev.mc.warpbooks.resourcepack.Resourcepack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WarpBooks extends JavaPlugin {

    // Idea: Simple warp book
    
    private static WarpBooks singleton;
    public static final Material PLUGIN_MATERIAL = Material.BLACK_DYE, FRAGMENT_MATERIAL = Material.FLINT_AND_STEEL;
    
    // Recipe: Paper + Ender pearl for pages
    
    public static NamespacedKey idKey, pageCraftkey, pageWorldKey, pagePitchKey, pageYawKey;
    public static int LevelCostPerTeleport = 3, LevelCostPerTeleportWorlds = 5;
    public static float minimumTeleportCooldown = 0.8f, defaultTeleportCooldown = 2.8f;
    
    public static final int WARP_SLOTS = 45;
    
    // Resource pack stuff
    static boolean useResourcePack = false;     // Todo make true once pack is set
    
    @Override
    public void onEnable() {
        singleton = this;
        
        idKey = new NamespacedKey(this, "id");
        pageCraftkey = new NamespacedKey(this, "pageCraftRecipeKey");
        pageWorldKey = new NamespacedKey(this, "pageWorld");
        pagePitchKey = new NamespacedKey(this, "pagePitch");
        pageYawKey = new NamespacedKey(this, "pageYaw");
        // Plugin startup logic
        WarpBook warpBook = new WarpBook();
        WarpPage warpPage = new WarpPage();
        Fragment fragment = new Fragment();
        
        Resourcepack.defaultEnabled = useResourcePack;
        new Resourcepack(this, "", "");
        
        Bukkit.getPluginManager().registerEvents(warpBook, this);
        Bukkit.getPluginManager().registerEvents(warpPage, this);
        Bukkit.getPluginManager().registerEvents(fragment, this);
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
    
    public static void fatal(String s) {
        singleton.getLogger().severe(s);
    }
    
    public static File getWarpbookFolder() {
        File f = new File(singleton.getDataFolder() + "/warpbooks");
        if (!f.exists()) f.mkdirs();
        return f;
    }
}
