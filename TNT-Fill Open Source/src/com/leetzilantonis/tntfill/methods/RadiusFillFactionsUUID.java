package com.leetzilantonis.tntfill.methods;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leetzilantonis.tntfill.FillResult;
import com.leetzilantonis.tntfill.Main;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;

public class RadiusFillFactionsUUID {

	Main plugin;

	public RadiusFillFactionsUUID(Main instance) {
		plugin = instance;
	}
	
	@SuppressWarnings({ "deprecation" })
	private void debug(String msg) {
		if (plugin.getCustomConfig().getBoolean("debug")) {
			for (Player online : Bukkit.getOnlinePlayers()) {

				if (online.hasPermission("tntfill.debug")) {

					online.sendMessage("DEBUG: " + msg);

				}

			}
			System.out.println("DEBUG: " + msg);
		}
	}

	public int countDispensers(Player p, int radius) {

		FPlayer mp = FPlayers.getInstance().getByPlayer(p);
		Faction pf = mp.getFaction();
		int count = 0;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
								Faction df = Board.getInstance().getFactionAt(fl);

								if (df.getId().equals(pf.getId())) {

									count += 1;

								} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

									count += 1;

								} else if (df.isNone()) {

									count += 1;

								}

							}
						}
					}
				}
			}
		} else {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
							Faction df = Board.getInstance().getFactionAt(fl);

							if (df.getId().equals(pf.getId())) {

								count += 1;

							} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

								count += 1;

							} else if (df.isNone()) {

								count += 1;

							}

						}
					}
				}
			}
		}
		return count;

	}

	public FillResult fillDispensersSurvival(Player p,  int radius, int tntPer, int tntRemoved) {

		FPlayer mp = FPlayers.getInstance().getByPlayer(p);
		Faction pf = mp.getFaction();
		HashMap<ItemMeta, Integer> pi = new HashMap<ItemMeta, Integer>();
		int total = 0;
		boolean fIsIn = false;

		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.TNT) {

					total += it.getAmount();

					if (pi.containsKey(it.getItemMeta())) {
						this.debug("tnt already in HashMap");
						int amount = pi.get(it.getItemMeta());
						pi.remove(it.getItemMeta());
						pi.put(it.getItemMeta(), amount + it.getAmount());
						this.debug("new amount: " + pi.get(it.getItemMeta()));
					} else {

						this.debug("new TNT type: " + it.getAmount());
						pi.put(it.getItemMeta(), it.getAmount());

					}

				}

			}

		}

		if (total == 0) {

			return FillResult.NO_TNT;

		} else if (total < tntRemoved) {

			return FillResult.NOT_ENOUGH_TNT;

		}

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
								Faction df = Board.getInstance().getFactionAt(fl);

								if (df.getId().equals(pf.getId())) {
									this.debug("is in faction");
									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
									Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
									this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												this.debug("put < per");
												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < put) {
														this.debug("get it < put");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= put");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - put);

														if (pi.get(it) == 0) {
															this.debug("get it = 0");
															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(put);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											} else {

												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < per) {
														this.debug("get it < per");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= per");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - per);

														if (pi.get(it) == 0) {

															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(per);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											}

										}

										if (per == 0) {

											break;

										}

									}

									if (per > 0) {

										if (pi.size() > 0) {

											for (int s = 0; s < slotCount; s++) {

												if (per > 64) {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < 64) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(64);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}

												} else {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < per) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(per);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}



												}

											}
										} else {

											return FillResult.UNKNOWN;

										}
									}
								} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

									if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {
										this.debug("is in ally faction");
										InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
										Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
										this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
										int tntSlotCount = 0;
										HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
										int rawTntCount = 0;
										int slotCount = 0;

										for (ItemStack it : dispenser.getInventory().getContents()) {

											if (it == null || it.getType() == Material.AIR) {

												slotCount += 1;

											} else if (it.getType() == Material.TNT) {

												tntSlotCount += 1;
												rawTntCount += it.getAmount();

												if (tntCount.containsKey(it.getItemMeta())) {

													int amount = tntCount.get(it.getItemMeta());
													tntCount.remove(it.getItemMeta());
													tntCount.put(it.getItemMeta(), amount + it.getAmount());

												} else {

													tntCount.put(it.getItemMeta(), it.getAmount());

												}

											}

										}

										int per = tntPer;

										if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

											per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

										}						

										for (ItemMeta it : tntCount.keySet()) {

											int amount = tntCount.get(it);

											if (amount % 64 > 0) {

												int put = amount % 64;

												if (put < per) {

													this.debug("put < per");
													if (pi.containsKey(it)) {
														this.debug("pi contains");
														ItemStack item = new ItemStack(Material.TNT, 1);

														if (pi.get(it) < put) {
															this.debug("get it < put");
															int a = pi.get(it);
															pi.remove(it);
															item.setItemMeta(it);
															item.setAmount(a);
															this.debug("a: " + a + " it: " + it);
														} else {
															this.debug("get it >= put");
															int a = pi.get(it);
															pi.remove(it);
															pi.put(it, a - put);

															if (pi.get(it) == 0) {
																this.debug("get it = 0");
																pi.remove(it);

															}
															this.debug("a: " + a + " it: " + it);
															item.setItemMeta(it);
															item.setAmount(put);
															this.debug("put: " + put);
														}

														p.getInventory().removeItem(item);
														dispenser.getInventory().addItem(item);
														per -= item.getAmount();
														this.debug("per: " + per + " Amount: " + item.getAmount());

													}

												} else {

													if (pi.containsKey(it)) {
														this.debug("pi contains");
														ItemStack item = new ItemStack(Material.TNT, 1);

														if (pi.get(it) < per) {
															this.debug("get it < per");
															int a = pi.get(it);
															pi.remove(it);
															item.setItemMeta(it);
															item.setAmount(a);
															this.debug("a: " + a + " it: " + it);
														} else {
															this.debug("get it >= per");
															int a = pi.get(it);
															pi.remove(it);
															pi.put(it, a - per);

															if (pi.get(it) == 0) {

																pi.remove(it);

															}
															this.debug("a: " + a + " it: " + it);
															item.setItemMeta(it);
															item.setAmount(per);
															this.debug("put: " + put);
														}

														p.getInventory().removeItem(item);
														dispenser.getInventory().addItem(item);
														per -= item.getAmount();
														this.debug("per: " + per + " Amount: " + item.getAmount());

													}

												}

											}

											if (per == 0) {

												break;

											}

										}

										if (per > 0) {

											if (pi.size() > 0) {

												for (int s = 0; s < slotCount; s++) {

													if (per > 64) {

														for (int f = 0; f < pi.size(); f++) {
															ItemMeta it = null;

															for (ItemMeta its : pi.keySet()){
																it = its;
															}

															ItemStack item = new ItemStack(Material.TNT, 1);

															if (per > 0) {
																if (pi.get(it) < 64) {

																	item.setAmount(pi.get(it));
																	item.setItemMeta(it);
																	p.getInventory().removeItem(item);
																	dispenser.getInventory().addItem(item);
																	pi.remove(it);
																	per -= item.getAmount();

																} else {

																	item.setAmount(64);
																	item.setItemMeta(it);
																	p.getInventory().removeItem(item);
																	dispenser.getInventory().addItem(item);
																	int temp = pi.get(it) - 64;
																	if (temp == 0) {
																		pi.remove(it);
																	} else {
																		pi.put(it, temp);
																	}
																	per -= item.getAmount();

																}
															} else {

																break;

															}
														}

													} else {

														for (int f = 0; f < pi.size(); f++) {
															ItemMeta it = null;

															for (ItemMeta its : pi.keySet()){
																it = its;
															}

															ItemStack item = new ItemStack(Material.TNT, 1);

															if (per > 0) {
																if (pi.get(it) < per) {

																	item.setAmount(pi.get(it));
																	item.setItemMeta(it);
																	p.getInventory().removeItem(item);
																	dispenser.getInventory().addItem(item);
																	pi.remove(it);
																	per -= item.getAmount();

																} else {

																	item.setAmount(per);
																	item.setItemMeta(it);
																	p.getInventory().removeItem(item);
																	dispenser.getInventory().addItem(item);
																	int temp = pi.get(it) - 64;
																	if (temp == 0) {
																		pi.remove(it);
																	} else {
																		pi.put(it, temp);
																	}
																	per -= item.getAmount();

																}
															} else {

																break;

															}
														}



													}

												}
											} else {

												return FillResult.UNKNOWN;

											}
										}

									}

								} else if (df.isNone()) {
									this.debug("is in wilderness");
									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
									Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
									this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												this.debug("put < per");
												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < put) {
														this.debug("get it < put");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= put");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - put);

														if (pi.get(it) == 0) {
															this.debug("get it = 0");
															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(put);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											} else {

												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < per) {
														this.debug("get it < per");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= per");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - per);

														if (pi.get(it) == 0) {

															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(per);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											}

										}

										if (per == 0) {

											break;

										}

									}

									if (per > 0) {

										if (pi.size() > 0) {

											for (int s = 0; s < slotCount; s++) {

												if (per > 64) {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < 64) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(64);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}

												} else {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < per) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(per);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}



												}

											}
										} else {

											return FillResult.UNKNOWN;

										}
									}

								} else {

									fIsIn = true;

								}

							}
						}
					}
				}
			}
		} else {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
							Faction df = Board.getInstance().getFactionAt(fl);

							if (df.getId().equals(pf.getId())) {
								this.debug("is in faction");
								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
								Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
								this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
								int tntSlotCount = 0;
								HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
								int rawTntCount = 0;
								int slotCount = 0;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it == null || it.getType() == Material.AIR) {

										slotCount += 1;

									} else if (it.getType() == Material.TNT) {

										tntSlotCount += 1;
										rawTntCount += it.getAmount();

										if (tntCount.containsKey(it.getItemMeta())) {

											int amount = tntCount.get(it.getItemMeta());
											tntCount.remove(it.getItemMeta());
											tntCount.put(it.getItemMeta(), amount + it.getAmount());

										} else {

											tntCount.put(it.getItemMeta(), it.getAmount());

										}

									}

								}

								int per = tntPer;

								if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

									per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

								}						

								for (ItemMeta it : tntCount.keySet()) {

									int amount = tntCount.get(it);

									if (amount % 64 > 0) {

										int put = amount % 64;

										if (put < per) {

											this.debug("put < per");
											if (pi.containsKey(it)) {
												this.debug("pi contains");
												ItemStack item = new ItemStack(Material.TNT, 1);

												if (pi.get(it) < put) {
													this.debug("get it < put");
													int a = pi.get(it);
													pi.remove(it);
													item.setItemMeta(it);
													item.setAmount(a);
													this.debug("a: " + a + " it: " + it);
												} else {
													this.debug("get it >= put");
													int a = pi.get(it);
													pi.remove(it);
													pi.put(it, a - put);

													if (pi.get(it) == 0) {
														this.debug("get it = 0");
														pi.remove(it);

													}
													this.debug("a: " + a + " it: " + it);
													item.setItemMeta(it);
													item.setAmount(put);
													this.debug("put: " + put);
												}

												p.getInventory().removeItem(item);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();
												this.debug("per: " + per + " Amount: " + item.getAmount());

											}

										} else {

											if (pi.containsKey(it)) {
												this.debug("pi contains");
												ItemStack item = new ItemStack(Material.TNT, 1);

												if (pi.get(it) < per) {
													this.debug("get it < per");
													int a = pi.get(it);
													pi.remove(it);
													item.setItemMeta(it);
													item.setAmount(a);
													this.debug("a: " + a + " it: " + it);
												} else {
													this.debug("get it >= per");
													int a = pi.get(it);
													pi.remove(it);
													pi.put(it, a - per);

													if (pi.get(it) == 0) {

														pi.remove(it);

													}
													this.debug("a: " + a + " it: " + it);
													item.setItemMeta(it);
													item.setAmount(per);
													this.debug("put: " + put);
												}

												p.getInventory().removeItem(item);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();
												this.debug("per: " + per + " Amount: " + item.getAmount());

											}

										}

									}

									if (per == 0) {

										break;

									}

								}

								if (per > 0) {

									if (pi.size() > 0) {

										for (int s = 0; s < slotCount; s++) {

											if (per > 64) {

												for (int f = 0; f < pi.size(); f++) {
													ItemMeta it = null;

													for (ItemMeta its : pi.keySet()){
														it = its;
													}

													ItemStack item = new ItemStack(Material.TNT, 1);

													if (per > 0) {
														if (pi.get(it) < 64) {

															item.setAmount(pi.get(it));
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															pi.remove(it);
															per -= item.getAmount();

														} else {

															item.setAmount(64);
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															int temp = pi.get(it) - 64;
															if (temp == 0) {
																pi.remove(it);
															} else {
																pi.put(it, temp);
															}
															per -= item.getAmount();

														}
													} else {

														break;

													}
												}

											} else {

												for (int f = 0; f < pi.size(); f++) {
													ItemMeta it = null;

													for (ItemMeta its : pi.keySet()){
														it = its;
													}

													ItemStack item = new ItemStack(Material.TNT, 1);

													if (per > 0) {
														if (pi.get(it) < per) {

															item.setAmount(pi.get(it));
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															pi.remove(it);
															per -= item.getAmount();

														} else {

															item.setAmount(per);
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															int temp = pi.get(it) - 64;
															if (temp == 0) {
																pi.remove(it);
															} else {
																pi.put(it, temp);
															}
															per -= item.getAmount();

														}
													} else {

														break;

													}
												}



											}

										}
									} else {

										return FillResult.UNKNOWN;

									}
								}
							} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

								if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {
									this.debug("is in ally faction");
									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
									Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
									this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												this.debug("put < per");
												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < put) {
														this.debug("get it < put");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= put");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - put);

														if (pi.get(it) == 0) {
															this.debug("get it = 0");
															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(put);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											} else {

												if (pi.containsKey(it)) {
													this.debug("pi contains");
													ItemStack item = new ItemStack(Material.TNT, 1);

													if (pi.get(it) < per) {
														this.debug("get it < per");
														int a = pi.get(it);
														pi.remove(it);
														item.setItemMeta(it);
														item.setAmount(a);
														this.debug("a: " + a + " it: " + it);
													} else {
														this.debug("get it >= per");
														int a = pi.get(it);
														pi.remove(it);
														pi.put(it, a - per);

														if (pi.get(it) == 0) {

															pi.remove(it);

														}
														this.debug("a: " + a + " it: " + it);
														item.setItemMeta(it);
														item.setAmount(per);
														this.debug("put: " + put);
													}

													p.getInventory().removeItem(item);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();
													this.debug("per: " + per + " Amount: " + item.getAmount());

												}

											}

										}

										if (per == 0) {

											break;

										}
									}

									if (per > 0) {

										if (pi.size() > 0) {

											for (int s = 0; s < slotCount; s++) {

												if (per > 64) {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < 64) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(64);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}

												} else {

													for (int f = 0; f < pi.size(); f++) {
														ItemMeta it = null;

														for (ItemMeta its : pi.keySet()){
															it = its;
														}

														ItemStack item = new ItemStack(Material.TNT, 1);

														if (per > 0) {
															if (pi.get(it) < per) {

																item.setAmount(pi.get(it));
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																pi.remove(it);
																per -= item.getAmount();

															} else {

																item.setAmount(per);
																item.setItemMeta(it);
																p.getInventory().removeItem(item);
																dispenser.getInventory().addItem(item);
																int temp = pi.get(it) - 64;
																if (temp == 0) {
																	pi.remove(it);
																} else {
																	pi.put(it, temp);
																}
																per -= item.getAmount();

															}
														} else {

															break;

														}
													}



												}

											}
										} else {

											return FillResult.UNKNOWN;

										}
									}

								}

							} else if (df.isNone()) {
								this.debug("is in wilderness");
								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
								Location l = p.getLocation().getBlock().getRelative(i, j, k).getLocation();
								this.debug("valid dispenser at: " + l.getX() + " " + l.getY() + " " + l.getZ());
								int tntSlotCount = 0;
								HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
								int rawTntCount = 0;
								int slotCount = 0;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it == null || it.getType() == Material.AIR) {

										slotCount += 1;

									} else if (it.getType() == Material.TNT) {

										tntSlotCount += 1;
										rawTntCount += it.getAmount();

										if (tntCount.containsKey(it.getItemMeta())) {

											int amount = tntCount.get(it.getItemMeta());
											tntCount.remove(it.getItemMeta());
											tntCount.put(it.getItemMeta(), amount + it.getAmount());

										} else {

											tntCount.put(it.getItemMeta(), it.getAmount());

										}

									}

								}

								int per = tntPer;

								if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

									per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

								}						

								for (ItemMeta it : tntCount.keySet()) {

									int amount = tntCount.get(it);

									if (amount % 64 > 0) {

										int put = amount % 64;

										if (put < per) {

											this.debug("put < per");
											if (pi.containsKey(it)) {
												this.debug("pi contains");
												ItemStack item = new ItemStack(Material.TNT, 1);

												if (pi.get(it) < put) {
													this.debug("get it < put");
													int a = pi.get(it);
													pi.remove(it);
													item.setItemMeta(it);
													item.setAmount(a);
													this.debug("a: " + a + " it: " + it);
												} else {
													this.debug("get it >= put");
													int a = pi.get(it);
													pi.remove(it);
													pi.put(it, a - put);

													if (pi.get(it) == 0) {
														this.debug("get it = 0");
														pi.remove(it);

													}
													this.debug("a: " + a + " it: " + it);
													item.setItemMeta(it);
													item.setAmount(put);
													this.debug("put: " + put);
												}

												p.getInventory().removeItem(item);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();
												this.debug("per: " + per + " Amount: " + item.getAmount());

											}

										} else {

											if (pi.containsKey(it)) {
												this.debug("pi contains");
												ItemStack item = new ItemStack(Material.TNT, 1);

												if (pi.get(it) < per) {
													this.debug("get it < per");
													int a = pi.get(it);
													pi.remove(it);
													item.setItemMeta(it);
													item.setAmount(a);
													this.debug("a: " + a + " it: " + it);
												} else {
													this.debug("get it >= per");
													int a = pi.get(it);
													pi.remove(it);
													pi.put(it, a - per);

													if (pi.get(it) == 0) {

														pi.remove(it);

													}
													this.debug("a: " + a + " it: " + it);
													item.setItemMeta(it);
													item.setAmount(per);
													this.debug("put: " + put);
												}

												p.getInventory().removeItem(item);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();
												this.debug("per: " + per + " Amount: " + item.getAmount());

											}

										}

									}

									if (per == 0) {

										break;

									}

								}

								if (per > 0) {

									if (pi.size() > 0) {

										for (int s = 0; s < slotCount; s++) {

											if (per > 64) {

												for (int f = 0; f < pi.size(); f++) {
													ItemMeta it = null;

													for (ItemMeta its : pi.keySet()){
														it = its;
													}

													ItemStack item = new ItemStack(Material.TNT, 1);

													if (per > 0) {
														if (pi.get(it) < 64) {

															item.setAmount(pi.get(it));
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															pi.remove(it);
															per -= item.getAmount();

														} else {

															item.setAmount(64);
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															int temp = pi.get(it) - 64;
															if (temp == 0) {
																pi.remove(it);
															} else {
																pi.put(it, temp);
															}
															per -= item.getAmount();

														}
													} else {

														break;

													}
												}

											} else {

												for (int f = 0; f < pi.size(); f++) {
													ItemMeta it = null;

													for (ItemMeta its : pi.keySet()){
														it = its;
													}

													ItemStack item = new ItemStack(Material.TNT, 1);

													if (per > 0) {
														if (pi.get(it) < per) {

															item.setAmount(pi.get(it));
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															pi.remove(it);
															per -= item.getAmount();

														} else {

															item.setAmount(per);
															item.setItemMeta(it);
															p.getInventory().removeItem(item);
															dispenser.getInventory().addItem(item);
															int temp = pi.get(it) - 64;
															if (temp == 0) {
																pi.remove(it);
															} else {
																pi.put(it, temp);
															}
															per -= item.getAmount();

														}
													} else {

														break;

													}
												}



											}

										}
									} else {

										return FillResult.UNKNOWN;

									}
								}

							} else {

								fIsIn = true;

							}
						}
					}
				}
			}
		}
		if (fIsIn) {
			return FillResult.SUCCESS_FACTION;
		}
		return FillResult.SUCCESS;
	}

	public FillResult fillDispensersCreative(Player p,  int radius, int tntPer) {

		FPlayer mp = FPlayers.getInstance().getByPlayer(p);
		Faction pf = mp.getFaction();
		boolean fIsIn = false;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
								Faction df = Board.getInstance().getFactionAt(fl);
								if (df.getId().equals(pf.getId())) {

									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(put);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();


											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(per);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

										}

										if (per == 0) {

											break;

										}

									}

									if (per > 0) {

										for (int s = 0; s < slotCount; s++) {

											if (per > 64) {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(64);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(per);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

											if (per == 0) {
												break;
											}

										}
									}

								} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

									if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {

										InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

										int tntSlotCount = 0;
										HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
										int rawTntCount = 0;
										int slotCount = 0;

										for (ItemStack it : dispenser.getInventory().getContents()) {

											if (it == null || it.getType() == Material.AIR) {

												slotCount += 1;

											} else if (it.getType() == Material.TNT) {

												tntSlotCount += 1;
												rawTntCount += it.getAmount();

												if (tntCount.containsKey(it.getItemMeta())) {

													int amount = tntCount.get(it.getItemMeta());
													tntCount.remove(it.getItemMeta());
													tntCount.put(it.getItemMeta(), amount + it.getAmount());

												} else {

													tntCount.put(it.getItemMeta(), it.getAmount());

												}

											}

										}

										int per = tntPer;

										if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

											per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

										}						

										for (ItemMeta it : tntCount.keySet()) {

											int amount = tntCount.get(it);

											if (amount % 64 > 0) {

												int put = amount % 64;

												if (put < per) {

													ItemStack item = new ItemStack(Material.TNT, 1);
													item.setItemMeta(it);
													item.setAmount(put);

													dispenser.getInventory().addItem(item);
													per -= item.getAmount();


												} else {

													ItemStack item = new ItemStack(Material.TNT, 1);
													item.setItemMeta(it);
													item.setAmount(per);

													dispenser.getInventory().addItem(item);
													per -= item.getAmount();

												}

											}

											if (per == 0) {

												break;

											}

										}

										if (per > 0) {

											for (int s = 0; s < slotCount; s++) {

												if (per > 64) {

													ItemStack item = new ItemStack(Material.TNT, 1);

													item.setAmount(64);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();

												} else {

													ItemStack item = new ItemStack(Material.TNT, 1);

													item.setAmount(per);
													dispenser.getInventory().addItem(item);
													per -= item.getAmount();

												}

												if (per == 0) {
													break;
												}

											}
										}

									}
								} else if (df.isNone()) {

									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(put);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();


											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(per);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

										}

										if (per == 0) {

											break;

										}

									}

									if (per > 0) {

										for (int s = 0; s < slotCount; s++) {

											if (per > 64) {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(64);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(per);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

											if (per == 0) {
												break;
											}

										}
									}

								} else {

									fIsIn = true;

								}

							}
						}
					}
				}
			}
		} else {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							FLocation fl = new FLocation(p.getLocation().getBlock().getRelative(i, j, k).getLocation());
							Faction df = Board.getInstance().getFactionAt(fl);
							if (df.getId().equals(pf.getId())) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

								int tntSlotCount = 0;
								HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
								int rawTntCount = 0;
								int slotCount = 0;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it == null || it.getType() == Material.AIR) {

										slotCount += 1;

									} else if (it.getType() == Material.TNT) {

										tntSlotCount += 1;
										rawTntCount += it.getAmount();

										if (tntCount.containsKey(it.getItemMeta())) {

											int amount = tntCount.get(it.getItemMeta());
											tntCount.remove(it.getItemMeta());
											tntCount.put(it.getItemMeta(), amount + it.getAmount());

										} else {

											tntCount.put(it.getItemMeta(), it.getAmount());

										}

									}

								}

								int per = tntPer;

								if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

									per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

								}						

								for (ItemMeta it : tntCount.keySet()) {

									int amount = tntCount.get(it);

									if (amount % 64 > 0) {

										int put = amount % 64;

										if (put < per) {

											ItemStack item = new ItemStack(Material.TNT, 1);
											item.setItemMeta(it);
											item.setAmount(put);

											dispenser.getInventory().addItem(item);
											per -= item.getAmount();


										} else {

											ItemStack item = new ItemStack(Material.TNT, 1);
											item.setItemMeta(it);
											item.setAmount(per);

											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										}

									}

									if (per == 0) {

										break;

									}

								}

								if (per > 0) {

									for (int s = 0; s < slotCount; s++) {

										if (per > 64) {

											ItemStack item = new ItemStack(Material.TNT, 1);

											item.setAmount(64);
											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										} else {

											ItemStack item = new ItemStack(Material.TNT, 1);

											item.setAmount(per);
											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										}

										if (per == 0) {
											break;
										}

									}
								}

							} else if (pf.getRelationTo(df).equals(Relation.ALLY)) {

								if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {

									InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

									int tntSlotCount = 0;
									HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
									int rawTntCount = 0;
									int slotCount = 0;

									for (ItemStack it : dispenser.getInventory().getContents()) {

										if (it == null || it.getType() == Material.AIR) {

											slotCount += 1;

										} else if (it.getType() == Material.TNT) {

											tntSlotCount += 1;
											rawTntCount += it.getAmount();

											if (tntCount.containsKey(it.getItemMeta())) {

												int amount = tntCount.get(it.getItemMeta());
												tntCount.remove(it.getItemMeta());
												tntCount.put(it.getItemMeta(), amount + it.getAmount());

											} else {

												tntCount.put(it.getItemMeta(), it.getAmount());

											}

										}

									}

									int per = tntPer;

									if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

										per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

									}						

									for (ItemMeta it : tntCount.keySet()) {

										int amount = tntCount.get(it);

										if (amount % 64 > 0) {

											int put = amount % 64;

											if (put < per) {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(put);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();


											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);
												item.setItemMeta(it);
												item.setAmount(per);

												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

										}

										if (per == 0) {

											break;

										}

									}

									if (per > 0) {

										for (int s = 0; s < slotCount; s++) {

											if (per > 64) {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(64);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											} else {

												ItemStack item = new ItemStack(Material.TNT, 1);

												item.setAmount(per);
												dispenser.getInventory().addItem(item);
												per -= item.getAmount();

											}

											if (per == 0) {
												break;
											}

										}
									}

								}
							} else if (df.isNone()) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

								int tntSlotCount = 0;
								HashMap<ItemMeta, Integer> tntCount = new HashMap<ItemMeta, Integer>();
								int rawTntCount = 0;
								int slotCount = 0;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it == null || it.getType() == Material.AIR) {

										slotCount += 1;

									} else if (it.getType() == Material.TNT) {

										tntSlotCount += 1;
										rawTntCount += it.getAmount();

										if (tntCount.containsKey(it.getItemMeta())) {

											int amount = tntCount.get(it.getItemMeta());
											tntCount.remove(it.getItemMeta());
											tntCount.put(it.getItemMeta(), amount + it.getAmount());

										} else {

											tntCount.put(it.getItemMeta(), it.getAmount());

										}

									}

								}

								int per = tntPer;

								if ((slotCount *64) + ((tntSlotCount * 64) - rawTntCount) < tntPer) {

									per = (slotCount *64) + ((tntSlotCount * 64) - rawTntCount);

								}						

								for (ItemMeta it : tntCount.keySet()) {

									int amount = tntCount.get(it);

									if (amount % 64 > 0) {

										int put = amount % 64;

										if (put < per) {

											ItemStack item = new ItemStack(Material.TNT, 1);
											item.setItemMeta(it);
											item.setAmount(put);

											dispenser.getInventory().addItem(item);
											per -= item.getAmount();


										} else {

											ItemStack item = new ItemStack(Material.TNT, 1);
											item.setItemMeta(it);
											item.setAmount(per);

											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										}

									}

									if (per == 0) {

										break;

									}

								}

								if (per > 0) {

									for (int s = 0; s < slotCount; s++) {

										if (per > 64) {

											ItemStack item = new ItemStack(Material.TNT, 1);

											item.setAmount(64);
											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										} else {

											ItemStack item = new ItemStack(Material.TNT, 1);

											item.setAmount(per);
											dispenser.getInventory().addItem(item);
											per -= item.getAmount();

										}

										if (per == 0) {
											break;
										}

									}
								}

							} else {

								fIsIn = true;

							}
						}
					}
				}
			}
		}
		if (fIsIn) {
			return FillResult.SUCCESS_FACTION;
		}
		return FillResult.SUCCESS;
	}

}
