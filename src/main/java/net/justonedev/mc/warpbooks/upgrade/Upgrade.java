package net.justonedev.mc.warpbooks.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Upgrade implements Listener {

	public static void openUpgrader(Player p) {
		Inventory upgrade = Bukkit.createInventory(null, 54, "Upgrade Warpbook");
		p.openInventory(upgrade);
	}

	private static final List<ClickType> RECOGNIZED_CLICK_TYPES = Arrays.asList(ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
	private static final int BOOK_SLOT = 22;
	private static final int XP_SLOT = 37;
	private static final List<Integer> NETHERITE_SLOTS = Arrays.asList(39, 40, 41);
	private static final int FRAGMENT_SLOT = 43;

	private static final List<Integer> ALL_SLOTS = Arrays.asList(BOOK_SLOT, XP_SLOT, 39, 40, 41, FRAGMENT_SLOT);


	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String lowercase = e.getView().getTitle().toLowerCase();
		if (!lowercase.contains("warpbook") && !lowercase.contains("warp book")) return;

		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}

		boolean clickedOwnInv = false;

		if (!RECOGNIZED_CLICK_TYPES.contains(e.getClick())) {
			if(!clickedOwnInv || e.getClick().isShiftClick()) e.setCancelled(true);
			return;
		}

		if (!clickedOwnInv && !ALL_SLOTS.contains(e.getRawSlot())) {
			e.setCancelled(true);
			return;
		}

		// The default (empty) values, when nothing is there:
		// Clicked: null
		// Cursor: AIR
		// HotbarItem: null
		ItemStack cursor = e.getCursor();
		ItemStack clicked = e.getCurrentItem();
		boolean clickedOwnInv = e.getRawSlot() != e.getSlot();
		boolean noCursor = cursor == null || cursor.getType() == Material.AIR;

		if (clickedOwnInv && e.getClick().isShiftClick()) {
			e.setCancelled(true);
			boolean isIngot = isLegit(clicked, Material.NETHERITE_INGOT);
			boolean isFragment = isIngot ? false : Fragment.isCompleteFragment(clicked);

			// clicked == null is caught.

			if (!isIngot && !isFragment) return;
			if (isIngot) {
				// See if we can place it somewhere
				int amount = clicked.getAmount();

				for (int i = 0; i < 3 && amount > 0; ++i) {

				}

				clicked.setAmount(amount);
				return;
			}
			// See if we have one placed
		}
	}

	private static boolean isLegit(ItemStack item, Material material) {
		if (item == null) return false;
		if (item.getType() != material) return false;
		if (!item.hasItemMeta()) return true;
		if (!item.getItemMeta().hasCustomModelData()) return true;
		return item.getItemMeta().getCustomModelData() == 0;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {

	}

}
