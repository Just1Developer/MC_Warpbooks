package net.justonedev.mc.warpbooks;

import net.justonedev.mc.warpbooks.config.Config;
import net.justonedev.mc.warpbooks.resourcepack.CrossVersionResourceHandler;
import net.justonedev.mc.warpbooks.resourcepack.ResourceHandler;
import net.justonedev.mc.warpbooks.resourcepack.Resourcepack;
import net.justonedev.mc.warpbooks.upgrade.EmptyBarSlot;
import net.justonedev.mc.warpbooks.upgrade.Upgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class WarpBooks extends JavaPlugin {

    // Idea: Simple warp book
    
    private static WarpBooks singleton;
    public static final Material PLUGIN_MATERIAL = Material.BLACK_DYE, FRAGMENT_MATERIAL = Material.FLINT_AND_STEEL;
    
    public static ItemStack PLACEHOLDER, UPGRADE;
    
    public static boolean enableUpgrading = true;
    public static boolean enableTPSound = true;
    public static int LevelsToUpgrade = 150;
    
    public static boolean enableCostTP = true, enableCostTPCrossWorlds = true;
    
    public static NamespacedKey idKey, bookCraftKey, pageCraftkey, pageWorldKey, pageXKey, pageYKey, pageZKey, pagePitchKey, pageYawKey;
    public static int LevelCostPerTeleport = 3, LevelCostPerTeleportWorlds = 5;
    public static final float minimumTeleportCooldown = 0.8f, defaultTeleportCooldown = 1.6f;
    public static float teleportCooldown = defaultTeleportCooldown;
    
    public static final int WARP_SLOTS = 45;

    public static boolean isDevelopmentBuild = false;
    
    // Resource pack stuff
    public static boolean useResourcePack = true;

    public static final String DEFAULT_PACK_URL = "https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?rlkey=a9brt8t27u5ruesm4l0g62x0x&st=a5imgznf&dl=1";
    public static String packUrl = DEFAULT_PACK_URL;
    public static final String DEFAULT_PACK_HASH = "f94a69613c3fb6cd9b04eb9dd946976d468955dc";
    public static String packHash = DEFAULT_PACK_HASH;

    private static ResourceHandler resourceHandler;

    @Override
    public void onEnable() {
        singleton = this;
        resourceHandler = new CrossVersionResourceHandler(this);

        init();
        EmptyBarSlot.init();
        Config.initialize();

        if (this.getDescription().getVersion().toLowerCase().contains("dev")) {
            isDevelopmentBuild = true;
            Bukkit.getLogger().warning("[" + this.getDescription().getPrefix() + "] Please note that this is a developer build and could contain bugs or untested features.");
        }
        
        idKey = new NamespacedKey(this, "id");
        bookCraftKey = new NamespacedKey(this, "bookCraftRecipeKey");
        pageCraftkey = new NamespacedKey(this, "pageCraftRecipeKey");
        pageWorldKey = new NamespacedKey(this, "pageWorld");
        pageXKey = new NamespacedKey(this, "pageX");
        pageYKey = new NamespacedKey(this, "pageY");
        pageZKey = new NamespacedKey(this, "pageZ");
        pagePitchKey = new NamespacedKey(this, "pagePitch");
        pageYawKey = new NamespacedKey(this, "pageYaw");
        // Plugin startup logic
        WarpBook warpBook = new WarpBook();
        WarpPage warpPage = new WarpPage();
        Fragment fragment = new Fragment();
        Upgrade upgrade = new Upgrade();

        new Resourcepack(this, packUrl, packHash);
        
        Bukkit.getPluginManager().registerEvents(warpBook, this);
        Bukkit.getPluginManager().registerEvents(warpPage, this);
        Bukkit.getPluginManager().registerEvents(fragment, this);
        Bukkit.getPluginManager().registerEvents(upgrade, this);
        Bukkit.getPluginManager().registerEvents(new Crafting(), this);
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, Crafting::addCraftingRecipes, 10);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        WarpPage.closeAll();
    }

    public static ResourceHandler getResourceHandler() {
        return resourceHandler;
    }
    
    public static Logger getBukkitLogger() {
        return singleton.getLogger();
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
    
    public static File getFolder() {
        return singleton.getDataFolder();
    }
    
    private static void init() {
        PLACEHOLDER = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = PLACEHOLDER.getItemMeta();
        assert meta != null;
        meta.setDisplayName(" ");
        PLACEHOLDER.setItemMeta(meta);
    }
}
