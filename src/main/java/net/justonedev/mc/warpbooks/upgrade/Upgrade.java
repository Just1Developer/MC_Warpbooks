package net.justonedev.mc.warpbooks.upgrade;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Upgrade implements Listener {

	public static void openUpgrader(Player p) {
		p.openInventory(Bukkit.createInventory(null, 54, "Upgrade Warpbook"));
	}

}
