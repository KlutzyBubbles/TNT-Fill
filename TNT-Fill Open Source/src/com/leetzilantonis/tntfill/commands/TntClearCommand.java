package com.leetzilantonis.tntfill.commands;

import com.leetzilantonis.tntfill.Main;

public class TntClearCommand {

	Main plugin;

	public TntClearCommand(Main instance) {
		plugin = instance;
	}
/*
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!sender.hasPermission("tntclear.use")) {

			sender.sendMessage(plugin.lm.getMessage("noPermission"));
			return true;

		} else {

			if (!(sender instanceof Player)) {

				sender.sendMessage(plugin.lm.getMessage("notPlayer"));
				return true;

			} else {

				Player p = (Player) sender;

				if (args.length == 0) {

					this.sendHelpMenu(p);
					return true;

				} else {

					if (args[0].equalsIgnoreCase("auto")) {

						if (!p.hasPermission("tntclear.auto")) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						int radius = plugin.defaultClearRadius;

						if (args.length == 2) {

							if (!(p.hasPermission("tntclear.auto.customradius"))) {

								p.sendMessage(plugin.lm.getMessage("noPermission"));
								return true;

							}

							if (!this.isInt(args[1])) {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							} else {

								radius = Integer.parseInt(args[1]);
								if (radius > plugin.maxClearRadius) {

									if (!p.hasPermission("tntclear.bypass")) {

										p.sendMessage(plugin.lm.getMessage("maxClearRadius"));
										return true;

									}

								}

							}

						} else if (args.length > 2) {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

						int dis = plugin.rf.countDispensers(p, radius);
						int space = plugin.rc.quickCount(p);

						if (dis == 0) {

							p.sendMessage(plugin.lm.getLangMessage("noDispensers", radius, dis));
							return true;

						}

						if (this.isCreative(p)) {

							ClearResult r = plugin.rc.clearDispensersCreative(p, plugin.defaultClearRadius);

							if (r == ClearResult.CLEARED) {

								p.sendMessage(plugin.lm.getLangMessage("dispensersCleared", radius, dis));
								return true;

							} else if (r == ClearResult.NO_TNT) {

								p.sendMessage(plugin.lm.getLangMessage("clearNoTNT", radius, dis));
								return true;

							} else if (r == ClearResult.PART_CLEARED) {

								p.sendMessage(plugin.lm.getLangMessage("dispensersPartCleared", radius, dis));
								return true;

							} else {

								p.sendMessage(plugin.lm.getLangMessage("errorClearing", radius, dis));
								return true;

							}

						} else {

							if (space == 0) {

								p.sendMessage(plugin.lm.getLangMessage("noSpace", radius, dis));
								return true;

							}

							int left = space % dis;
							space -= left;
							int per = space / dis;

							ClearResult r = plugin.rc.clearDispensersSurvival(p, plugin.defaultClearRadius, per);

							if (r == ClearResult.CLEARED) {

								p.sendMessage(plugin.lm.getLangMessage("dispensersCleared", radius, dis));
								return true;

							} else if (r == ClearResult.NO_TNT) {

								p.sendMessage(plugin.lm.getLangMessage("clearNoTNT", radius, dis));
								return true;

							} else if (r == ClearResult.PART_CLEARED) {

								p.sendMessage(plugin.lm.getLangMessage("dispensersPartCleared", radius, dis));
								return true;

							} else {

								p.sendMessage(plugin.lm.getLangMessage("errorClearing", radius, dis));
								return true;

							}

						}

					} else if (args[0].equalsIgnoreCase("all")) {

						if (!p.hasPermission("tntclear.all")) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						int radius = plugin.defaultClearRadius;

						if (args.length == 2) {

							if (!(p.hasPermission("tntclear.all.customradius"))) {

								p.sendMessage(plugin.lm.getMessage("noPermission"));
								return true;

							}

							if (!this.isInt(args[1])) {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							} else {

								radius = Integer.parseInt(args[1]);
								if (radius > plugin.maxClearRadius) {

									if (!p.hasPermission("tntclear.bypass")) {

										p.sendMessage(plugin.lm.getMessage("maxClearRadius"));
										return true;

									}

								}

							}

						} else if (args.length > 2) {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

						int dis = plugin.rf.countDispensers(p, radius);

						ClearResult r = plugin.rc.clearDispensersSurvival(p, plugin.defaultClearRadius);

						if (r == ClearResult.CLEARED) {

							p.sendMessage(plugin.lm.getLangMessage("dispensersCleared", radius, dis));
							return true;

						} else if (r == ClearResult.NO_TNT) {

							p.sendMessage(plugin.lm.getLangMessage("clearNoTNT", radius, dis));
							return true;

						} else if (r == ClearResult.PART_CLEARED) {

							p.sendMessage(plugin.lm.getLangMessage("dispensersPartCleared", radius, dis));
							return true;

						} else {

							p.sendMessage(plugin.lm.getLangMessage("errorClearing", radius, dis));
							return true;

						}

					} 
					ClearResult r = plugin.rc.clearDispensersSurvival(p, plugin.defaultClearRadius);

					if (r == ClearResult.CLEARED) {

						p.sendMessage(plugin.lm.getMessage("dispensersCleared"));
						return true;

					} else if (r == ClearResult.NO_TNT) {

						p.sendMessage(plugin.lm.getMessage("clearNoTNT"));
						return true;

					} else if (r == ClearResult.PART_CLEARED) {

						p.sendMessage(plugin.lm.getMessage("dispensersPartCleared"));
						return true;

					} else {

						p.sendMessage(plugin.lm.getMessage("errorClearing"));
						return true;

					}

				}

			}

		}

	}

	private boolean isInt(String num) {

		try {
			Integer.parseInt(num);
		} catch (NumberFormatException e){
			return false;
		}
		return true;

	}

	public void sendHelpMenu(Player p) {

		List<String> menu = plugin.getLangConfig().getStringList("normalHelp");

		if (menu.size() < 15) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("helpError")));
			return;
		}

		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(0)));

		if (p.hasPermission("tntfill.use.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(2)));
		} else if (p.hasPermission("tntfill.use")) {
			String m = menu.get(1);
			m = m.replace("{defaultClearRadius}", String.valueOf(plugin.defaultClearRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}

		if (p.hasPermission("tntfill.auto.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(4)));
		} else if (p.hasPermission("tntfill.auto")) {
			String m = menu.get(3);
			m = m.replace("{defaultClearRadius}", String.valueOf(plugin.defaultClearRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}

		if (p.hasPermission("tntfill.count.customradius")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(6)));
		} else if (p.hasPermission("tntfill.count")) {
			String m = menu.get(5);
			m = m.replace("{defaultClearRadius}", String.valueOf(plugin.defaultClearRadius));
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
			m = m.replace("{maxClearRadius}", String.valueOf(plugin.maxClearRadius));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(13)));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', menu.get(14)));

		if (plugin.getCustomConfig().getBoolean("helpSuffix")) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getLangConfig().getString("helpSuffix")));
		}

	}

	private boolean isCreative(Player p) {

		return p.getGameMode().equals(GameMode.CREATIVE);

	}
*/
}
