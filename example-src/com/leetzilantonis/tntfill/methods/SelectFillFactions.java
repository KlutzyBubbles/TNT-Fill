package com.leetzilantonis.tntfill;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SelectFillFactions {

	Main plugin;

	public SelectFillFactions(Main instance) {
		plugin = instance;
	}

	public FillResult fillSelectedDispensersSurvival(Player p,  LinkedList<Location> locations , int tntPer, int tntRemoved) {}

	public FillResult fillSelectedDispensersCreative(Player p,  LinkedList<Location> locations, int tntPer) {}

}
