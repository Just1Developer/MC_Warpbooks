package net.justonedev.mc.warpbooks;

import org.bukkit.inventory.ItemStack;

import javax.xml.stream.Location;

public class Warp {

	private Location location;
	
	private Warp(Location location) {
	
	}
	
	public static Warp of(ItemStack stack) {
		return null;
	}
	
	public ItemStack toItemStack() {
		return new ItemStack(WarpPage.warpPage);
	}

}
