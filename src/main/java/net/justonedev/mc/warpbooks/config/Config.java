package net.justonedev.mc.warpbooks.config;

import net.justonedev.mc.warpbooks.Fragment;
import net.justonedev.mc.warpbooks.WarpBooks;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Config {

	private static final String KEY_RESOURCE_PACK_URL = "Resource Pack (only change if you know what you're doing)";
	private static final String KEY_RESOURCE_PACK_HASH = "Resource Pack File Hash";

	public static void loadConfig() {
		File f = new File(WarpBooks.getFolder(), "config.yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		
		if (!f.exists()) {
			// Default Values are already set
			cfg.set("Teleport Cooldown Time in Seconds", WarpBooks.defaultTeleportCooldown);
			cfg.set("Enable cost for same-world teleports", WarpBooks.enableCostTP);
			cfg.set("Enable cost for cross-world teleports", WarpBooks.enableCostTPCrossWorlds);
			cfg.set("Level cost per same-world teleport", WarpBooks.LevelCostPerTeleport);
			cfg.set("Level cost per cross-world teleport", WarpBooks.LevelCostPerTeleportWorlds);
			cfg.set("Enable Upgrading Books", WarpBooks.enableUpgrading);
			cfg.set("Level cost to upgrade", WarpBooks.LevelsToUpgrade);
			cfg.set("Fragment Spawn Chance in Percent", (int) (Fragment.CHANCE * 100));
			cfg.set("Enable Resource Pack", WarpBooks.useResourcePack);
			cfg.set(KEY_RESOURCE_PACK_URL, WarpBooks.DEFAULT_PACK_URL);
			cfg.set(KEY_RESOURCE_PACK_HASH, WarpBooks.DEFAULT_PACK_HASH);
			cfg.set("Enable Sound when Teleporting", WarpBooks.enableTPSound);
			saveCfg(f, cfg);
			return;
		}
		
		Map<String, Object> updateThese = new HashMap<>();
		
		WarpBooks.teleportCooldown = getOrDefaultFloat("Teleport Cooldown Time in Seconds", cfg, updateThese, WarpBooks.defaultTeleportCooldown);
		WarpBooks.enableCostTP = getOrDefaultBoolean("Enable cost for same-world teleports", cfg, updateThese, WarpBooks.enableCostTP);
		WarpBooks.enableCostTPCrossWorlds = getOrDefaultBoolean("Enable cost for cross-world teleports", cfg, updateThese, WarpBooks.enableCostTPCrossWorlds);
		WarpBooks.LevelCostPerTeleport = getOrDefaultInteger("Level cost per same-world teleport", cfg, updateThese, WarpBooks.LevelCostPerTeleport);
		WarpBooks.LevelCostPerTeleportWorlds = getOrDefaultInteger("Level cost per cross-world teleport", cfg, updateThese, WarpBooks.LevelCostPerTeleportWorlds);
		WarpBooks.enableUpgrading = getOrDefaultBoolean("Enable Upgrading Books", cfg, updateThese, WarpBooks.enableUpgrading);
		WarpBooks.LevelsToUpgrade = getOrDefaultInteger("Level cost to upgrade", cfg, updateThese, WarpBooks.LevelsToUpgrade);
		WarpBooks.useResourcePack = getOrDefaultBoolean("Enable Resource Pack", cfg, updateThese, WarpBooks.useResourcePack);
		WarpBooks.enableTPSound = getOrDefaultBoolean("Enable Sound when Teleporting", cfg, updateThese, WarpBooks.enableTPSound);
		WarpBooks.packUrl = getOrDefaultURL(cfg, updateThese);
		WarpBooks.packHash = getOrDefaultHash(cfg, updateThese);

		if (!WarpBooks.enableCostTP) WarpBooks.LevelCostPerTeleport = -1;
		if (!WarpBooks.enableCostTPCrossWorlds) WarpBooks.LevelCostPerTeleportWorlds = -1;
		
		float chance = getOrDefaultFloat("Fragment Spawn Chance in Percent", cfg, updateThese, (int) (Fragment.CHANCE * 100)) / 100;
		if (chance < 0) chance = 0;
		else if (chance > 1) chance = 1;
		Fragment.CHANCE = chance;
		
		if (WarpBooks.teleportCooldown < WarpBooks.minimumTeleportCooldown) {
			WarpBooks.getBukkitLogger().warning(String.format("Teleport Cooldown was below allowed minimum of %f ms. The value will be set to %f seconds.", WarpBooks.teleportCooldown, WarpBooks.teleportCooldown));
			WarpBooks.teleportCooldown = WarpBooks.minimumTeleportCooldown;
		}
		
		if (WarpBooks.enableCostTP && WarpBooks.LevelCostPerTeleport < 0) {
			WarpBooks.getBukkitLogger().warning("Level cost per same-world teleport was below 0 The value will be set to 0.");
			WarpBooks.LevelCostPerTeleport = 0;
		}
		
		if (WarpBooks.enableCostTPCrossWorlds && WarpBooks.LevelCostPerTeleportWorlds < 0) {
			WarpBooks.getBukkitLogger().warning("Level cost per same-world teleport was below 0 The value will be set to 0.");
			WarpBooks.LevelCostPerTeleportWorlds = 0;
		}
		
		if (updateThese.isEmpty()) return;
		cfg = YamlConfiguration.loadConfiguration(f);	// Reload config
		for (Map.Entry<String, Object> entry : updateThese.entrySet()) {
			cfg.set(entry.getKey(), entry.getValue());
		}
		saveCfg(f, cfg);
	}
	
	private static boolean getOrDefaultBoolean(String key, YamlConfiguration cfg, Map<String, Object> updateThese, boolean defaultValue) {
		if (cfg.isSet(key)) return cfg.getBoolean(key);
		updateThese.put(key, defaultValue);
		return defaultValue;
	}
	
	private static int getOrDefaultInteger(String key, YamlConfiguration cfg, Map<String, Object> updateThese, int defaultValue) {
		if (cfg.isSet(key)) return cfg.getInt(key);
		updateThese.put(key, defaultValue);
		return defaultValue;
	}
	
	private static float getOrDefaultFloat(String key, YamlConfiguration cfg, Map<String, Object> updateThese, float defaultValue) {
		if (cfg.isSet(key)) return (float) cfg.getDouble(key);	// Yeah, I know
		updateThese.put(key, defaultValue);
		return defaultValue;
	}

	private static String getOrDefaultURL(YamlConfiguration cfg, Map<String, Object> updateThese) {
		if (cfg.isSet(Config.KEY_RESOURCE_PACK_URL)) {
			String url = cfg.getString(Config.KEY_RESOURCE_PACK_URL);
            return fixOrDefaultUrl(url);
		}
		updateThese.put(Config.KEY_RESOURCE_PACK_URL, WarpBooks.DEFAULT_PACK_URL);
		return WarpBooks.DEFAULT_PACK_URL;
	}

    public static String fixOrDefaultUrl(String url) {
        if (url == null) return WarpBooks.DEFAULT_PACK_URL;
        if (!url.startsWith("http")) return WarpBooks.DEFAULT_PACK_URL;
        if (!url.startsWith("https")) url = url.replaceFirst("http", "https");
        if (!url.matches("^https://www\\.dropbox\\.com/.+$")) return url;
        if (url.endsWith("dl=0")) return url.substring(0, url.length() - 4) + "dl=1";
        return url;
    }

	private static String getOrDefaultHash(YamlConfiguration cfg, Map<String, Object> updateThese) {
		if (cfg.isSet(Config.KEY_RESOURCE_PACK_HASH)) {
			String hash = cfg.getString(Config.KEY_RESOURCE_PACK_HASH);
			return hash == null ? WarpBooks.DEFAULT_PACK_HASH : hash;
		}
		updateThese.put(Config.KEY_RESOURCE_PACK_HASH, WarpBooks.DEFAULT_PACK_HASH);
		return WarpBooks.DEFAULT_PACK_HASH;
	}
	
	private static void saveCfg(File f, YamlConfiguration cfg) {
		try {
			cfg.save(f);
		} catch (IOException ignored) {}
	}

}
