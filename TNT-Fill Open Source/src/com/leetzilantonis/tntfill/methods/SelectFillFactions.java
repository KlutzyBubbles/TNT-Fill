package com.leetzilantonis.tntfill.methods;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leetzilantonis.tntfill.FillResult;
import com.leetzilantonis.tntfill.Main;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class SelectFillFactions {

	Main plugin;

	public SelectFillFactions(Main instance) {

		plugin = instance;

	}

	public FillResult fillSelectedDispensersSurvival(Player p,  LinkedList<Location> locations , int tntPer, int tntRemoved) {

		MPlayer mp = MPlayer.get(p);
		Faction pf = mp.getFaction();
		HashMap<ItemMeta, Integer> pi = new HashMap<ItemMeta, Integer>();
		int total = 0;
		boolean fIsIn = false;

		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.TNT) {

					total += it.getAmount();

					if (pi.containsKey(it.getItemMeta())) {

						int amount = pi.get(it.getItemMeta());
						pi.remove(it.getItemMeta());
						pi.put(it.getItemMeta(), amount + it.getAmount());

					} else {

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

		for (Location loc : locations) {

			if (loc.getBlock().getType() == Material.DISPENSER) {

				Faction df = BoardColl.get().getFactionAt(PS.valueOf(loc));
				if (df.getId().equals(pf.getId())) {

					InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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

								if (pi.containsKey(it)) {

									ItemStack item = new ItemStack(Material.TNT, 1);

									if (pi.get(it) < put) {

										int a = pi.get(it);
										pi.remove(it);
										item.setItemMeta(it);
										item.setAmount(a);

									} else {

										int a = pi.get(it);
										pi.remove(it);
										pi.put(it, a - put);

										if (pi.get(it) == 0) {

											pi.remove(it);

										}

										item.setItemMeta(it);
										item.setAmount(put);

									}

									p.getInventory().removeItem(item);
									dispenser.getInventory().addItem(item);
									per -= item.getAmount();

								}

							} else {

								if (pi.containsKey(it)) {

									ItemStack item = new ItemStack(Material.TNT, 1);

									if (pi.get(it) < per) {

										int a = pi.get(it);
										pi.remove(it);
										item.setItemMeta(it);
										item.setAmount(a);

									} else {

										int a = pi.get(it);
										pi.remove(it);
										pi.put(it, a - per);

										if (pi.get(it) == 0) {

											pi.remove(it);

										}

										item.setItemMeta(it);
										item.setAmount(per);

									}

									p.getInventory().removeItem(item);
									dispenser.getInventory().addItem(item);
									per -= item.getAmount();

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
				} else if (pf.getRelationTo(df).equals(Rel.ALLY)) {

					if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {

						InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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

									if (pi.containsKey(it)) {

										ItemStack item = new ItemStack(Material.TNT, 1);

										if (pi.get(it) < put) {

											int a = pi.get(it);
											pi.remove(it);
											item.setItemMeta(it);
											item.setAmount(a);

										} else {

											int a = pi.get(it);
											pi.remove(it);
											pi.put(it, a - put);

											if (pi.get(it) == 0) {

												pi.remove(it);

											}

											item.setItemMeta(it);
											item.setAmount(put);

										}

										p.getInventory().removeItem(item);
										dispenser.getInventory().addItem(item);
										per -= item.getAmount();

									}

								} else {

									if (pi.containsKey(it)) {

										ItemStack item = new ItemStack(Material.TNT, 1);

										if (pi.get(it) < per) {

											int a = pi.get(it);
											pi.remove(it);
											item.setItemMeta(it);
											item.setAmount(a);

										} else {

											int a = pi.get(it);
											pi.remove(it);
											pi.put(it, a - per);

											if (pi.get(it) == 0) {

												pi.remove(it);

											}

											item.setItemMeta(it);
											item.setAmount(per);

										}

										p.getInventory().removeItem(item);
										dispenser.getInventory().addItem(item);
										per -= item.getAmount();

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
					
					InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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

								if (pi.containsKey(it)) {

									ItemStack item = new ItemStack(Material.TNT, 1);

									if (pi.get(it) < put) {

										int a = pi.get(it);
										pi.remove(it);
										item.setItemMeta(it);
										item.setAmount(a);

									} else {

										int a = pi.get(it);
										pi.remove(it);
										pi.put(it, a - put);

										if (pi.get(it) == 0) {

											pi.remove(it);

										}

										item.setItemMeta(it);
										item.setAmount(put);

									}

									p.getInventory().removeItem(item);
									dispenser.getInventory().addItem(item);
									per -= item.getAmount();

								}

							} else {

								if (pi.containsKey(it)) {

									ItemStack item = new ItemStack(Material.TNT, 1);

									if (pi.get(it) < per) {

										int a = pi.get(it);
										pi.remove(it);
										item.setItemMeta(it);
										item.setAmount(a);

									} else {

										int a = pi.get(it);
										pi.remove(it);
										pi.put(it, a - per);

										if (pi.get(it) == 0) {

											pi.remove(it);

										}

										item.setItemMeta(it);
										item.setAmount(per);

									}

									p.getInventory().removeItem(item);
									dispenser.getInventory().addItem(item);
									per -= item.getAmount();

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
		if (fIsIn) {
			return FillResult.SUCCESS_FACTION;
		}
		return FillResult.SUCCESS;
	}

	public FillResult fillSelectedDispensersCreative(Player p,  LinkedList<Location> locations, int tntPer) {

		MPlayer mp = MPlayer.get(p);
		Faction pf = mp.getFaction();
		boolean fIsIn = false;

		for (Location loc : locations) {

			if (loc.getBlock().getType() == Material.DISPENSER) {

				Faction df = BoardColl.get().getFactionAt(PS.valueOf(loc));
				if (df.getId().equals(pf.getId())) {

					InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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

				} else if (pf.getRelationTo(df).equals(Rel.ALLY)) {

					if (plugin.getCustomConfig().getBoolean("allowAllyFill")) {

						InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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
					
					InventoryHolder dispenser = (InventoryHolder) loc.getBlock().getState();

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
		if (fIsIn) {
			return FillResult.SUCCESS_FACTION;
		}
		return FillResult.SUCCESS;
	}

}
