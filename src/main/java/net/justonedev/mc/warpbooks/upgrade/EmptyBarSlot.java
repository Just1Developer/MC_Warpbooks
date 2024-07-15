package net.justonedev.mc.warpbooks.upgrade;

import net.justonedev.mc.warpbooks.WarpBooks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Objects;

public class EmptyBarSlot {

	public static final String itemName = "§81x " + Objects.requireNonNull(new ItemStack(Material.NETHERITE_INGOT).getItemMeta()).getItemName();
	public static final String itemLore = "§7Click to place";
	public static ItemStack warpPage;

	public static void init() {
		warpPage = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = warpPage.getItemMeta();
		assert meta != null;
		meta.setDisplayName(itemName);
		meta.setLore(Collections.singletonList(itemLore));
		meta.setCustomModelData(8496);
		warpPage.setItemMeta(meta);
	}
	
}
