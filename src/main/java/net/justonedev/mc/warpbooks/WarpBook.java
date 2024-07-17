package net.justonedev.mc.warpbooks;

import net.justonedev.mc.warpbooks.upgrade.Upgrade;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WarpBook implements Listener {
	
	public static final String bookPrefix = "§b";
	public static final String elderPrefix = bookPrefix + "§l";
	
	private static ItemStack warpBook, opWarpBook;
	public static final String warpBookName = bookPrefix + "Warpbook";
	public static final String elderBookName = elderPrefix + "Elder Warpbook";
	
	private static void init() {
		warpBook = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = warpBook.getItemMeta();
		assert meta != null;
		meta.setDisplayName(warpBookName);
		meta.setCustomModelData(8498);
		warpBook.setItemMeta(meta);
		
		opWarpBook = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		meta = opWarpBook.getItemMeta();
		assert meta != null;
		meta.setDisplayName(elderBookName);
		meta.setCustomModelData(8497);
		opWarpBook.setItemMeta(meta);
		
		WarpBooks.UPGRADE = new ItemStack(opWarpBook);
		meta  = WarpBooks.UPGRADE.getItemMeta();
		assert meta != null;
		meta.setDisplayName("§5Upgrade Warpbook");
		meta.setLore(Collections.singletonList("§7Elder Warpbooks provide free teleports."));
		WarpBooks.UPGRADE.setItemMeta(meta);
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
	
	public static String getWarpBookUUID(ItemStack warpBook) {
		ItemMeta meta = warpBook.getItemMeta();
		assert meta != null;
		return meta.getPersistentDataContainer().get(WarpBooks.idKey, PersistentDataType.STRING);
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
		assert book != null;
		// Yes, it's long. It's basically asserted that warp books have thad id tag, so we get it and parse the UUID
		String uuid;
		try {
			uuid = Objects.requireNonNull(Objects.requireNonNull(book.getItemMeta())
					.getPersistentDataContainer().get(WarpBooks.idKey, PersistentDataType.STRING));
		} catch (Exception ignored) {
			return;
		}
		
		Player p = e.getPlayer();
		
		if (p.isSneaking()) openWarpBookEditor(p, uuid, level == 1);
		else openWarpBook(p, uuid);
		p.playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.85f, 1.0f);
	}
	
	private void openWarpBook(Player player, String uuid) {
		Inventory bookInv = Bukkit.createInventory(null, WarpBooks.WARP_SLOTS, "Warpbook");
		List<ItemStack> warps = WarpSaver.loadWarps(uuid, player);
		for (ItemStack warp : warps) {
			bookInv.addItem(warp);
		}
		player.openInventory(bookInv);
	}
	
	private void openWarpBookEditor(Player player, String uuid, boolean isNormal) {
		Inventory bookInv = Bukkit.createInventory(null, WarpBooks.WARP_SLOTS + 9, "Edit Warpbook");
		bookInv.setContents(WarpSaver.loadWarpsInventoryExact(uuid));
		
		if(isNormal) bookInv.setItem(4, new ItemStack(WarpBooks.UPGRADE));
		player.openInventory(bookInv);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (!e.getView().getTitle().contains("Edit Warpbook")) return;
		ItemStack book = e.getPlayer().getInventory().getItemInMainHand();
		String uuid = getWarpBookUUID(book);
		WarpSaver.saveWarps(uuid, e.getInventory().getContents());
	}
	
	
	
	// --------------------- INVENTORY CLICK ---------------------
	
	
	private static final List<ClickType> NORMAL_CLICK_TYPES = Arrays.asList(ClickType.LEFT, ClickType.SHIFT_LEFT, ClickType.RIGHT, ClickType.SHIFT_RIGHT, ClickType.MIDDLE);
	private static final List<ClickType> RECOGNIZED_CLICK_TYPES = Collections.singletonList(ClickType.LEFT);
	HashMap<UUID, Long> teleportCooldown = new HashMap<>();
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		String lowercase = e.getView().getTitle().toLowerCase();
		if (!lowercase.contains("warpbook") && !lowercase.contains("warp book")) return;
		if (lowercase.contains("upgrade")) return;
		
		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
		
		if (!lowercase.contains("edit")) {
			
			if (!NORMAL_CLICK_TYPES.contains(e.getClick())) {
				e.setCancelled(true);
				return;
			}
			if (e.getSlot() != e.getRawSlot()) {
				if (e.isShiftClick() || e.getSlot() == e.getWhoClicked().getInventory().getHeldItemSlot()) e.setCancelled(true);
				return;
			}
			
			// Warp Page Clicked
			e.setCancelled(true);
			
			if (RECOGNIZED_CLICK_TYPES.contains(e.getClick())) warpPageClicked((Player) e.getWhoClicked(), e.getCurrentItem());
			return;
		}
		
		
		// The Edit Warpbook Inventory
		
		
		// Allowed Clicks: Everything, basically. Them's the rules:
		// 1. The book's slot cannot be modified
		// 2. The top row of the edit menu cannot be modified
		// 3. The other rows of the warp book must only contain set warp pages.
		
		// Here are the default (empty) values, when nothing is there:
		// Clicked: null
		// Cursor: AIR
		// HotbarItem: null
		
		ItemStack cursor = e.getCursor();
		ItemStack clicked = e.getCurrentItem();
		boolean clickedOwnInv = e.getRawSlot() != e.getSlot();
		boolean noCursor = cursor == null || cursor.getType() == Material.AIR;
		int bookSlot = e.getWhoClicked().getInventory().getHeldItemSlot();
		
		if (e.getRawSlot() < 9 || e.getClick() == ClickType.SWAP_OFFHAND || e.getClick() == ClickType.UNKNOWN) {
			e.setCancelled(true);
			if (e.getRawSlot() == 4 && WarpBooks.UPGRADE.isSimilar(clicked) && WarpBooks.enableUpgrading) {
				// Clicked the upgrade
				e.setCurrentItem(null);	// For some reason, when shift-clicking, we still get the item
				Upgrade.openUpgrader((Player) e.getWhoClicked());
			}
			return;
		}
		
		if (e.getClick() == ClickType.DROP || e.getClick() == ClickType.CONTROL_DROP) {
			if (clickedOwnInv && e.getSlot() == bookSlot) e.setCancelled(true);
			return;
		}
		
		if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
			if (clickedOwnInv && !WarpPage.isSetWarpPage(clicked)) { e.setCancelled(true); return; }
			if (!clickedOwnInv) return;		// Transferred something out of warp book, irrelevant for duplicates
			if (clicked == null) return;		// Nothing clicked
			
			boolean contains = false;
			for (int i = 9; i < e.getView().getTopInventory().getSize() && !contains; ++i) {
				if (clicked.isSimilar(e.getView().getTopInventory().getItem(i))) contains = true;	// Already inside, inv.contains(add) doesn't quite work
			}
			
			if (contains) {
				e.setCancelled(true);
				return;
			}
			
			if (clicked.getAmount() == 1) return;
			e.setCancelled(true);
			ItemStack add = new ItemStack(clicked);
			add.setAmount(1);
			
			HashMap<Integer, ItemStack> rest = e.getView().getTopInventory().addItem(add);
			if (!rest.isEmpty()) return;	// Item not added: Full Inventory?
			clicked.setAmount(clicked.getAmount() - 1);
		}
		
		if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) {
			
			if (!clickedOwnInv && !(WarpPage.isSetWarpPage(cursor) || noCursor))  { e.setCancelled(true); return; }
			if (noCursor) return;	// Picked up something, doesn't matter when checking for multiple pages
			if (clickedOwnInv) return;
			
			if (clicked == null) {
				
				boolean contains = false;
				for (int i = 9; i < e.getView().getTopInventory().getSize() && !contains; ++i) {
					if (cursor.isSimilar(e.getView().getTopInventory().getItem(i))) contains = true;	// Already inside, inv.contains(add) doesn't quite work
				}
				
				if (contains) {
					e.setCancelled(true);
					return;
				}
				
				if (cursor.getAmount() > 1) {
					e.setCancelled(true);
					cursor.setAmount(cursor.getAmount() - 1);
					ItemStack newItem = new ItemStack(cursor);
					newItem.setAmount(1);
					e.getInventory().setItem(e.getSlot(), newItem);
				}
				// Else: Cancel if they're similar (same item, different amount), as they'd stack
			} else if (clicked.isSimilar(cursor)) e.setCancelled(true);
		}
		
		if (e.getClick() == ClickType.NUMBER_KEY) {
			if (bookSlot == e.getHotbarButton())  { e.setCancelled(true); return; }
			ItemStack hotbarItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
			
			if (!clickedOwnInv && !(WarpPage.isSetWarpPage(hotbarItem) || hotbarItem == null || hotbarItem.getType() == Material.AIR)) { e.setCancelled(true); return; }
			
			//todo contains check
		}
		
		if (e.getClick() == ClickType.DOUBLE_CLICK) {
			// Custom pick-up
			// Custom Double Click Implementation:
			// - The slot (e.getCurrentItem()) is irrelevant
			// - The Cursor is what was picked up
			// - The Cursor is never null, just AIR or an Item. getCurrentItem() can be null.
			// - The Cursor Item is then filled by all contents
			
			assert cursor != null;
			if(cursor.getType().isAir()) return;	// :) if event is cancelled or something
			
			Inventory inv = e.getView().getTopInventory();
			for(int i = 9; i < inv.getSize(); ++i) {
				// Obv can't fill up when max stack size is 1
				if (cursor.getMaxStackSize() == 1 || cursor.getAmount() == cursor.getMaxStackSize()) break;
				
				ItemStack slotItem = inv.getItem(i);
				if (slotItem == null || slotItem.getType() == Material.AIR) continue;
				if (!slotItem.getType().equals(cursor.getType())) continue;
				if (!slotItem.isSimilar(cursor)) continue;
				
				// Lets fill this bad boy up
				int subtract = Math.min(cursor.getMaxStackSize() - cursor.getAmount(), slotItem.getAmount());
				slotItem.setAmount(slotItem.getAmount() - subtract);
				cursor.setAmount(cursor.getAmount() + subtract);
			}
			
			inv = e.getView().getBottomInventory();
			for(int i = 9; i < inv.getSize(); ++i) {
				// Obv can't fill up when max stack size is 1
				if (cursor.getMaxStackSize() == 1 || cursor.getAmount() == cursor.getMaxStackSize()) break;
				
				ItemStack slotItem = inv.getItem(i);
				if (slotItem == null || slotItem.getType() == Material.AIR) continue;
				if (!slotItem.getType().equals(cursor.getType())) continue;
				if (!slotItem.isSimilar(cursor)) continue;
				
				// Lets fill this bad boy up
				int subtract = Math.min(cursor.getMaxStackSize() - cursor.getAmount(), slotItem.getAmount());
				slotItem.setAmount(slotItem.getAmount() - subtract);
				cursor.setAmount(cursor.getAmount() + subtract);
			}
			
			e.setCancelled(true);
		}
		if (e.isCancelled()) return;
		
		// Unhandled (on purpose, not relevant): MIDDLE, CREATIVE, WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT
		
		// Now: Handle multiple pages
		
		//if ()
	}
	
	@EventHandler
	public void onAnvil(PrepareAnvilEvent e) {
		// No cursive names for warp pages
		if (WarpPage.isWarpPage(e.getResult())) {
			if (e.getResult() == null) return;
			ItemStack result = e.getResult();
			ItemMeta meta = result.getItemMeta();
			if (meta == null) return;
			if(!meta.getDisplayName().startsWith("§"))
				meta.setDisplayName("§f" + meta.getDisplayName());
			result.setItemMeta(meta);
			e.setResult(result); 
		}
		// Keep colors when renaming
		int level = WarpBook.getWarpBookLevel(e.getResult());
		if (level > 0) {
			if (e.getResult() == null) return;
			ItemStack result = e.getResult();
			ItemMeta meta = result.getItemMeta();
			if (meta == null) return;
			meta.setDisplayName((level == 2 ? elderPrefix : bookPrefix) + meta.getDisplayName());
			result.setItemMeta(meta);
			e.setResult(result);
		}
	}
	
	@EventHandler
	public void InventoryDrag(InventoryDragEvent e) {
		String lowercase = e.getView().getTitle().toLowerCase();
		if (!lowercase.contains("warpbook") && !lowercase.contains("warp book")) return;
		
		if (!(e.getWhoClicked() instanceof Player)) {
			e.setCancelled(true);
			return;
		}
		
		if (!lowercase.contains("edit")) {
			for (int slot : e.getRawSlots()) {
				if (slot < e.getInventory().getSize()) {
					e.setCancelled(true);
					return;
				}
			}
		}
		
		if (lowercase.contains("upgrade")) return;
		
		if (WarpPage.isSetWarpPage(e.getCursor())) return;
		for (int slot : e.getRawSlots()) {
			// Allow Set Warp Pages
			if (slot < e.getInventory().getSize()) {
				e.setCancelled(true);
				return;
			}
		}
	}
	
	public void warpPageClicked(Player p, ItemStack item) {
		if (!WarpPage.isWarpPage(item)) return;
		Location loc = WarpPage.getLocation(item);
		if (loc == null) return;
		
		int cost = WarpBooks.enableCostTP ? WarpBooks.LevelCostPerTeleport : 0;
		if (!Objects.equals(loc.getWorld(), p.getWorld())) cost = WarpBooks.enableCostTPCrossWorlds ? WarpBooks.LevelCostPerTeleportWorlds : 0;
		
		if (p.getLevel() < cost) {
			p.sendMessage(String.format("§cYou don't have enough XP Levels to teleport. (§e%d §crequired, you have §e%d§c)", cost, p.getLevel()));
			return;
		}
		
		if (teleportCooldown.getOrDefault(p.getUniqueId(), 0L) > System.currentTimeMillis()) {
			return;
		}
		
		teleportCooldown.put(p.getUniqueId(), System.currentTimeMillis() + (long) (1000 * WarpBooks.teleportCooldown));
		p.setLevel(p.getLevel() - cost);
		p.teleport(loc);
		if (WarpBooks.enableTPSound) p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.45f, 1.0f);
	}
}
