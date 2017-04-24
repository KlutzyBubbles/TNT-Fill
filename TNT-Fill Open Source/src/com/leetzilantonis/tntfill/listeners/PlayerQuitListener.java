package com.leetzilantonis.tntfill.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.leetzilantonis.tntfill.Main;

public class PlayerQuitListener implements Listener {

	Main plugin;
	
	public PlayerQuitListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		
		if (!plugin.getCustomConfig().getBoolean("keepSelectingAfterLogout")) {
			this.removeSelection(p.getUniqueId().toString());
		} else if (p.hasPermission("tntfill.removeselection")) {
			this.removeSelection(p.getUniqueId().toString());
		}
	}
	
	private void removeSelection(String UUID) {
		if (plugin.isSelecting.contains(UUID))
			plugin.isSelecting.remove(UUID);
		
		if (plugin.locs.containsKey(UUID))
			plugin.locs.remove(UUID);
	}
}
