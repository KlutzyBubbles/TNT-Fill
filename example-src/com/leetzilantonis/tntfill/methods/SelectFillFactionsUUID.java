package com.leetzilantonis.tntfill;

import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SelectFillFactionsUUID {

	Main plugin;

	public SelectFillFactionsUUID(Main instance) {
		plugin = instance;
	}

	public FillResult fillSelectedDispensersSurvival(Player p,  LinkedList<Location> locations , int tntPer, int tntRemoved) {}

	public FillResult fillSelectedDispensersCreative(Player p,  LinkedList<Location> locations, int tntPer) {}

}
