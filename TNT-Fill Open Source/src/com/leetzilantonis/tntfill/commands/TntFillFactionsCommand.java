package com.leetzilantonis.tntfill.commands;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.leetzilantonis.tntfill.FillResult;
import com.leetzilantonis.tntfill.Main;

public class TntFillFactionsCommand implements CommandExecutor {

	Main plugin;

	public TntFillFactionsCommand (Main instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!sender.hasPermission("tntfill.use")) {

			sender.sendMessage(plugin.lm.getMessage("noPermission"));
			return true;

		} else {

			if (!(sender instanceof Player)) {

				sender.sendMessage(plugin.lm.getMessage("notPlayer"));
				return true;

			} else {

				Player p = (Player) sender;

				if (args.length == 0) {

					plugin.sendFillHelpMenu(p);
					return true;

				} else {

					String uuid = p.getUniqueId().toString();
					
					if (args[0].equalsIgnoreCase("auto") || args[0].equalsIgnoreCase("a")) {

						if (!(p.hasPermission("tntfill.auto"))) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (args.length == 1) {

							int d = plugin.rff.countDispensers(p, plugin.defaultFillRadius);

							if (d == 0) {

								p.sendMessage(plugin.lm.getLangMessage("noDispensers", plugin.defaultFillRadius, d));
								return true;

							} else {

								int t = plugin.countTnt(p);

								if (t == 0) {

									p.sendMessage(plugin.lm.getMessage("noTNT"));
									return true;

								} else if (t < d) {

									p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
									return true;

								} else {

									int remove = t - (t % d);

									if (this.isCreative(p)) {


										FillResult fr = plugin.rff.fillDispensersCreative(p, plugin.defaultFillRadius, remove / d);

										if (fr == FillResult.SUCCESS) {

											p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
											return true;

										} else if (fr == FillResult.SUCCESS_FACTION) {
											
											p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
											return true;
											
										} else {

											p.sendMessage(plugin.lm.getMessage("errorFilling"));
											return true;

										}

									} else {

										FillResult fr = plugin.rff.fillDispensersSurvival(p, plugin.defaultFillRadius, remove / d, remove);

										if (fr == FillResult.NO_TNT) {

											p.sendMessage(plugin.lm.getMessage("noTNT"));
											return true;

										} else if (fr == FillResult.NOT_ENOUGH_TNT) {

											p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
											return true;

										} else if (fr == FillResult.SUCCESS) {

											p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
											return true;

										} else if (fr == FillResult.SUCCESS_FACTION) {
											
											p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
											return true;
											
										} else {

											p.sendMessage(plugin.lm.getMessage("errorFilling"));
											return true;

										}

									}

								}

							}

						} else if (args.length == 2) {

							if (!(p.hasPermission("tntfill.auto.customradius"))) {

								p.sendMessage(plugin.lm.getMessage("noPermission"));
								return true;

							}

							if (this.isInt(args[1])) {

								int radius = Integer.parseInt(args[1]);

								if (radius > plugin.maxFillRadius) {

									if (!p.hasPermission("tntfill.bypass")) {

										p.sendMessage(plugin.lm.getMessage("maxFillRadius"));
										return true;

									}

								}

								int d = plugin.rff.countDispensers(p, radius);

								if (d == 0) {

									p.sendMessage(plugin.lm.getLangMessage("noDispensers", radius, d));

								} else {

									int t = plugin.countTnt(p);

									if (t == 0) {

										p.sendMessage(plugin.lm.getMessage("noTNT"));
										return true;

									} else if (t < d) {

										p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", radius, d));
										return true;

									} else {

										int remove = t - (t % d);

										if (this.isCreative(p)) {


											FillResult fr = plugin.rff.fillDispensersCreative(p, radius, remove / d);

											if (fr == FillResult.SUCCESS) {

												p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", radius, d));
												return true;

											} else if (fr == FillResult.SUCCESS_FACTION) {
												
												p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
												return true;
												
											} else {

												p.sendMessage(plugin.lm.getMessage("errorFilling"));
												return true;

											}

										} else {

											FillResult fr = plugin.rff.fillDispensersSurvival(p, radius, remove / d, remove);

											if (fr == FillResult.NO_TNT) {

												p.sendMessage(plugin.lm.getMessage("noTNT"));
												return true;

											} else if (fr == FillResult.NOT_ENOUGH_TNT) {

												p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", radius, d));
												return true;

											} else if (fr == FillResult.SUCCESS_FACTION) {
												
												p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
												return true;
												
											} else if (fr == FillResult.SUCCESS) {

												p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", radius, d));
												return true;

											} else {

												p.sendMessage(plugin.lm.getMessage("errorFilling"));
												return true;

											}

										}

									}

								}

							} else {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							}

						} else {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

					} else if (args[0].equalsIgnoreCase("count") || args[0].equalsIgnoreCase("c")) {

						if (!(p.hasPermission("tntfill.count"))) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (args.length == 1) {

							p.sendMessage(plugin.lm.getLangMessage("countDispensers", plugin.defaultFillRadius, plugin.rff.countDispensers(p, plugin.defaultFillRadius)));
							return true;

						} else if (args.length == 2) {

							if (!(p.hasPermission("tntfill.count.customradius"))) {

								p.sendMessage(plugin.lm.getMessage("noPermission"));
								return true;

							}

							if (this.isInt(args[1])) {

								int radius = Integer.parseInt(args[1]);

								if (radius > plugin.maxFillRadius) {

									if (!p.hasPermission("tntfill.bypass")) {

										p.sendMessage(plugin.lm.getMessage("maxFillRadius"));
										return true;

									}

								}

								p.sendMessage(plugin.lm.getLangMessage("countDispensers", radius, plugin.rff.countDispensers(p, radius)));
								return true;

							} else {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							}

						} else {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

					} else if (args[0].equalsIgnoreCase("fill") || args[0].equalsIgnoreCase("f")) {

						if (!p.hasPermission("tntfill.fillselected")) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (plugin.isSelecting.contains(uuid)) {

							if (args.length == 1) {

								p.sendMessage(plugin.lm.getMessage("tooFewArguments"));
								return true;

							} else if (args.length == 2) {

								if (!plugin.locs.containsKey(uuid)) {

									p.sendMessage(plugin.lm.getMessage("noDispensersToFill"));
									return true;

								} else if (plugin.locs.get(uuid).size() == 0) {

									p.sendMessage(plugin.lm.getMessage("noDispensersToFill"));
									return true;

								} else {

									int t = plugin.countTnt(p);
									int d = plugin.locs.get(uuid).size();

									if (args[1].equalsIgnoreCase("auto")) {

										if (t == 0) {

											p.sendMessage(plugin.lm.getMessage("noTNT"));
											return true;

										} else if (t < d) {

											p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", -1, d));
											return true;

										} else {

											int remove = t - (t % d);

											if (this.isCreative(p)) {


												FillResult fr = plugin.sff.fillSelectedDispensersCreative(p, plugin.locs.get(uuid), remove / d);

												if (fr == FillResult.SUCCESS) {

													p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
													return true;

												} else {

													p.sendMessage(plugin.lm.getMessage("errorFilling"));
													return true;

												}

											} else {

												FillResult fr = plugin.sff.fillSelectedDispensersSurvival(p, plugin.locs.get(uuid), remove / d, remove);

												if (fr == FillResult.NO_TNT) {

													p.sendMessage(plugin.lm.getMessage("noTNT"));
													return true;

												} else if (fr == FillResult.NOT_ENOUGH_TNT) {

													p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
													return true;

												} else if (fr == FillResult.SUCCESS) {

													p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
													return true;

												} else {

													p.sendMessage(plugin.lm.getMessage("errorFilling"));
													return true;

												}

											}

										}

									} else {

										if (this.isInt(args[1])) {

											int per = Integer.parseInt(args[1]);

											if (this.isCreative(p)) {

												FillResult fr = plugin.sff.fillSelectedDispensersCreative(p, plugin.locs.get(uuid), per);

												if (fr == FillResult.SUCCESS) {

													p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", -1, d));
													return true;

												} else {

													p.sendMessage(plugin.lm.getMessage("errorFilling"));
													return true;

												}

											} else {

												if (t == 0) {

													p.sendMessage(plugin.lm.getMessage("noTNT"));
													return true;

												} else if (t < d) {

													p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", -1, d));
													return true;

												} else if (per * d > t) {
													
													p.sendMessage(plugin.lm.getLangMessage("notEnoughTNT", -1, d));
													return true;
													
												} else {
													
													FillResult fr = plugin.sff.fillSelectedDispensersSurvival(p, plugin.locs.get(uuid), per, per * d);

													if (fr == FillResult.NO_TNT) {

														p.sendMessage(plugin.lm.getMessage("noTNT"));
														return true;

													} else if (fr == FillResult.NOT_ENOUGH_TNT) {

														p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
														return true;

													} else if (fr == FillResult.SUCCESS) {

														p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
														return true;

													} else {

														p.sendMessage(plugin.lm.getMessage("errorFilling"));
														return true;

													}
													
												}

											}

										} else {

											p.sendMessage(plugin.lm.getMessage("notNumber"));
											return true;

										}
									}
								}

							} else {

								p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
								return true;

							}

						} else {

							p.sendMessage(plugin.lm.getMessage("notSelecting"));
							return true;

						}

					} else if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("sel") || args[0].equalsIgnoreCase("s")) {

						if (!p.hasPermission("tntfill.select")) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (args.length == 1) {

							if (plugin.isSelecting.contains(uuid)) {

								if (plugin.locs.containsKey(uuid)) {
									plugin.locs.remove(uuid);
								}
								
								plugin.isSelecting.remove(uuid);
								p.sendMessage(plugin.lm.getMessage("selectingDone"));
								return true;

							} else {

								plugin.isSelecting.add(uuid);
								p.sendMessage(plugin.lm.getMessage("addedToSelecting"));
								return true;

							}

						} else {

							if (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("c")) {

								if (!p.hasPermission("tntfill.select.clear")) {

									p.sendMessage(plugin.lm.getMessage("noPermission"));
									return true;

								} else {

									if (plugin.isSelecting.contains(uuid)) {
										
										if (!plugin.locs.containsKey(uuid)) {
											
											p.sendMessage(plugin.lm.getMessage("nothingToClear"));
											return true;
											
										} else {
											
											if (plugin.locs.get(uuid).size() == 0) {
												
												p.sendMessage(plugin.lm.getMessage("nothingToClear"));
												return true;
												
											} else {
												
												plugin.locs.remove(uuid);
												p.sendMessage(plugin.lm.getMessage("selectionsCleared"));
												return true;
												
											}
											
										}
										
									} else {
										
										p.sendMessage(plugin.lm.getMessage("notSelecting"));
										return true;
										
									}

								}

							} else if (args[1].equalsIgnoreCase("undo") || args[1].equalsIgnoreCase("u") || args[1].equalsIgnoreCase("<")) {

								if (!p.hasPermission("tntfill.select.undo")) {

									p.sendMessage(plugin.lm.getMessage("noPermission"));
									return true;

								} else {

									if (!plugin.locs.containsKey(uuid)) {
										
										p.sendMessage(plugin.lm.getMessage("nothingToUndo"));
										return true;
										
									} else {
										
										LinkedList<Location> locs = plugin.locs.get(uuid);
										LinkedList<Location> newLocs = new LinkedList<Location>();
										
										if (locs.size() == 0) {
											
											plugin.locs.remove(uuid);
											p.sendMessage(plugin.lm.getMessage("nothingToUndo"));
											return true;
											
										} else {
										
											for (int i = 0; i < locs.size(); i++) {
												
												if (i != locs.size() - 1) {
													
													newLocs.add(locs.get(i));
													
												}
												
											}
											
											plugin.locs.put(uuid, newLocs);
											p.sendMessage(plugin.lm.getMessage("selectionUndone"));
											return true;
											
										}
									}
								}

							} else if (args[1].equalsIgnoreCase("done") || args[1].equalsIgnoreCase("d")) {

								if (plugin.isSelecting.contains(uuid)) {

									if (plugin.locs.containsKey(uuid)) {
										plugin.locs.remove(uuid);
									}
									
									plugin.isSelecting.remove(uuid);
									p.sendMessage(plugin.lm.getMessage("selectingDone"));
									return true;

								} else {

									p.sendMessage(plugin.lm.getMessage("notSelecting"));
									return true;

								}

							} else if (args[1].equalsIgnoreCase("list") || args[1].equalsIgnoreCase("l")) {

								if (!p.hasPermission("tntfill.select.list")) {

									p.sendMessage(plugin.lm.getMessage("noPermission"));
									return true;

								} else {

									if (!plugin.isSelecting.contains(uuid)) {
										
										p.sendMessage(plugin.lm.getMessage("notSelecting"));
										return true;
										
									} else if (!plugin.locs.containsKey(uuid)) {
										
										p.sendMessage(plugin.lm.getMessage("selectionListTitle"));
										p.sendMessage(plugin.lm.getMessage("selectionListNoDispensers"));
										p.sendMessage(plugin.lm.getMessage("selectionListSuffix"));
										return true;
										
									} else if (plugin.locs.get(uuid).size() == 0) {
										
										p.sendMessage(plugin.lm.getMessage("selectionListTitle"));
										p.sendMessage(plugin.lm.getMessage("selectionListNoDispensers"));
										p.sendMessage(plugin.lm.getMessage("selectionListSuffix"));
										plugin.locs.remove(uuid);
										return true;
										
									} else {
										
										LinkedList<Location> list = plugin.locs.get(uuid);
										
										p.sendMessage(plugin.lm.getMessage("selectionListTitle"));
										
										int count = 0;
										
										for (Location loc : list) {
											
											count++;
											int x = loc.getBlockX();
											int y = loc.getBlockY();
											int z = loc.getBlockZ();
											int amount = 0;
											
											Block b = loc.getBlock();
											
											if (b.getType() == Material.DISPENSER) {
												
												InventoryHolder dispenser = (InventoryHolder) b.getState();
												
												for (ItemStack s : dispenser.getInventory().getContents()) {
													if (s != null && s.getType() == Material.TNT) {
														amount += s.getAmount();
													}
												}
												
											} else {
												amount = -1;
											}
											
											String location = "X: " + x + " Y: " + y + " Z: " + z;
											String message = plugin.lm.getUntranslatedMessage("selectionListItem");
											message = message.replace("{prefix}", plugin.lm.getUntranslatedMessage("prefix"));
											message = message.replace("{location}", location);
											message = message.replace("{number}", "" + count);
											if (amount == -1) {
												
												message = message.replace("{amountFilled}", "NOT DISPENSER");
												
											} else {
												
												message = message.replace("{amountFilled}", "" + amount);
												
											}
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
											
										}
										
										p.sendMessage(plugin.lm.getMessage("selectionListSuffix"));
										return true;
									}
									
								}

							} else if (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("h") || args[1].equalsIgnoreCase("?")) {
								
								if (args.length == 1) {
									
									plugin.sendFillSelectHelpMenu(p);
									return true;
									
								} else {
									
									p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
									return true;
									
								}
								
							} else {

								p.sendMessage(plugin.lm.getMessage("error"));
								return true;

							}

						}

					} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {

						if (!(p.hasPermission("tntfill.reload"))) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (args.length == 1) {

							plugin.reloadCustomConfig();
							plugin.reloadLangConfig();
							plugin.defaultFillRadius = plugin.defaultFillRadius;
							plugin.maxFillRadius = plugin.maxFillRadius;
							p.sendMessage(plugin.lm.getMessage("reloaded"));
							return true;

						} else {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

					} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("?")) {
						
						if (args.length == 1) {
							
							plugin.sendFillHelpMenu(p);
							return true;
							
						} else {
							
							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;
							
						}
						
					} else {

						if (!(p.hasPermission("tntfill.use"))) {

							p.sendMessage(plugin.lm.getMessage("noPermission"));
							return true;

						}

						if (args.length == 1) {

							if (this.isInt(args[0])) {

								int d = plugin.rff.countDispensers(p, plugin.defaultFillRadius);
								int per = Integer.parseInt(args[0]);
								
								if (d == 0) {

									p.sendMessage(plugin.lm.getLangMessage("noDispensers", plugin.defaultFillRadius, d));
									return true;

								} else {

									if (this.isCreative(p)) {
										
										FillResult fr = plugin.rff.fillDispensersCreative(p, plugin.defaultFillRadius, per);

										if (fr == FillResult.SUCCESS) {

											p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
											return true;

										} else if (fr == FillResult.SUCCESS_FACTION) {
											
											p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
											return true;
											
										} else {

											p.sendMessage(plugin.lm.getMessage("errorFilling"));
											return true;

										}

									} else {

										int t = plugin.countTnt(p);

										if (t == 0) {

											p.sendMessage(plugin.lm.getMessage("noTNT"));
											return true;

										} else if (t < d) {

											p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
											return true;

										} else {

											if (per * d > t) {

												p.sendMessage(plugin.lm.getLangMessage("notEnoughTNT", plugin.defaultFillRadius, d));
												return true;

											} else {

												FillResult fr = plugin.rff.fillDispensersSurvival(p, plugin.defaultFillRadius, per, per * d);

												if (fr == FillResult.NO_TNT) {

													p.sendMessage(plugin.lm.getMessage("noTNT"));
													return true;

												} else if (fr == FillResult.NOT_ENOUGH_TNT) {

													p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", plugin.defaultFillRadius, d));
													return true;

												} else if (fr == FillResult.SUCCESS) {

													p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", plugin.defaultFillRadius, d));
													return true;

												} else if (fr == FillResult.SUCCESS_FACTION) {
													
													p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
													return true;
													
												} else {

													p.sendMessage(plugin.lm.getMessage("errorFilling"));
													return true;

												}

											}

										}

									}
								}

							} else {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							}

						} else if (args.length == 2) {

							if (!(p.hasPermission("tntfill.use.customradius"))) {

								p.sendMessage(plugin.lm.getMessage("noPermission"));
								return true;

							}
							
							if (this.isInt(args[0])) {
								
								if (this.isInt(args[1])) {
									
									int radius = Integer.parseInt(args[1]);
									
									if (radius > plugin.maxFillRadius) {
										
										if (!p.hasPermission("tntfill.bypass")) {
											
											p.sendMessage(plugin.lm.getMessage("noPermission"));
											return true;
											
										}
										
									}
									
									int d = plugin.rff.countDispensers(p, radius);
									int per = Integer.parseInt(args[0]);
									
									if (d == 0) {

										p.sendMessage(plugin.lm.getLangMessage("noDispensers", radius, d));
										return true;

									} else {

										if (this.isCreative(p)) {
											
											FillResult fr = plugin.rff.fillDispensersCreative(p, radius, per);

											if (fr == FillResult.SUCCESS) {

												p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", radius, d));
												return true;

											} else if (fr == FillResult.SUCCESS_FACTION) {
												
												p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
												return true;
												
											} else {

												p.sendMessage(plugin.lm.getMessage("errorFilling"));
												return true;

											}

										} else {

											int t = plugin.countTnt(p);

											if (t == 0) {

												p.sendMessage(plugin.lm.getMessage("noTNT"));
												return true;

											} else if (t < d) {

												p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", radius, d));
												return true;

											} else {

												if (per * d > t) {

													p.sendMessage(plugin.lm.getLangMessage("notEnoughTNT", radius, d));
													return true;

												} else {

													FillResult fr = plugin.rff.fillDispensersSurvival(p, radius, per, per * d);

													if (fr == FillResult.NO_TNT) {

														p.sendMessage(plugin.lm.getMessage("noTNT"));
														return true;

													} else if (fr == FillResult.NOT_ENOUGH_TNT) {

														p.sendMessage(plugin.lm.getLangMessage("onePerDispenser", radius, d));
														return true;

													} else if (fr == FillResult.SUCCESS) {

														p.sendMessage(plugin.lm.getLangMessage("dispensersFilled", radius, d));
														return true;

													} else if (fr == FillResult.SUCCESS_FACTION) {
														
														p.sendMessage(plugin.lm.getLangMessage("dispensersFilledFaction", plugin.defaultFillRadius, d));
														return true;
														
													} else {

														p.sendMessage(plugin.lm.getMessage("errorFilling"));
														return true;

													}

												}

											}

										}
									}
									
								} else {

									p.sendMessage(plugin.lm.getMessage("notNumber"));
									return true;

								}
								
							} else {

								p.sendMessage(plugin.lm.getMessage("notNumber"));
								return true;

							}

						} else {

							p.sendMessage(plugin.lm.getMessage("tooManyArguments"));
							return true;

						}

					}
				}
			}

		}
		return true;
	}

	private boolean isCreative(Player p) {

		return p.getGameMode().equals(GameMode.CREATIVE);

	}

	private boolean isInt(String num) {

		try {
			Integer.parseInt(num);
		} catch (NumberFormatException e){
			return false;
		}
		return true;

	}
}
