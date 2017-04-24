package com.leetzilantonis.tntfill.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.leetzilantonis.tntfill.Main;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

public class BlockClickFactionsUUIDListener implements Listener {

	Main plugin;

	public BlockClickFactionsUUIDListener(Main instance) {
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

			FPlayer mp = FPlayers.getInstance().getByPlayer(p);
			Faction pf = mp.getFaction();
			Location loc = e.getClickedBlock().getLocation();

			FLocation fl = new FLocation(loc);
			Faction df = Board.getInstance().getFactionAt(fl);

			if (df.getId().equals(pf.getId())) {

				plugin.addLocation(p, loc);

			} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

				plugin.addLocation(p, loc);

			} else if (df.isNone()) {

				plugin.addLocation(p, loc);

			}

		}

	}

}
