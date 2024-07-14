package net.justonedev.mc.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class WarpPage implements Listener {

	public static final String itemName = "§fWarp page";
	public static ItemStack warpPage;
	
	public static void init() {
		warpPage = new ItemStack(WarpBooks.PLUGIN_MATERIAL, 1);
		ItemMeta meta = warpPage.getItemMeta();
		assert meta != null;
		meta.setDisplayName(itemName);
		meta.setCustomModelData(8499);
		warpPage.setItemMeta(meta);
	}
	
	public static boolean isWarpPage(ItemStack item) {
		return WarpBook.isWarpItem(item, warpPage);
	}
	
	public static Location getLocation(ItemStack warpPage) {
		if (warpPage == null) return null;
		if (warpPage.getItemMeta() == null) return null;
		PersistentDataContainer data = warpPage.getItemMeta().getPersistentDataContainer();
		if (!data.has(WarpBooks.pageWorldKey)) return null;
		// Assume it's got everything
		try {
			return new Location(
					Bukkit.getWorld(data.get(WarpBooks.pageWorldKey, PersistentDataType.STRING)),
					Objects.requireNonNull(data.get(WarpBooks.pageXKey, PersistentDataType.DOUBLE)),
					Objects.requireNonNull(data.get(WarpBooks.pageYKey, PersistentDataType.DOUBLE)),
					Objects.requireNonNull(data.get(WarpBooks.pageZKey, PersistentDataType.DOUBLE)),
					Objects.requireNonNull(data.get(WarpBooks.pagePitchKey, PersistentDataType.FLOAT)),
					Objects.requireNonNull(data.get(WarpBooks.pageYawKey, PersistentDataType.FLOAT))
			);
		} catch (NullPointerException ignored) { return null; }
	}
	
	public static boolean hasLocation(ItemStack warpPage) {
		return getLocation(warpPage) != null;
	}
	
	public WarpPage() {
		init();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack page = e.getItem();
		
		Block b = e.getClickedBlock();
		if (b != null) {
			// Invoked twice when player is looking at where sign is gonna be
			if (signs.containsValue(b.getLocation())) {
				e.setCancelled(true);
				return;
			}
		}
		
		if (!isWarpPage(page)) return;
		if (hasLocation(page)) return;
		
		String name = Objects.requireNonNull(page.getItemMeta()).getDisplayName();
		// ggf.: Split these pages up into one and multiple
		if (page.getAmount() > 1) {
			ItemStack remaining = new ItemStack(page);
			remaining.setAmount(page.getAmount() - 1);
			page.setAmount(1);
			
			ItemMeta meta = page.getItemMeta();
			assert meta != null;
			meta.setLore(Collections.singletonList(name));
			meta.setDisplayName("temp-page::non-dup");
			page.setItemMeta(meta);
			
			HashMap<Integer, ItemStack> items = p.getInventory().addItem(remaining);
			for (ItemStack value : items.values()) {
				p.getWorld().dropItem(p.getLocation(), value);
			}
		}
		
		UUID uuid = p.getUniqueId();
		Sign sign = getNewSign(uuid, p.getLocation());
		
		if (sign == null) {
			p.sendMessage("§cYou're standing in blocks, please don't.");
			return;
		}
		
		sign.getSide(Side.FRONT).setLine(0, name.equals(itemName) ? "" : name);
		sign.update();
		p.openSign(sign);
	}
	
	//private static final HashMap<UUID, Triple<Material, BlockData>> previousBlocks = new HashMap<>();
	private static final HashMap<UUID, Location> signs = new HashMap<>();
	
	private Sign getNewSign(UUID uuid, Location loc) {
		Location l = new Location(loc.getWorld(), loc.getX(), Objects.requireNonNull(loc.getWorld()).getMaxHeight() - 1, loc.getZ());
		l = loc.clone();
		if (l.getBlock().getType() != Material.AIR) {
			l.add(0, 1, 0);
			if (l.getBlock().getType() != Material.AIR) {
				return null;
			}
		}
		
		/*int i = 0;
		while (l.getBlock().getType() != Material.AIR) {
			l.add(1, 0, 0);
			if (i++ > 60) {
				break;
			}
		}*/
		//if (l.getBlock().getType() != Material.AIR) previousBlocks.put(uuid, new Triple<>(l.getBlock().getType(), l.getBlock().getBlockData().clone(), l.getBlock().));
		l.getBlock().setType(Material.BIRCH_WALL_SIGN);
		signs.put(uuid, l.getBlock().getLocation());	// Round to block location
		return (Sign) l.getBlock().getState();
	}
	
	@EventHandler
	public void onSignFinish(SignChangeEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if (!signs.containsKey(uuid)) return;
		signs.remove(uuid);
		e.setCancelled(true);
		
		e.getBlock().setType(Material.AIR);
		
		/*
		Location l = e.getBlock().getLocation();
		
		Bukkit.broadcastMessage("§e" + previousBlocks.containsKey(uuid));
		
		// Remove the sign block
		if (previousBlocks.containsKey(uuid)) {
			Bukkit.broadcastMessage("§b" + previousBlocks.get(uuid));
			Triple<Material, BlockData> prev = previousBlocks.get(uuid);
			previousBlocks.remove(uuid);
			l.getBlock().setType(prev.Key);
			l.getBlock().setBlockData(prev.Value);
		} else {
			l.getBlock().setType(Material.AIR);
		}*/
		
		ItemStack page = p.getInventory().getItemInMainHand();
		if (!isWarpPage(page)) return;
		ItemMeta meta = page.getItemMeta();
		assert meta != null;
		
		String line = e.getLine(0);
		if (line == null || line.isEmpty()) {
			// Give old name back
			if (!meta.hasLore() || Objects.requireNonNull(meta.getLore()).isEmpty()) return;
			meta.setDisplayName(meta.getLore().get(0));
			page.setItemMeta(meta);
			return;
		}
		
		// new:
		Location l = p.getLocation();
		meta.setDisplayName("§f" + line);	// Non-cursive
		meta.getPersistentDataContainer().set(WarpBooks.pageWorldKey, PersistentDataType.STRING, p.getWorld().getName());
		meta.getPersistentDataContainer().set(WarpBooks.pageXKey, PersistentDataType.DOUBLE, l.getX());
		meta.getPersistentDataContainer().set(WarpBooks.pageYKey, PersistentDataType.DOUBLE, l.getY());
		meta.getPersistentDataContainer().set(WarpBooks.pageZKey, PersistentDataType.DOUBLE, l.getZ());
		meta.getPersistentDataContainer().set(WarpBooks.pagePitchKey, PersistentDataType.FLOAT, l.getPitch());
		meta.getPersistentDataContainer().set(WarpBooks.pageYawKey, PersistentDataType.FLOAT, l.getYaw());
		page.setItemMeta(meta);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if (signs.containsValue(e.getBlock().getLocation())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onProtect(BlockExplodeEvent e) {
		e.blockList().removeIf((b) -> signs.containsValue(b.getLocation()));
	}
	
	@EventHandler
	public void onProtect2(EntityExplodeEvent e) {
		e.blockList().removeIf((b) -> signs.containsValue(b.getLocation()));
	}
	
	@EventHandler
	public void onSignDistanceChanged(PlayerMoveEvent e) {
		if (!signs.containsKey(e.getPlayer().getUniqueId())) return;
		Bukkit.broadcastMessage("§aDistance: " + e.getPlayer().getLocation().distance(signs.get(e.getPlayer().getUniqueId())));
		
		
		Location loc = signs.get(e.getPlayer().getUniqueId());
		if (loc == null) return;
		try {
			Sign old = ((Sign) loc.getBlock().getState());
			if (old.getAllowedEditor() == null) {
				loc.getBlock().setType(Material.AIR);
				
				Sign sign = getNewSign(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
				Bukkit.broadcastMessage("§dNew Sign: " + sign);
				if (sign == null) {
					// Remove old and return
					signs.remove(e.getPlayer().getUniqueId());
					return;
				}
				String s = old.getSide(Side.FRONT).getLine(0);
				sign.getSide(Side.FRONT).setLine(0, s);
				sign.update();
				e.getPlayer().openSign(sign);
			}
		} catch (Exception any) {
			loc.getBlock().setType(Material.AIR);
			signs.remove(e.getPlayer().getUniqueId());
		}
	}
	
}
