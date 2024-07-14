package net.justonedev.mc.warpbooks.resourcepack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Resourcepack implements Listener {
	
	public static boolean defaultEnabled = true;
	public boolean enabled = true;
	private final String RESOURCE_PACK_URL;
	private final UUID RESOURCE_PACK_UUID;
	private final byte[] RESOURCE_PACK_SHA1;
	
	public Resourcepack(JavaPlugin plugin, String packURL, String packSHA1) {
		enabled = defaultEnabled;
		RESOURCE_PACK_URL = packURL;
		RESOURCE_PACK_UUID = UUID.randomUUID();
		RESOURCE_PACK_SHA1 = hexStringToByteArray(packSHA1);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (enabled) event.getPlayer().addResourcePack(RESOURCE_PACK_UUID, RESOURCE_PACK_URL, RESOURCE_PACK_SHA1, "This server uses a resource pack for (colored) backpacks. Usage is recommended.", false);
	}
	
	// Helper method to convert SHA-1 hash string to byte array
	private static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

}
