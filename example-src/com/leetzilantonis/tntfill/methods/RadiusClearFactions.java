package com.leetzilantonis.tntfill;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class RadiusClearFactions {

	Main plugin;

	public RadiusClearFactions(Main plugin) {
		this.plugin = plugin;
	}

	public int countTNT(Player p, int radius) {}

	public HashMap<ItemMeta, Integer> countListTNT(Player p, int radius) {}

	public int quickCount(Player p) {}

	public ClearResult clearDispensersSurvival(Player p, int radius) {}

	public ClearResult clearDispensersSurvival(Player p, int radius, int per) {}

	public ClearResult clearDispensersCreative(Player p, int radius) {}

	public ClearResult clearDispensersCreative(Player p, int radius, int per) {}
	
}
