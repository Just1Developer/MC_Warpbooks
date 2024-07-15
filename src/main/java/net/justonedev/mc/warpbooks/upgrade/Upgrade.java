package net.justonedev.mc.warpbooks.upgrade;

import net.justonedev.mc.warpbooks.Fragment;
import net.justonedev.mc.warpbooks.WarpBook;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Upgrade implements Listener {

	public static void openUpgrader(Player p) {
		Inventory upgrade = Bukkit.createInventory(null, 54, "Upgrade Warpbook");
		upgrade.setItem(BOOK_SLOT, EmptyBarSlot.emptyBook);
		upgrade.setItem(FRAGMENT_SLOT, EmptyBarSlot.emptyFragment);
		upgrade.setItem(XP_SLOT, new ItemStack(Material.EXPERIENCE_BOTTLE));
		for (int slot : NETHERITE_SLOTS) {
			upgrade.setItem(slot, EmptyBarSlot.emptyIngot);
		}
		p.openInventory(upgrade);
	}
	
	private static final int startRow = 9;

	private static final List<ClickType> RECOGNIZED_CLICK_TYPES = Arrays.asList(ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
	private static final int BOOK_SLOT = startRow + 4;
	private static final int XP_SLOT = startRow + 19;
	private static final List<Integer> NETHERITE_SLOTS = Arrays.asList(startRow + 21, startRow + 22, startRow + 23);
	private static final int FRAGMENT_SLOT = startRow + 25;

	private static final List<Integer> ALL_SLOTS = Arrays.asList(BOOK_SLOT, XP_SLOT, NETHERITE_SLOTS.get(0), NETHERITE_SLOTS.get(1), NETHERITE_SLOTS.get(2), FRAGMENT_SLOT);


	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String lowercase = e.getView().getTitle().toLowerCase();
		if (!lowercase.contains("warpbook") && !lowercase.contains("warp book")) return;

		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
		
		boolean clickedOwnInv = e.getRawSlot() != e.getSlot();

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
		boolean noCursor = cursor == null || cursor.getType() == Material.AIR;

		if (e.getClick().isShiftClick()) {
			if (!clickedOwnInv) {
				if (clicked == null) return;
				e.setCancelled(true);
				if (e.getRawSlot() == FRAGMENT_SLOT) {
					if (clicked.isSimilar(EmptyBarSlot.emptyFragment)) return;
					
					HashMap<Integer, ItemStack> remaining = e.getWhoClicked().getInventory().addItem(clicked);
					if (!remaining.isEmpty()) return;
					
					e.getInventory().setItem(FRAGMENT_SLOT, EmptyBarSlot.emptyFragment);
				} else if (e.getRawSlot() == BOOK_SLOT) {
					if (clicked.isSimilar(EmptyBarSlot.emptyBook)) return;
					
					HashMap<Integer, ItemStack> remaining = e.getWhoClicked().getInventory().addItem(clicked);
					if (!remaining.isEmpty()) return;
					
					e.getInventory().setItem(BOOK_SLOT, EmptyBarSlot.emptyBook);
				} else if (NETHERITE_SLOTS.contains(e.getRawSlot())) {
					if (clicked.isSimilar(EmptyBarSlot.emptyIngot)) return;
					
					HashMap<Integer, ItemStack> remaining = e.getWhoClicked().getInventory().addItem(clicked);
					if (!remaining.isEmpty()) return;
					
					e.getInventory().setItem(e.getRawSlot(), EmptyBarSlot.emptyIngot);
				}
				return;
			}
			e.setCancelled(true);
			boolean isIngot = isLegit(clicked, Material.NETHERITE_INGOT);
			boolean isFragment = !isIngot && Fragment.isCompleteFragment(clicked);
			boolean isBook = !isIngot && !isFragment && WarpBook.isWarpBook(clicked);

			// clicked == null is caught.

			if (!isIngot && !isFragment && !isBook) return;
			assert clicked != null;
			
			ItemStack setBarItem = new ItemStack(clicked);
			setBarItem.setAmount(1);
			
			if (isIngot) {
				// See if we can place it somewhere
				int amount = clicked.getAmount();
				
				for (int i = 0; i < NETHERITE_SLOTS.size() && amount > 0; ++i) {
					ItemStack it = e.getInventory().getItem(NETHERITE_SLOTS.get(i));
					if (!EmptyBarSlot.isEmptyIngot(it)) continue;
					
					amount--;
					e.getInventory().setItem(NETHERITE_SLOTS.get(i), setBarItem);
				}

				clicked.setAmount(amount);
				return;
			}
			
			// Fragment.
			// See if we have one placed
			if (isFragment) {
				ItemStack it = e.getInventory().getItem(FRAGMENT_SLOT);
				if (!EmptyBarSlot.isEmptyFragment(it)) return;
				e.getInventory().setItem(FRAGMENT_SLOT, setBarItem);
				clicked.setAmount(clicked.getAmount() - 1);
				return;
			}
			
			// Book.
			ItemStack it = e.getInventory().getItem(BOOK_SLOT);
			if (!EmptyBarSlot.isEmptyBook(it)) return;
			e.getInventory().setItem(BOOK_SLOT, setBarItem);
			clicked.setAmount(clicked.getAmount() - 1);
			return;
		}
		
		// Normal Left / Right Click
		
		// If cursor is valid item and the slot is empty, then allow it
		// Otherwise, don't
		// Except for if it's a pickup
		
		if (clickedOwnInv) return;
		
		/* L8r
		boolean isIngot = isLegit(clicked, Material.NETHERITE_INGOT);
		boolean isFragment = !isIngot && Fragment.isCompleteFragment(clicked);
		boolean isBook = !isIngot && !isFragment && WarpBook.isWarpBook(clicked);
		*/
		boolean isIngot = isLegit(cursor, Material.NETHERITE_INGOT);
		boolean isFragment = !isIngot && Fragment.isCompleteFragment(cursor);
		boolean isBook = !isIngot && !isFragment && WarpBook.isWarpBook(cursor);
		
		boolean isValidCursor = noCursor || (isIngot && NETHERITE_SLOTS.contains(e.getRawSlot()))
				|| (isFragment && e.getSlot() == FRAGMENT_SLOT) || (isBook && e.getSlot() == BOOK_SLOT);
		
		if (!isValidCursor) {
			e.setCancelled(true);
			return;
		}
		
		// We know a valid slot was clicked with a valid cursor
		
		// Todo real logic
		e.getInventory().setItem(e.getRawSlot(), EmptyBarSlot.emptyFragment);
	}

	private static boolean isLegit(ItemStack item, Material material) {
		if (item == null) return false;
		if (item.getType() != material) return false;
		if (!item.hasItemMeta()) return true;
		if (!Objects.requireNonNull(item.getItemMeta()).hasCustomModelData()) return true;
		return item.getItemMeta().getCustomModelData() == 0;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {

	}

}
