package com.leetzilantonis.tntfill.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leetzilantonis.tntfill.Main;

public class BlockClickListener implements Listener {

	Main plugin;
	
	public BlockClickListener(Main instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (e.getAction() != Action.LEFT_CLICK_BLOCK
				|| e.getClickedBlock().getType() == null
				|| e.getClickedBlock().getType() != Material.DISPENSER
				|| !plugin.isSelecting.contains(p.getUniqueId().toString())){
			return;
		}
		
		if (e.getClickedBlock().getType() == Material.DISPENSER) {
			
			plugin.addLocation(p, e.getClickedBlock().getLocation());
			
		}
		
	}
	
}
