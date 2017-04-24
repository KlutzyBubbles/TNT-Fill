package com.leetzilantonis.tntfill;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangManager {

	Main plugin;

	private FileConfiguration langConfig = null;
	private File langConfigFile = null;

	public LangManager(Main instance) {
		plugin = instance;
	}

	public void initLangConfig() {

		FileConfiguration l = this.getLangConfig();
		
		l.addDefault("notPlayer", "{prefix} &cYou must be a player to do that!");
		l.addDefault("tooManyArguments", "{prefix} &cToo many arguments! try /tntfill ?");
		l.addDefault("tooFewArguments", "{prefix} &cToo few arguments! try /tntfill ?");
		l.addDefault("error", "{prefix} &cIncorrect arguments try /tntfill ?");
		l.addDefault("errorFilling", "{prefix} &cAn unknown error occured, contact staff immidietly!");
		l.addDefault("exitSelectMode", "{prefix} &cYou must exit select mode before you can break dispensers in creative!");
		l.addDefault("noDispensers", "{prefix} &cThere are no dispensers within a {radius} block radius");
		l.addDefault("noTNT", "{prefix} &cYou do not have any TNT!");
		l.addDefault("onePerDispenser", "{prefix} &cYou need at-least 1 TNT for each of the {dispensers} dispensers!");
		l.addDefault("dispensersFilled", "{prefix} &aAll dispensers have been filled!");
		l.addDefault("notNumber", "{prefix} &cPlease only use a number for the amount or radius!");
		l.addDefault("maxFillRadius", "{prefix} &cPlease use a radius smaller than {maxFillRadius}");
		l.addDefault("notEnoughTNT", "{prefix} &cYou do not have enough TNT to do that!");
		l.addDefault("reloaded", "{prefix} &aAll configurations have been reloaded!");
		l.addDefault("countDispensers", "{prefix} &aThere are {dispensers} dispensers in a {radius} block radius!");
		l.addDefault("autoCreative", "{prefix} &cYou must have TNT in your inventory to use /tntfill auto in creative!");
		l.addDefault("alreadySelecting", "{prefix} &cYou are already selecting dispensers! select them by left clicking them!");
		l.addDefault("addedToSelecting", "{prefix} &7You can now &6left click &7 dispensers to select them!");
		l.addDefault("notSelecting", "{prefix} &cYou must be in selecting mode to use that command!");
		l.addDefault("nothingToUndo", "{prefix} &cThere is nothing to undo!");
		l.addDefault("nothingToClear", "{prefix} &cThere is nothing to clear!");
		l.addDefault("selectionUndone", "{prefix} &7Your previous selection has been undone!");
		l.addDefault("selectionsCleared", "{prefix} &7Your selection(s) have been cleared!");
		l.addDefault("maxDispensersSelected", "{prefix} &cYou cannot select more than {dispensers} dispensers!");
		l.addDefault("dispenserSelected", "{prefix} &7That dispenser has been selected!");
		l.addDefault("dispenserAlreadySelected", "{prefix} &cThat dispenser has already been selected, please try another one!");
		l.addDefault("noDispensersToFill", "{prefix} &cThere are no dispensers to fill, &7try &6Left Clicking &7a dispenser to select it!");
		l.addDefault("selectingDone", "{prefix} &7You have now exited selecting mode, use /tntfill select to enable it again");
		l.addDefault("selectionListTitle", "&7-----&r {prefix} &f: &6Dispensers Selected &7-----");
		l.addDefault("selectionListItem", "&6{number}. &7Location: &6{location}&7, TNT-in: &6{amountFilled}");
		l.addDefault("selectionListNoDispensers", "&cYou have no dispensers selected!");
		l.addDefault("selectionListSuffix", "&7---------- &6By LeePMC &7----------");
		l.addDefault("selectedDispenserBroken", "{prefix} &7You broke a dispenser that was selected by {player}");
		l.addDefault("noPermission", "&cYou do not have permission to do that!");
		l.addDefault("helpSuffix", "&6Plugin by &bLeePMC &7- &6Buy it on SpigotMC.org!");
		l.addDefault("pluginIsUpdated", "&6TNT-Fill &7is up to date with version {currentVersion}!");
		l.addDefault("pluginNeedsUpdates", "&6TNT-Fill &cis not up to date. Current installed: {currentVersion}, Avalible: {newVersion}");
		l.addDefault("dispensersFilledFaction", "{prefix} &aAll dispensers have been filled except for the ones in non ally factions!");
		
		l.addDefault("dispensersCleared", "{prefix} &aAll TNT has been cleared out of {dispensers} dispensers");
		l.addDefault("dispensersPartCleared", "{prefix} &aTNT cleared! but you didnt have enough space to take all of it!");
		l.addDefault("errorClearing", "{prefix} &cAn unknown error occured while clearing, contact staff imidietly!");
		l.addDefault("clearNoTNT", "{prefix} &cThere is no TNT left to clear!");
		l.addDefault("noSpace", "{prefix} &cYou do not have any space to clear TNT!");

		List<String> normalFillHelp = new ArrayList<String>();
		normalFillHelp.add("&7--------- &8[&6TNT-Fill : Help&8]&7 -----------------");
		normalFillHelp.add("&6/tntfill <#>&7: Fills all dispensers in a {defaultRadius} block radius with the specified amount of TNT (&6#&7)");
		normalFillHelp.add("&6/tntfill <#> [RADIUS]&7: Fills all dispensers in a specified block radius with the specified amount of TNT (&6#&7)");
		normalFillHelp.add("&6/tntfill auto&7: Spreads all of your TNT equally into each dispenser in a {defaultRadius} block radius");
		normalFillHelp.add("&6/tntfill auto [RADIUS]&7: Spreads all of your TNT equally into each dispenser in a specified block radius");
		normalFillHelp.add("&6/tntfill count&7: Counts the amount of dispensers in a {defaultRadius} block radius");
		normalFillHelp.add("&6/tntfill count [RADIUS]&7: Counts the amount of dispensers in a specified block radius");
		normalFillHelp.add("&6/tntfill fill <#>&7: Fills all selected dispensers with the specified amount of TNT (&6#&7)");
		normalFillHelp.add("&6/tntfill fill [auto/<#>]&7: Spreads all of your TNT equally into each selected dispenser");
		normalFillHelp.add("&6/tntfill select ?&7: Display the help menu for all /tntfill select commands");
		normalFillHelp.add("&6/tntfill reload&7: Reload TNT-Fill configuration. DO NOT SPAM");
		normalFillHelp.add("&6Maximum Radius&7: Unlimited (try not to go over 100)");
		normalFillHelp.add("&6Maximum Radius&7: {maxFillRadius}");
		normalFillHelp.add("&6Legend&7: [] = Optional, <> = Required, CAPS/# = Number, Lower case = words");
		normalFillHelp.add("&6Aliases&7: tntfill, tf, fillaround, loadtnt");
		l.addDefault("normalFillHelp", normalFillHelp);
		
		List<String> normalClearHelp = new ArrayList<String>();
		normalClearHelp.add("&7--------- &8[&6TNT-Fill : Help&8]&7 -----------------");
		normalClearHelp.add("&6/tntclear <#>&7: Clears all dispensers in a {defaultRadius} block radius with the specified amount of TNT (&6#&7)");
		normalClearHelp.add("&6/tntclear <#> [RADIUS]&7: Clears all dispensers in a specified block radius with the specified amount of TNT (&6#&7)");
		normalClearHelp.add("&6/tntclear auto&7: Clears an equal amount of TNT from each dispenser in a {defaultRadius} block radius");
		normalClearHelp.add("&6/tntclear auto [RADIUS]&7: Clears an equal amount of TNT from each dispenser in a {defaultRadius} specified block radius");
		normalClearHelp.add("&6/tntclear count&7: Counts the amount of dispensers in a {defaultRadius} block radius");
		normalClearHelp.add("&6/tntclear count [RADIUS]&7: Counts the amount of dispensers in a specified block radius");
		normalClearHelp.add("&6/tntclear fill <#>&7: Fills all selected dispensers with the specified amount of TNT (&6#&7)");
		normalClearHelp.add("&6/tntclear fill [auto/<#>]&7: Spreads all of your TNT equally into each selected dispenser");
		normalClearHelp.add("&6/tntclear select ?&7: Display the help menu for all /tntfill select commands");
		normalClearHelp.add("&6/tntclear reload&7: Reload TNT-Fill configuration. DO NOT SPAM");
		normalClearHelp.add("&6Maximum Radius&7: Unlimited (try not to go over 100)");
		normalClearHelp.add("&6Maximum Radius&7: {maxFillRadius}");
		normalClearHelp.add("&6Legend&7: [] = Optional, <> = Required, CAPS/# = Number, Lower case = words");
		normalClearHelp.add("&6Aliases&7: tntclear, tc, cleararound, cleartnt");
		l.addDefault("normalFillHelp", normalClearHelp);

		List<String> selectHelp = new ArrayList<String>();
		selectHelp.add("&7--------- &8[&6TNT-Fill : Select Help&8]&7 -------------");
		selectHelp.add("&6/tntfill select&7: Enable select mode and select dispensers by left clicking on them");
		selectHelp.add("&6/tntfill select clear&7: Clear all selected dispensers. CANNOT BE UNDONE!");
		selectHelp.add("&6/tntfill select undo&7: Undo your previously selected dispenser");
		selectHelp.add("&6/tntfill select done&7: Disable select mode, this will clear all currently selected dispensers");
		selectHelp.add("&6/tntfill select list&7: List all currently selected dispensers and their locations in selected order");
		selectHelp.add("&6Aliases&7: /tntfill [ select / sel / s ]");
		l.addDefault("selectHelp", selectHelp);
        
		l.addDefault("helpError", "&cThe help menu is currently unavailable, please contact staff immediately");
		l.addDefault("prefix", "&7[&aTNT-Fill&7]");
        
		l.options().copyDefaults(true);
		this.saveLangConfig();

	}        

	public void reloadLangConfig() {
		if (langConfigFile == null) {
			langConfigFile = new File(plugin.getDataFolder(), "lang.yml");
		}
		langConfig = YamlConfiguration.loadConfiguration(langConfigFile);

		// Look for defaults in the jar
		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(plugin.getResource("lang.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(langConfigFile);
			langConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getLangConfig() {
		if (langConfig == null) {
			reloadLangConfig();
		}
		return langConfig;
	}

	public void saveLangConfig() {
		if (langConfig == null || langConfigFile == null) {
			return;
		}
		try {
			getLangConfig().save(langConfigFile);
		} catch (IOException ex) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + langConfigFile, ex);
		}
	}

	public void saveDefaultLangConfig() {
		if (langConfigFile == null) {
			langConfigFile = new File(plugin.getDataFolder(), "lang.yml");
		}
		if (!langConfigFile.exists()) {            
			this.plugin.saveResource("lang.yml", false);
		}
	}
	// End Custom Config

	public String getLangMessage(String path, int radius, int dispensers) {

		FileConfiguration lang = this.getLangConfig();

		String message = lang.getString(path);
		message = message.replace("{prefix}", lang.getString("prefix"));
		message = message.replace("{radius}", String.valueOf(radius));
		message = message.replace("{dispensers}", String.valueOf(dispensers));
		message = message.replace("{maxFillRadius}", plugin.maxFillRadius + "");
		message = message.replace("{defaultFillRadius}", plugin.defaultFillRadius + "");
		message = ChatColor.translateAlternateColorCodes('&', message);

		return message;

	}

	public String getUntranslatedMessage(String path) {

		FileConfiguration lang = this.getLangConfig();

		String message = lang.getString(path);
		message = message.replace("{prefix}", lang.getString("prefix"));

		return message;

	}

	public String getMessage(String path) {


		FileConfiguration lang = this.getLangConfig();

		String message = lang.getString(path);
		message = message.replace("{prefix}", lang.getString("prefix"));

		return ChatColor.translateAlternateColorCodes('&', message);
	}

}
