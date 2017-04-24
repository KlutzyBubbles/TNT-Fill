package com.leetzilantonis.tntfill.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leetzilantonis.tntfill.Main;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class BlockClickFactionsListener implements Listener {

	Main plugin;
	
	public BlockClickFactionsListener(Main instance) {
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
			
			MPlayer mp = MPlayer.get(p);
			Faction pf = mp.getFaction();
			Location loc = e.getClickedBlock().getLocation();
			
			Faction df = BoardColl.get().getFactionAt(PS.valueOf(loc));
			if (df.getId().equals(pf.getId())) {

				plugin.addLocation(p, loc);

			} else if (pf.getRelationTo(df).equals(Rel.ALLY)) {

				if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {

					plugin.addLocation(p, loc);

				}
			} else if (df.isNone()) {
				
				plugin.addLocation(p, loc);
				
			}
			
		}
		
	}
	
}
