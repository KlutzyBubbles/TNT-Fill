package com.leetzilantonis.tntfill.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.leetzilantonis.tntfill.Main;

public class PlayerJoinListener implements Listener {
	
	Main plugin;
	
	public PlayerJoinListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (e.getPlayer().isOp() && plugin.getCustomConfig().getBoolean("notifyOP")) {
			e.getPlayer().sendMessage(ChatColor.GREEN + "TNT-Fill v" + plugin.getDescription().getVersion() + " succesfully running...");
			e.getPlayer().sendMessage(ChatColor.GREEN + "Contact the owner/developer if you experience any glitches.");
		}
	}

}
