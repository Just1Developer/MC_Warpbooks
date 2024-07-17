package net.justonedev.mc.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.Random;

public class Fragment implements Listener {
	
	// Durability full => valid fragment for upgrading
	
	public static ItemStack warpFragment;
	public static final String warpFragmentName = "§3Warp Fragment";
	
	public static void init() {
		warpFragment = new ItemStack(WarpBooks.FRAGMENT_MATERIAL, 1);
		ItemMeta meta = warpFragment.getItemMeta();
		assert meta != null;
		meta.setDisplayName(warpFragmentName);
		meta.setCustomModelData(2000000000);
		warpFragment.setItemMeta(meta);
	}
	
	public static boolean isFragment(ItemStack item) {
		return WarpBook.isWarpItem(item, warpFragment);
	}

	public static boolean isCompleteFragment(ItemStack item) {
		if (!isFragment(item)) return false;
		return ((Damageable) Objects.requireNonNull(item.getItemMeta())).getDamage() == 0;
	}
	
	public static final float minDur = 0.01f, maxDur = 0.11f;
	public static double CHANCE = 0.03;
	
	public static ItemStack newFragment() {
		ItemStack fragment = new ItemStack(warpFragment);
		Damageable fragmentMeta = (Damageable) fragment.getItemMeta();
		assert fragmentMeta != null;
		fragmentMeta.setDamage((int) Math.round(63 - 64 * (new Random(System.nanoTime()).nextDouble() * maxDur + minDur)));
		fragment.setItemMeta(fragmentMeta);
		return fragment;
	}
	
	public Fragment() {
		init();
	}
	
	static Random lootRandom = new Random(System.nanoTime());
	
	@EventHandler
	public void onLootGenerate(LootGenerateEvent e) {
		if (e.getLootContext().getKiller() != null) return;
		if (e.getLootContext().getLootedEntity() != null) return;
		if (lootRandom.nextDouble() < CHANCE) {
			e.getLoot().add(newFragment());
		}
	}
	
	@EventHandler
	public void onCraftCombine(PrepareItemCraftEvent e) {
		// Top to Bottom, Left to Right. Null if empty
		ItemStack[] items = e.getInventory().getMatrix();
		
		boolean isAnyNotFragment = false;
		boolean isAnyFragment = false;
		
		int totalHP = 0;
		
		for (ItemStack item : items) {
			if (item == null) continue;
			boolean isFrag = isFragment(item);
			if (isFrag) isAnyFragment = true;
			else isAnyNotFragment = true;
		
			if (!isFrag) continue;
			
			totalHP += 64 - ((Damageable) Objects.requireNonNull(item.getItemMeta())).getDamage();
		}
		
		// Prevent Crafting with Warp Items
		if (isAnyNotFragment && isAnyFragment) {
			// Prevent Crafting
			e.getInventory().setResult(null);
			return;
		}
		
		if (!isAnyFragment) return;
		
		// All are Fragments
		ItemStack result = new ItemStack(warpFragment);
		Damageable meta = (Damageable) result.getItemMeta();
		assert meta != null;
		meta.setDamage(64 - totalHP);
		result.setItemMeta(meta);
		e.getInventory().setResult(result);
	}
	
	@EventHandler
	public void onAnvil(PrepareAnvilEvent e) {
		Inventory inv = e.getInventory();
		// As expected, 3 Slots: 0 = Left, 1 = Right, 2 = Result
		
		if (Fragment.isFragment(inv.getItem(0)) && !Fragment.isFragment(inv.getItem(1))
		|| !Fragment.isFragment(inv.getItem(0)) && Fragment.isFragment(inv.getItem(1))) {
			e.setResult(null);
		}
		
		if (Fragment.isFragment(inv.getItem(0)) && Fragment.isFragment(inv.getItem(1))) {
			ItemStack item1 = inv.getItem(0), item2 = inv.getItem(1);
			assert item1 != null; assert item2 != null;
			assert item1.getItemMeta() != null; assert item2.getItemMeta() != null;
			Damageable m1 = (Damageable) item1.getItemMeta(), m2 = (Damageable) item2.getItemMeta();
			
			ItemStack result = new ItemStack(Objects.requireNonNull(inv.getItem(0)));
			Damageable meta = (Damageable) result.getItemMeta();
			assert meta != null;
			meta.setDamage(64 - (128 - m1.getDamage() - m2.getDamage()));
			result.setItemMeta(meta);
			
			e.setResult(result);
		}
	}
	
}
