package net.justonedev.mc.warpbooks.upgrade;

import net.justonedev.mc.warpbooks.Fragment;
import net.justonedev.mc.warpbooks.WarpBook;
import net.justonedev.mc.warpbooks.WarpBooks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EmptyBarSlot {

	public static String itemName = "";
	public static final String itemLore = "§7Add here";
	public static final String fragmentName = "§81x " + Fragment.warpFragmentName;
	public static final String bookName = "§81x " + WarpBook.warpBookName;

	public static final String readyItemName = "§5Upgrade Warpbook now";
	public static final String notReadyItemName = "§8Upgrade Warpbook";
	public static final List<String> notReadyLore = Arrays.asList("§7Place all items and make", "§7sure you have enough xp");
	public static ItemStack emptyIngot, emptyFragment, emptyBook, readyItem, notReadyItem;

	public static void init() {
		
		String name = Objects.requireNonNull(new ItemStack(Material.NETHERITE_INGOT).getItemMeta()).getDisplayName();
		if (name.isEmpty()) name = "Netherite Ingot";
		itemName = "§81x " + name;
		
		emptyIngot = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = emptyIngot.getItemMeta();
		assert meta != null;
		meta.setDisplayName(itemName);
		meta.setLore(Collections.singletonList(itemLore));
		meta.setCustomModelData(8490);
		emptyIngot.setItemMeta(meta);
		
		emptyFragment = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		meta = emptyFragment.getItemMeta();
		assert meta != null;
		meta.setDisplayName(fragmentName);
		meta.setLore(Collections.singletonList(itemLore));
		meta.setCustomModelData(8491);
		emptyFragment.setItemMeta(meta);
		
		emptyBook = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		meta = emptyBook.getItemMeta();
		assert meta != null;
		meta.setDisplayName(bookName);
		meta.setLore(Collections.singletonList(itemLore));
		meta.setCustomModelData(8492);
		emptyBook.setItemMeta(meta);

		readyItem = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1);
		meta = readyItem.getItemMeta();
		assert meta != null;
		meta.setDisplayName(readyItemName);
		readyItem.setItemMeta(meta);

		notReadyItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
		meta = notReadyItem.getItemMeta();
		assert meta != null;
		meta.setDisplayName(notReadyItemName);
		meta.setLore(notReadyLore);
		notReadyItem.setItemMeta(meta);
	}
	
	public static boolean isEmptyIngot(ItemStack item) {
		if (item == null) return true;
		if (item.getType() == Material.AIR) return true;
		return item.isSimilar(emptyIngot);
	}
	
	public static boolean isEmptyFragment(ItemStack item) {
		if (item == null) return true;
		if (item.getType() == Material.AIR) return true;
		return item.isSimilar(emptyFragment);
	}
	
	public static boolean isEmptyBook(ItemStack item) {
		if (item == null) return true;
		if (item.getType() == Material.AIR) return true;
		return item.isSimilar(emptyBook);
	}

	public static ItemStack getReadyItem(boolean isReady) {
		if (isReady) return readyItem;
		return notReadyItem;
	}
	
}
