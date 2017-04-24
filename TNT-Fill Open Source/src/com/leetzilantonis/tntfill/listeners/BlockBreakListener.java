	package com.leetzilantonis.tntfill.listeners;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leetzilantonis.tntfill.Main;

public class BlockBreakListener implements Listener {

	Main plugin;
	
	public BlockBreakListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent e) {
		
		Player p = e.getPlayer();
		
		if (e.getBlock().getType() == Material.DISPENSER
				&& plugin.isSelecting.contains(p.getUniqueId().toString())
				&& p.getGameMode() == GameMode.CREATIVE) {
			
			if (!plugin.getCustomConfig().getBoolean("breakInCreative")) {
				e.setCancelled(true);
				if (plugin.getCustomConfig().getBoolean("notifyCreativeSelect")) {
					p.sendMessage(plugin.lm.getLangMessage("exitSelectMode", -1, -1));
				}
				return;
			}
		}
		
		Location loc = e.getBlock().getLocation();
		
		for (Entry<String, LinkedList<Location>> value : plugin.locs.entrySet()) {
			
			LinkedList<Location> list = value.getValue();
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(value.getKey()));
			
			if (list.contains(loc)) {
				
				list.remove(loc);
				
				if (plugin.getCustomConfig().getBoolean("messageOnSelectedDispenserBreak")) {
					
					String message = plugin.lm.getUntranslatedMessage("selectedDispenserBroken");
					message = message.replace("{player}", target.getName());
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
					
				}
				
			}
			
		}
		
	}
	
}
