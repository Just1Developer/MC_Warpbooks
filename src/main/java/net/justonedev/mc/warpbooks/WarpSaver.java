package net.justonedev.mc.warpbooks;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class WarpSaver {
	
	/**
	 * Saves a list of warps to a file.
	 * @param uuid The UUID of the warpbook.
	 * @param warpContents The warps (inventory contents).
	 */
	public static void saveWarps(final String uuid, ItemStack[] warpContents) {
		File f = new File(WarpBooks.getWarpbookFolder(), uuid + ".yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		for (int i = 0; i < WarpBooks.WARP_SLOTS; i++) {
			ItemStack item = 9 + i >= warpContents.length ? null : warpContents[9 + i];
			
			if (item != null) {
				ItemMeta m = item.getItemMeta();
				if (m != null) {
					m.setLore(Collections.emptyList());
					item.setItemMeta(m);
				}
			}
			
			cfg.set("warps." + i, item);
		}
		try {
			cfg.save(f);
		} catch (IOException ignored) {
			WarpBooks.fatal("Failed to save warpbook " + uuid);
		}
	}
	
	/**
	 * Loads warps consecutively, ignoring empty slots. Used for creating the view inventory.
	 * @param uuid The warpbooks uuid.
	 * @return All warps for this warp book.
	 */
	public static List<ItemStack> loadWarps(final String uuid, Player player) {
		File f = new File(WarpBooks.getWarpbookFolder(), uuid + ".yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		List<ItemStack> warps = new ArrayList<>();
		for (int i = 0; i < WarpBooks.WARP_SLOTS; i++) {
			ItemStack itemstack = getPageItemStack(cfg.getItemStack("warps." + i), player);
			if (itemstack != null) warps.add(itemstack);
		}
		return warps;
	}
	
	/**
	 * Loads warps at the saved slots. Used for creating the modify inventory.
	 * @param uuid The warpbooks uuid.
	 * @return An array containing the entire slot inventory for the warp pages, 1:1.
	 */
	public static ItemStack[] loadWarpsInventoryExact(final String uuid) {
		File f = new File(WarpBooks.getWarpbookFolder(), uuid + ".yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		ItemStack[] warps = new ItemStack[WarpBooks.WARP_SLOTS + 9];
		
		for (int i = 0; i < WarpBooks.WARP_SLOTS; i++) {
			ItemStack itemstack = cfg.getItemStack("warps." + i);
			warps[i + 9] = itemstack;
		}
		
		for (int i = 0; i < 9; i++) {
			warps[i] = WarpBooks.PLACEHOLDER;
		}
		
		return warps;
	}
	
	private static ItemStack getPageItemStack(ItemStack item, Player p) {
		if (item == null) return null;
		ItemStack xp = new ItemStack(item);
		ItemMeta meta = xp.getItemMeta();
		
		if (meta != null) {
			int needed;
			Location loc = WarpPage.getLocation(item);
			if (loc != null && loc.getWorld() != null) {
				if (loc.getWorld().equals(p.getWorld())) needed = WarpBooks.LevelCostPerTeleport;
				else needed = WarpBooks.LevelCostPerTeleportWorlds;
			} else needed = -1;
			
			if (needed == 0) {
				meta.setLore(Collections.singletonList("§aFree"));
			} else if (needed > 0) {
				meta.setLore(Collections.singletonList((p.getLevel() >= needed ? "§a" : "§c") + p.getLevel() + "/" + needed + " Levels"));
			} else {
				meta.setLore(Collections.emptyList());
			}
		}
		
		xp.setItemMeta(meta);
		return xp;
	}
	
}
