package net.justonedev.mc.warpbooks.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class Upgrade implements Listener {

	public static void openUpgrader(Player p) {
		Inventory upgrade = Bukkit.createInventory(null, 54, "Upgrade Warpbook");
		p.openInventory(upgrade);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {

	}

}
