package net.justonedev.mc.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.UUID;

public class WarpBook implements Listener {
	
	private static ItemStack warpBook, opWarpBook;
	
	private static void init() {
		warpBook = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = warpBook.getItemMeta();
		assert meta != null;
		meta.setDisplayName("§bWarp book");
		meta.setCustomModelData(8498);
		warpBook.setItemMeta(meta);
		
		opWarpBook = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		meta = opWarpBook.getItemMeta();
		assert meta != null;
		meta.setDisplayName("§b§lWarp book");
		meta.setCustomModelData(8497);
		opWarpBook.setItemMeta(meta);
	}
	
	public static boolean isWarpBook(ItemStack item) {
		return isWarpItem(item, warpBook);
	}
	
	public static boolean isOPWarpBook(ItemStack item) {
		return isWarpItem(item, opWarpBook);
	}
	
	static boolean isWarpItem(ItemStack item, ItemStack warpItem) {
		if (item == null || item.getType() != warpItem.getType()) return false;
		if (item.getItemMeta() == null) return false;
		if (!item.getItemMeta().hasCustomModelData()) return false;
		return item.getItemMeta().getCustomModelData() == Objects.requireNonNull(warpItem.getItemMeta()).getCustomModelData();
	}
	
	private static int getWarpBookLevel(ItemStack item) {
		if (isWarpBook(item)) return 1;
		if (isOPWarpBook(item)) return 2;
		return 0;
	}
	
	public static ItemStack getNewWarpBook() {
		ItemStack book = new ItemStack(warpBook);
		ItemMeta meta = book.getItemMeta();
		assert meta != null;
		meta.getPersistentDataContainer().set(WarpBooks.idKey, PersistentDataType.STRING, UUID.randomUUID().toString());
		book.setItemMeta(meta);
		return book;
	}
	
	// 100 xp level + like 1-3 netherite + possession of the dragon egg = upgraded warp book
	
	public static ItemStack getUpgraded(ItemStack warpBook) {
		if (!isWarpBook(warpBook)) return warpBook;
		ItemStack newBook = new ItemStack(warpBook);
		ItemMeta meta = newBook.getItemMeta();
		assert meta != null;
		meta.setCustomModelData(Objects.requireNonNull(opWarpBook.getItemMeta()).getCustomModelData());
		if (meta.hasDisplayName() && meta.getDisplayName().startsWith("§b")) {
			meta.setDisplayName("§b§l" + meta.getDisplayName().substring(2));
		} else {
			meta.setDisplayName("§b§l" + meta.getDisplayName());
		}
		newBook.setItemMeta(meta);
		return newBook;
	}
	
	public WarpBook() {
		init();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		ItemStack book = e.getItem();
		int level = getWarpBookLevel(book);
		if (level == 0) return;
		Bukkit.broadcastMessage("Warpbook! LeveL: §e" + level);
		assert book != null;
		// Yes, it's long. It's basically asserted that warp books have thad id tag, so we get it and parse the UUID
		UUID uuid;
		try {
			uuid = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(book.getItemMeta())
					.getPersistentDataContainer().get(WarpBooks.idKey, PersistentDataType.STRING)));
		} catch (Exception ignored) {
			return;
		}
		
		Bukkit.broadcastMessage("uuid: §b" + uuid.toString());
	}
	
	private void openWarpBook(Player player) {
	
	}
	
	private void openWarpBookEditor(Player player) {
	
	}
	
}
