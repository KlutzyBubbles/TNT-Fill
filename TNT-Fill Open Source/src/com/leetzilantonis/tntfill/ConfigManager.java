package com.leetzilantonis.tntfill;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {

	Main plugin;

	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	
	public ConfigManager(Main instance) {
		plugin = instance;
	}
	
	public void initCustomConfig() {
		
		FileConfiguration c = this.getCustomConfig();
		
		c.addDefault("defaultFillRadius", 10);
		c.addDefault("maxFillRadius", 50);
		
		c.addDefault("maxDispenserSelect", 25);
		c.addDefault("keepSelectingAfterLogout", false);
		c.addDefault("messageOnSelectedDispenserBreak", true);
		c.addDefault("helpBasedOnPerm", true);
		c.addDefault("notifyOP", true);
		c.addDefault("helpSuffix", true);
		c.addDefault("breakInCreative", false);
		c.addDefault("updateCheck", true);
		c.addDefault("sendUpdates", true);
		c.addDefault("notifyCreativeSelect", true);
		c.addDefault("useFactions", false);
		c.addDefault("factionsUUID", false);
		c.addDefault("allowAllyFill", true);
		c.addDefault("naturalRadius", false);
		
		c.addDefault("debug", true);
		
		List<String> a = new ArrayList<String>();
		a.add("tf");
		a.add("loadtnt");
		a.add("fillaround");
		c.addDefault("fillAliases", a);
		a.clear();
		a.add("tc");
		a.add("cleartnt");
		a.add("cleararound");
		c.addDefault("clearAliases", a);
		
		c.options().copyDefaults(true);
		this.saveDefaultCustomConfig();
		this.saveCustomConfig();
		
	}
	
	public void reloadCustomConfig() {
	    if (customConfigFile == null) {
	    customConfigFile = new File(plugin.getDataFolder(), "settings.yml");
	    }
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("settings.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig() {
	    if (customConfig == null) {
	        reloadCustomConfig();
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig() {
	    if (customConfig == null || customConfigFile == null) {
	        return;
	    }
	    try {
	        getCustomConfig().save(customConfigFile);
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
	public void saveDefaultCustomConfig() {
	    if (customConfigFile == null) {
	        customConfigFile = new File(plugin.getDataFolder(), "settings.yml");
	    }
	    if (!customConfigFile.exists()) {            
	         this.plugin.saveResource("settings.yml", false);
	     }
	}
  // End Custom Config
	
}
