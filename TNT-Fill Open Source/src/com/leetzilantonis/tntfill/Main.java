package com.leetzilantonis.tntfill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

//import com.leetzilantonis.tntfill.commands.TntClearCommand;
import com.leetzilantonis.tntfill.commands.TntFillFactionsCommand;
import com.leetzilantonis.tntfill.commands.TntFillFactionsUUIDCommand;
import com.leetzilantonis.tntfill.commands.TntfillCommand;
import com.leetzilantonis.tntfill.listeners.BlockBreakListener;
import com.leetzilantonis.tntfill.listeners.BlockClickFactionsListener;
import com.leetzilantonis.tntfill.listeners.BlockClickFactionsUUIDListener;
import com.leetzilantonis.tntfill.listeners.BlockClickListener;
import com.leetzilantonis.tntfill.listeners.PlayerJoinListener;
//import com.leetzilantonis.tntfill.methods.RadiusClear;
import com.leetzilantonis.tntfill.methods.RadiusFill;
import com.leetzilantonis.tntfill.methods.RadiusFillFactions;
import com.leetzilantonis.tntfill.methods.RadiusFillFactionsUUID;
import com.leetzilantonis.tntfill.methods.SelectFill;
import com.leetzilantonis.tntfill.methods.SelectFillFactions;
import com.leetzilantonis.tntfill.methods.SelectFillFactionsUUID;
import com.leetzilantonis.tntfill.validation.Metrics;
import com.leetzilantonis.tntfill.validation.UpdateCheck;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

	public LangManager lm;
	ConfigManager conf;
	//public RadiusClear rc;
	public RadiusFill rf;
	public RadiusFillFactions rff;
	public RadiusFillFactionsUUID rffu;
	public SelectFill sf;
	public SelectFillFactions sff;
	public SelectFillFactionsUUID sffu;

	UpdateCheck uc;

	public List<String> isSelecting = new ArrayList<String>();
	public HashMap<String, LinkedList<Location>> locs = new HashMap<>();

	public int defaultFillRadius;
	public int maxFillRadius;
	public int defaultClearRadius;
	public int maxClearRadius;
	public boolean natural;

	@Override
	public void onEnable() {

		conf = new ConfigManager(this);
		lm = new LangManager(this);
		//rc = new RadiusClear(this);
		rf = new RadiusFill(this);
		sf = new SelectFill();

		lm.initLangConfig();
		conf.initCustomConfig();

		uc = new UpdateCheck(this, this.getDescription().getVersion());
		uc.startUpdateCheck();

		defaultFillRadius = this.getCustomConfig().getInt("defaultFillRadius", 10);
		maxFillRadius = this.getCustomConfig().getInt("maxFillRadius", 50);
		defaultClearRadius = this.getCustomConfig().getInt("defaultClearRadius", 10);
		maxClearRadius = this.getCustomConfig().getInt("maxClearRadius", 50);
		natural = this.getCustomConfig().getBoolean("naturalRadius", false);

		if (this.getCustomConfig().contains("fillAliases")) {

			this.getCommand("tntfill").setAliases(this.getCustomConfig().getStringList("fillAliases"));

		}
		
		if (this.getCustomConfig().contains("clearAliases")) {

			this.getCommand("tntfill").setAliases(this.getCustomConfig().getStringList("clearAliases"));

		}

		if (this.getCustomConfig().getBoolean("useFactions", false)) {

			if (this.getCustomConfig().getBoolean("factionsUUID", false)) {

				if (this.getServer().getPluginManager().getPlugin("FactionsUUID") == null) {

					this.getLogger().warning("You have factions uuid mode enabled in TNT-Fill but you do not have factionsuuid, using factions untill the plugin finds factionsuuid...");
					this.getCommand("tntfill").setExecutor(new TntfillCommand(this));

				} else {

					rffu = new RadiusFillFactionsUUID(this);
					sffu = new SelectFillFactionsUUID(this);
					this.getCommand("tntfill").setExecutor(new TntFillFactionsUUIDCommand(this));
					this.getServer().getPluginManager().registerEvents(new BlockClickFactionsUUIDListener(this), this);

				}

			} else {

				if (this.getServer().getPluginManager().getPlugin("Factions") == null) {

					this.getLogger().warning("You have factions mode enabled in TNT-Fill but you do not have factions, using non factions untill the plugin finds factions...");
					this.getCommand("tntfill").setExecutor(new TntfillCommand(this));

				} else {

					rff = new RadiusFillFactions(this);
					sff = new SelectFillFactions(this);
					this.getCommand("tntfill").setExecutor(new TntFillFactionsCommand(this));
					this.getServer().getPluginManager().registerEvents(new BlockClickFactionsListener(this), this);

				}

			}

		} else {

			this.getCommand("tntfill").setExecutor(new TntfillCommand(this));
			this.getServer().getPluginManager().registerEvents(new BlockClickListener(this), this);

		}

		//this.getCommand("tntclear").setExecutor(new TntClearCommand(this));

		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);

		this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been ENABLED");

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			// Failed to submit the stats :-(
		}

		if (this.getCustomConfig().getList("playersSelecting") != null) {
			isSelecting = this.getCustomConfig().getStringList("playersSelecting");
		}

		if (this.getCustomConfig().contains("storage")) {

			ConfigurationSection sec = this.getCustomConfig().getConfigurationSection("storage");

			if (sec.getKeys(false) == null) {

				this.getLogger().info("No one is in selecting mode! (Its not a bad thing, just info)");

			} else {

				for(String str: sec.getKeys(false)) {

					List<String> stringList = this.getCustomConfig().getStringList("storage." + str);
					LinkedList<Location> list = new LinkedList<Location>();

					for (String value : stringList) {

						String[] items = value.split(",");
						Location loc = new Location(this.getServer().getWorld(items[0]), Double.parseDouble(items[1]), Double.parseDouble(items[2]), Double.parseDouble(items[3]));
						list.add(loc);

					}

					locs.put(str, list);

				}
			}
		}
	}

	@Override
	public void onDisable() {

		if (!isSelecting.isEmpty()) {
			this.getCustomConfig().set("playersSelecting", isSelecting);
		}

		if (!locs.isEmpty()) {
			for(Entry<String, LinkedList<Location>> entry : locs.entrySet()) {

				double x, y, z;
				String world;

				LinkedList<Location> list = entry.getValue();
				List<String> stringList = new ArrayList<String>();

				for (Location loc : list) {

					x = loc.getX();
					y = loc.getY();
					z = loc.getZ();
					world = loc.getWorld().getName();

					String save = world + "," + x + "," + y + "," + z;
					stringList.add(save);

				}

				this.getCustomConfig().set("storage." + entry.getKey(), stringList);

			}
		}
		this.saveCustomConfig();
		this.saveLangConfig();
		this.getLogger().info(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " has been DISABLED.");

	}

	public void reloadLangConfig() {
		lm.reloadLangConfig();
	}

	public void saveLangConfig() {
		lm.saveLangConfig();
	}

	public FileConfiguration getLangConfig() {
		return lm.getLangConfig();
	}

	public void reloadCustomConfig() {
		conf.reloadCustomConfig();
	}

	public void saveCustomConfig() {
		conf.saveCustomConfig();
	}

	public void saveDefaultCustomConfig() {
		conf.saveDefaultCustomConfig();
	}

	public FileConfiguration getCustomConfig() {
		return conf.getCustomConfig();
	}

	public void UpdateCheck() {
		uc.startUpdateCheck();
	}

	@SuppressWarnings({ "deprecation", "unused" })
	private void debug(String msg) {
		if (this.getCustomConfig().getBoolean("debug", true)) {
			for (Player online : this.getServer().getOnlinePlayers()) {

				if (online.hasPermission("tntfill.debug")) {
					online.sendMessage(ChatColor.LIGHT_PURPLE + "DEBUG: " + ChatColor.WHITE + msg);
				}

			}
		}
	}

	public int countTnt(Player p) {

		int count = 0;

		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.TNT) {

					count += it.getAmount();

				}

			}

		}

		return count;

	}

	public void sendFillHelpMenu(Player p) {

		List<String> menu = this.getLangConfig().getStringList("normalFillHelp");

		if (menu.size() < 15) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getLangConfig().getString("helpError")));
			return;
		}

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(0)));

		if (p.hasPermission("tntfill.use.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(2)));
		} else if (p.hasPermission("tntfill.use")) {
			String m = menu.get(1);
			m = m.replace("{defaultFillRadius}", String.valueOf(this.defaultFillRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}

		if (p.hasPermission("tntfill.auto.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(4)));
		} else if (p.hasPermission("tntfill.auto")) {
			String m = menu.get(3);
			m = m.replace("{defaultFillRadius}", String.valueOf(this.defaultFillRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}

		if (p.hasPermission("tntfill.count.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(6)));
		} else if (p.hasPermission("tntfill.count")) {
			String m = menu.get(5);
			m = m.replace("{defaultFillRadius}", String.valueOf(this.defaultFillRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}

		if (p.hasPermission("tntfill.fillselected.auto")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(8)));
		} else if (p.hasPermission("tntfill.fillselected")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(7)));
		}

		if (p.hasPermission("tntfill.select")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(9)));
		}

		if (p.hasPermission("tntfill.reload")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(10)));
		}

		if (p.hasPermission("tntfill.bypass")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(11)));
		} else {
			String m = menu.get(12);
			m = m.replace("{maxFillRadius}", String.valueOf(this.maxFillRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(13)));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(14)));

		if (this.getCustomConfig().getBoolean("helpSuffix")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getLangConfig().getString("helpSuffix")));
		}

	}

	public void sendFillSelectHelpMenu(Player p) {

		List<String> menu = this.getLangConfig().getStringList("selectHelp");

		if (menu.size() < 7) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getLangConfig().getString("helpError")));
			return;
		}

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(0)));

		if (p.hasPermission("tntfill.select")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(1)));
		}
		if (p.hasPermission("tntfill.select.clear")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(2)));
		}
		if (p.hasPermission("tntfill.select.undo")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(3)));
		}
		if (p.hasPermission("tntfill.select.done")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(4)));
		}
		if (p.hasPermission("tntfill.select.list")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(5)));
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(6)));

		if (this.getCustomConfig().getBoolean("helpSuffix")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', this.getLangConfig().getString("helpSuffix")));
		}
	}

	public void addLocation(Player p, Location loc) {

		if (this.isSelecting.contains(p.getUniqueId().toString())) {

			if (!this.locs.containsKey(p.getUniqueId().toString())) {

				LinkedList<Location> locs = new LinkedList<Location>();

				locs.add(loc);

				this.locs.put(p.getUniqueId().toString(), locs);

				p.sendMessage(this.lm.getLangMessage("dispenserSelected", -1, -1));

			} else {

				int size = this.locs.get(p.getUniqueId().toString()).size();

				if (size == this.getCustomConfig().getInt("maxDispenserSelect")) {

					if (p.hasPermission("tntfill.maxdispensers.bypass")) {

						LinkedList<Location> locs = this.locs.get(p.getUniqueId().toString());

						if (locs.contains(loc)) {
							p.sendMessage(this.lm.getLangMessage("dispenserAlreadySelected", -1, -1));
							return;
						}

						locs.add(loc);

						this.locs.put(p.getUniqueId().toString(), locs);

						p.sendMessage(this.lm.getLangMessage("dispenserSelected", -1, -1));

					} else {

						String message = this.lm.getUntranslatedMessage("maxDispensersSelected");
						message = message.replace("{dispensers}", size + "");
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

					}
				} else {

					LinkedList<Location> locs = this.locs.get(p.getUniqueId().toString());

					if (locs.contains(loc)) {
						p.sendMessage(this.lm.getLangMessage("dispenserAlreadySelected", -1, -1));
						return;
					}

					locs.add(loc);

					this.locs.put(p.getUniqueId().toString(), locs);

					p.sendMessage(this.lm.getLangMessage("dispenserSelected", -1, -1));

				}

			}

		}

	}

}