package com.leetzilantonis.tntfill;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main plugin;
	
	@Override
	public void onEnable() {

		plugin = this;
		
		if (this.getCustomConfig().getBoolean("useFactions", false)) {

			if (this.getCustomConfig().getBoolean("factionsUUID", false)) {

				if (this.getServer().getPluginManager().getPlugin("FactionsUUID") == null) {

					// No FactionsUUID, load normal tnt fill properties
					
				} else {

					// FactionsUUID detected, load FactionsUUID properties
					
				}

			} else {

				if (this.getServer().getPluginManager().getPlugin("Factions") == null) {

					// No Factions, load normal tnt fill properties
					
				} else {

					// Factions detected, load Facitons properties
					
				}

			}

		} else {

			// no faction properties have been enabled... enable normal tnt fill properties
			
		}

	}
	
	@Override
	public void onDisable() {
		
		plugin = null;
		
	}
	
	/**
	 * @return The instance of the main class used with methods
	 */
	public static Main getInstance() {
		return plugin;
	}

}
