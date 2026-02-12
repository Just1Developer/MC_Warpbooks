package net.justonedev.mc.warpbooks.upgrade;

import net.justonedev.mc.warpbooks.Fragment;
import net.justonedev.mc.warpbooks.WarpBook;
import net.justonedev.mc.warpbooks.WarpBooks;
import net.justonedev.mc.warpbooks.resourcepack.ItemModelHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Upgrade implements Listener {

	private static final String UpgradeTitle = "Upgrade Warpbook";
	private static final String UpgradedTitle = "Warpbook upgraded";
	
	public static void openUpgrader(Player p) {
		Inventory upgrade = Bukkit.createInventory(null, 54, UpgradeTitle);
		upgrade.setItem(BOOK_SLOT, EmptyBarSlot.emptyBook);
		upgrade.setItem(FRAGMENT_SLOT, EmptyBarSlot.emptyFragment);
		upgrade.setItem(XP_SLOT, getXP(p));
		for (int slot : NETHERITE_SLOTS) {
			upgrade.setItem(slot, EmptyBarSlot.emptyIngot);
		}
		p.openInventory(upgrade);
	}
	
	public static void openUpgraded(Player p, ItemStack upgraded) {
		Inventory upgrade = Bukkit.createInventory(null, 27, UpgradedTitle);
		upgrade.setItem(13, upgraded);
		p.openInventory(upgrade);
	}
	
	private static final int startRow = 9;

	private static final List<ClickType> RECOGNIZED_CLICK_TYPES = Arrays.asList(ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
	private static final int BOOK_SLOT = startRow + 4;
	private static final int XP_SLOT = startRow + 19;
	private static final List<Integer> NETHERITE_SLOTS = Arrays.asList(startRow + 21, startRow + 22, startRow + 23);
	private static final List<Integer> CONFIRM_SLOTS = Arrays.asList(startRow + 29, startRow + 30, startRow + 31, startRow + 32, startRow + 33);
	private static final int FRAGMENT_SLOT = startRow + 25;

	// XP Slot is just a visual info, nothing clickable.
	private static final List<Integer> ALL_SLOTS = Arrays.asList(BOOK_SLOT, NETHERITE_SLOTS.get(0), NETHERITE_SLOTS.get(1), NETHERITE_SLOTS.get(2), FRAGMENT_SLOT);
	
	
	@EventHandler
	@SuppressWarnings("deprecation")	// Our application is okay.
	public void onInventoryClick(InventoryClickEvent e) {
		if (!e.getView().getTitle().equals(UpgradeTitle)) return;

		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
		
		boolean clickedOwnInv = e.getRawSlot() != e.getSlot();

		if (!RECOGNIZED_CLICK_TYPES.contains(e.getClick())) {
			if(!clickedOwnInv || e.getClick().isShiftClick()) e.setCancelled(true);
			return;
		}

		if (CONFIRM_SLOTS.contains(e.getRawSlot()) && e.getClick() == ClickType.LEFT) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (!EmptyBarSlot.getReadyItem(true).isSimilar(e.getCurrentItem())) return;
			ItemStack upgraded = WarpBook.getUpgraded(e.getView().getTopInventory().getItem(BOOK_SLOT));
			e.getView().getTopInventory().clear();
			
			p.setLevel(p.getLevel() - WarpBooks.LevelsToUpgrade);
			openUpgraded(p, upgraded);
			p.playSound(e.getWhoClicked().getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 0.7f, 1.0f);
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
				updateReadyStatus(e.getView().getTopInventory());
				return;
			}
			e.setCancelled(true);
			boolean isIngot = isNormalNetheriteBar(clicked);
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
				updateReadyStatus(e.getView().getTopInventory());
				return;
			}
			
			// Fragment.
			// See if we have one placed
			if (isFragment) {
				ItemStack it = e.getInventory().getItem(FRAGMENT_SLOT);
				if (!EmptyBarSlot.isEmptyFragment(it)) return;
				e.getInventory().setItem(FRAGMENT_SLOT, setBarItem);
				clicked.setAmount(clicked.getAmount() - 1);
				updateReadyStatus(e.getView().getTopInventory());
				return;
			}
			
			// Book.
			ItemStack it = e.getInventory().getItem(BOOK_SLOT);
			if (!EmptyBarSlot.isEmptyBook(it)) return;
			e.getInventory().setItem(BOOK_SLOT, setBarItem);
			clicked.setAmount(clicked.getAmount() - 1);
			updateReadyStatus(e.getView().getTopInventory());
			return;
		}
		
		// Normal Left / Right Click
		
		// If cursor is valid item and the slot is empty, then allow it
		// Otherwise, don't
		// Except for if it's a pickup
		
		if (clickedOwnInv) return;
		
		boolean isIngot = isNormalNetheriteBar(cursor);
		boolean isFragment = !isIngot && Fragment.isCompleteFragment(cursor);
		boolean isBook = !isIngot && !isFragment && WarpBook.isWarpBook(cursor);
		
		boolean isValidCursor = noCursor || (isIngot && NETHERITE_SLOTS.contains(e.getRawSlot()))
				|| (isFragment && e.getSlot() == FRAGMENT_SLOT) || (isBook && e.getSlot() == BOOK_SLOT);
		
		if (!isValidCursor) {
			e.setCancelled(true);
			return;
		}
		
		boolean isClickedSlotEmpty = clicked == null || (clicked.isSimilar(EmptyBarSlot.emptyIngot))
				|| (clicked.isSimilar(EmptyBarSlot.emptyFragment)) || (clicked.isSimilar(EmptyBarSlot.emptyBook));
		
		// We know a valid slot was clicked with a valid cursor
		
		if (isClickedSlotEmpty) {
			e.setCancelled(true); // prevent pickup of placeholders
			if (noCursor) return;
			ItemStack newItem = new ItemStack(cursor);
			newItem.setAmount(1);
			
			e.getInventory().setItem(e.getRawSlot(), newItem);	// Validity is confirmed beforehand.
			cursor.setAmount(cursor.getAmount() - 1);
			updateReadyStatus(e.getView().getTopInventory());
			return;
		}
		
		// Clicked slot is not empty.
		// If we have a cursor
		if (noCursor) {
			e.setCancelled(true);
			e.setCursor(clicked);	// This is marked as deprecated because it can cause inconsistencies. Not a problem in this case.
			
			if(NETHERITE_SLOTS.contains(e.getRawSlot())) e.getInventory().setItem(e.getRawSlot(), EmptyBarSlot.emptyIngot);
			else if(e.getSlot() == FRAGMENT_SLOT) e.getInventory().setItem(e.getRawSlot(), EmptyBarSlot.emptyFragment);
			else e.getInventory().setItem(e.getRawSlot(), EmptyBarSlot.emptyBook);
			updateReadyStatus(e.getView().getTopInventory());
			return;
		}
		
		// If they would stack, cancel. If they can't stack, it won't make a difference since they're similar
		if (cursor.isSimilar(clicked)) e.setCancelled(true);
		updateReadyStatus(e.getView().getTopInventory());
		// Otherwise just let the valid swap happen
	}

	private static boolean isNormalNetheriteBar(ItemStack item) {
		if (item == null) return false;
		if (item.getType() != Material.NETHERITE_INGOT) return false;
		if (!item.hasItemMeta()) return true;
        var modelData = ItemModelHandler.getModelData(item);
		if (modelData.isNone()) return true;
		return !modelData.hasModelInteger() || modelData.getModelInteger() == 0;
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if (p.getOpenInventory().getTitle().equalsIgnoreCase(UpgradedTitle)) {
			List<ItemStack> items = new ArrayList<>();
			for (ItemStack item : e.getInventory().getContents()) {
				if (item == null) continue;
				items.addAll(p.getInventory().addItem(item).values());
			}
			for (ItemStack item : items) {
				p.getWorld().dropItemNaturally(p.getLocation(), item);
			}
			return;
		}
		if (!p.getOpenInventory().getTitle().equalsIgnoreCase(UpgradeTitle)) return;
		
		// Give all the items
		List<ItemStack> items = new ArrayList<>();
		if (e.getInventory().getItem(BOOK_SLOT) != null && !EmptyBarSlot.emptyBook.isSimilar(e.getInventory().getItem(BOOK_SLOT)))
			items.addAll(p.getInventory().addItem(e.getInventory().getItem(BOOK_SLOT)).values());
		if (e.getInventory().getItem(FRAGMENT_SLOT) != null && !EmptyBarSlot.emptyFragment.isSimilar(e.getInventory().getItem(FRAGMENT_SLOT)))
			items.addAll(p.getInventory().addItem(e.getInventory().getItem(FRAGMENT_SLOT)).values());
		for (int slot : NETHERITE_SLOTS) if (e.getInventory().getItem(slot) != null && !EmptyBarSlot.emptyIngot.isSimilar(e.getInventory().getItem(slot)))
			items.addAll(p.getInventory().addItem(e.getInventory().getItem(slot)).values());
		
		for (ItemStack item : items) {
			p.getWorld().dropItemNaturally(p.getLocation(), item);
		}
	}
	
	@EventHandler
	public void onXPChange(PlayerExpChangeEvent e) {
		Player p = e.getPlayer();
		if (!p.getOpenInventory().getTitle().equalsIgnoreCase("upgrade warpbook")) return;
		p.getOpenInventory().getTopInventory().setItem(XP_SLOT, getXP(p));
	}
	
	private static ItemStack getXP(Player p) {
		ItemStack xp = new ItemStack(Material.EXPERIENCE_BOTTLE);
		ItemMeta meta = xp.getItemMeta();
		assert meta != null;
		int needed = WarpBooks.LevelsToUpgrade;
		meta.setDisplayName("§e" + needed + " XP Levels");
		meta.setLore(Collections.singletonList((p.getLevel() >= needed ? "§a" : "§c") + p.getLevel() + "/" + needed + " Levels"));
		xp.setItemMeta(meta);
		return xp;
	}

	private static void updateReadyStatus(Inventory upgradeInventory) {
		boolean ready = shouldBeReady(upgradeInventory);
		ItemStack fill = EmptyBarSlot.getReadyItem(ready);
		if (fill.isSimilar(upgradeInventory.getItem(CONFIRM_SLOTS.get(0)))) return;
		for (int slot : CONFIRM_SLOTS) {
			upgradeInventory.setItem(slot, fill);
		}
	}

	private static boolean shouldBeReady(Inventory upgradeInventory) {
		boolean staticSlotsFull = !EmptyBarSlot.isEmptyBook(upgradeInventory.getItem(BOOK_SLOT))
				&& !EmptyBarSlot.isEmptyFragment(upgradeInventory.getItem(FRAGMENT_SLOT));
		if (!staticSlotsFull) return false;

		ItemStack xp = upgradeInventory.getItem(XP_SLOT);
		boolean xpReady = xp != null && xp.hasItemMeta() && Objects.requireNonNull(xp.getItemMeta()).hasLore()
				&& !Objects.requireNonNull(xp.getItemMeta().getLore()).isEmpty()
				&& xp.getItemMeta().getLore().get(0).startsWith("§a");
		if (!xpReady) return false;

		for (int slot : NETHERITE_SLOTS) {
			if (EmptyBarSlot.isEmptyIngot(upgradeInventory.getItem(slot))) {
				return false;
			}
		}
		return true;
	}

}
