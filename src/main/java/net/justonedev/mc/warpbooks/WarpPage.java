package net.justonedev.mc.warpbooks;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WarpPage implements Listener {

	public static ItemStack warpPage;
	
	public static void init() {
		warpPage = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = warpPage.getItemMeta();
		assert meta != null;
		meta.setDisplayName("§fWarp page");
		meta.setCustomModelData(8499);
		warpPage.setItemMeta(meta);
	}
	
	public static boolean isWarpPage(ItemStack item) {
		return WarpBook.isWarpItem(item, warpPage);
	}
	
	public WarpPage() {
		init();
	}
	
}
