package net.justonedev.mc.warpbooks;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
			cfg.set("warps." + i, 9 + i >= warpContents.length ? null : warpContents[9 + i]);
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
	public static List<ItemStack> loadWarps(final String uuid) {
		File f = new File(WarpBooks.getWarpbookFolder(), uuid + ".yml");
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
		List<ItemStack> warps = new ArrayList<>();
		for (int i = 0; i < WarpBooks.WARP_SLOTS; i++) {
			ItemStack itemstack = cfg.getItemStack("warps." + i);
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
			warps[i] = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		}
		
		return warps;
	}
	
}
