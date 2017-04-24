package com.leetzilantonis.tntfill.methods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leetzilantonis.tntfill.ClearResult;
import com.leetzilantonis.tntfill.Main;

public class RadiusClear {

	Main plugin;

	public RadiusClear(Main instance) {
		plugin = instance;
	}

	public int countTNT(Player p, int radius) {

		int count = 0;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it != null) {

										if (it.getType() == Material.TNT) {

											count += it.getAmount();

										}
									}
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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (it != null) {

									if (it.getType() == Material.TNT) {

										count += it.getAmount();

									}
								}
							}
						}
					}
				}
			}

		}
		return count;
	}

	public HashMap<ItemMeta, Integer> countListTNT(Player p, int radius) {

		HashMap<ItemMeta, Integer> count = new HashMap<ItemMeta, Integer>();

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it != null) {

										if (it.getType() == Material.TNT) {

											if (count.containsKey(it.getItemMeta())) {

												int temp = count.get(it.getItemMeta()) + it.getAmount();
												count.put(it.getItemMeta(), temp);

											} else {

												count.put(it.getItemMeta(), it.getAmount());

											}

										}
									}
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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (it != null) {

									if (it.getType() == Material.TNT) {

										if (count.containsKey(it.getItemMeta())) {

											int temp = count.get(it.getItemMeta()) + it.getAmount();
											count.put(it.getItemMeta(), temp);

										} else {

											count.put(it.getItemMeta(), it.getAmount());

										}

									}
								}
							}
						}
					}
				}
			}

		}
		return count;
	}

	public int quickCount(Player p) {

		int count = 0;
		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.AIR) {

					count += 64;

				} else if (it.getType() == Material.TNT) {

					int left = 64 - it.getAmount();
					if (left > 0) {
						count += left;
					}

				}

			} else {

				count += 64;

			}

		}
		return count;
		
	}

	public ClearResult clearDispensersSurvival(Player p, int radius) {

		int count = this.countTNT(p, radius);

		if (count == 0) {

			return ClearResult.NO_TNT;

		}

		int quickCount = this.quickCount(p);
		if (quickCount == 0) {
			
			return ClearResult.NO_SPACE;
			
		}
		
		ItemMeta im = new ItemStack(Material.TNT, 1).getItemMeta();
		HashMap<ItemMeta, Integer> c = new HashMap<ItemMeta, Integer>();
		
		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.AIR) {

					if (c.containsKey(im)) {

						int temp = c.get(im) + 64;
						c.put(im, temp);

					} else {

						c.put(im, 64);

					}

				} else if (it.getType() == Material.TNT) {

					int left = 64 - it.getAmount();

					if (left > 0) {

						if (c.containsKey(it.getItemMeta())) {

							int temp = c.get(it.getItemMeta()) + left;
							c.put(it.getItemMeta(), temp);

						} else {

							c.put(it.getItemMeta(), left);

						}

					}

				}

			} else {

				if (c.containsKey(im)) {

					int temp = c.get(im) + 64;
					c.put(im, temp);

				} else {

					c.put(im, 64);

				}

			}

		}

		ClearResult r = ClearResult.CLEARED;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
								List<ItemStack> remove = new ArrayList<ItemStack>();

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it != null) {

										if (it.getType() == Material.TNT) {

											if (c.isEmpty()) {

												r = ClearResult.PART_CLEARED;
												break;

											} else {

												if (c.containsKey(it.getItemMeta())) {

													int left = c.get(it.getItemMeta());

													if (left > it.getAmount()) {

														left -= it.getAmount();
														c.put(it.getItemMeta(), left);
														remove.add(it);

													} else {

														int more = it.getAmount() - left;
														c.remove(it.getItemMeta());

														if (c.isEmpty()) {

															it.setAmount(more);

														} else {

															if (c.containsKey(im)) {

																int plainLeft = c.get(im);

																if (more <= plainLeft) {

																	plainLeft -= more;
																	if (plainLeft == 0) {

																		c.remove(im);

																	} else {

																		c.put(im, plainLeft);

																	}
																	remove.add(it);

																} else {

																	more -= plainLeft;
																	c.remove(im);
																	it.setAmount(more);

																}

															} else {

																it.setAmount(more);

															}

														}

													}

												} else {

													if (c.containsKey(im)) {

														int plainLeft = c.get(im);

														if (it.getAmount() <= plainLeft) {

															plainLeft -= it.getAmount();
															if (plainLeft == 0) {

																c.remove(im);

															} else {

																c.put(im, plainLeft);

															}
															remove.add(it);

														} else {

															int more = it.getAmount();

															more -= plainLeft;
															c.remove(im);
															it.setAmount(more);

														}

													} else {

														r = ClearResult.PART_CLEARED;
														break;

													}
												}
											}
										}
									}
								}

								for (ItemStack it : remove) {

									dispenser.getInventory().remove(it);
									p.getInventory().addItem(it);

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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
							List<ItemStack> remove = new ArrayList<ItemStack>();

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (it != null) {

									if (it.getType() == Material.TNT) {

										if (c.isEmpty()) {

											r = ClearResult.PART_CLEARED;
											break;

										} else {

											if (c.containsKey(it.getItemMeta())) {

												int left = c.get(it.getItemMeta());

												if (left > it.getAmount()) {

													left -= it.getAmount();
													c.put(it.getItemMeta(), left);
													remove.add(it);

												} else {

													int more = it.getAmount() - left;
													c.remove(it.getItemMeta());

													if (c.isEmpty()) {

														it.setAmount(more);

													} else {

														if (c.containsKey(im)) {

															int plainLeft = c.get(im);

															if (more <= plainLeft) {

																plainLeft -= more;
																if (plainLeft == 0) {

																	c.remove(im);

																} else {

																	c.put(im, plainLeft);

																}
																remove.add(it);

															} else {

																more -= plainLeft;
																c.remove(im);
																it.setAmount(more);

															}

														} else {

															it.setAmount(more);

														}

													}

												}

											} else {

												if (c.containsKey(im)) {

													int plainLeft = c.get(im);

													if (it.getAmount() <= plainLeft) {

														plainLeft -= it.getAmount();
														if (plainLeft == 0) {

															c.remove(im);

														} else {

															c.put(im, plainLeft);

														}
														remove.add(it);

													} else {

														int more = it.getAmount();

														more -= plainLeft;
														c.remove(im);
														it.setAmount(more);

													}

												} else {

													r = ClearResult.PART_CLEARED;
													break;

												}
											}
										}
									}
								}
							}

							for (ItemStack it : remove) {

								dispenser.getInventory().remove(it);
								p.getInventory().addItem(it);

							}

						}
					}
				}
			}

		}

		return r;

	}

	public ClearResult clearDispensersSurvival(Player p, int radius, int per) {

		int count = this.countTNT(p, radius);

		if (count == 0) {

			return ClearResult.NO_TNT;

		}

		ItemMeta im = new ItemStack(Material.TNT, 1).getItemMeta();
		HashMap<ItemMeta, Integer> c = new HashMap<ItemMeta, Integer>();

		for (ItemStack it : p.getInventory().getContents()) {

			if (it != null) {

				if (it.getType() == Material.AIR) {

					if (c.containsKey(im)) {

						int temp = c.get(im) + 64;
						c.put(im, temp);

					} else {

						c.put(im, 64);

					}

				} else if (it.getType() == Material.TNT) {

					int left = 64 - it.getAmount();

					if (c.containsKey(it.getItemMeta())) {

						int temp = c.get(it.getItemMeta()) + left;
						c.put(it.getItemMeta(), temp);

					} else {

						c.put(it.getItemMeta(), left);

					}

				}

			} else {

				if (c.containsKey(im)) {

					int temp = c.get(im) + 64;
					c.put(im, temp);

				} else {

					c.put(im, 64);

				}

			}

		}

		ClearResult r = ClearResult.CLEARED;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
								List<ItemStack> remove = new ArrayList<ItemStack>();

								int newPer = per;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (newPer <= 0)
										break;

									if (it != null) {

										if (it.getType() == Material.TNT) {

											if (c.isEmpty()) {

												r = ClearResult.PART_CLEARED;
												break;

											} else {

												if (c.containsKey(it.getItemMeta())) {

													int left = c.get(it.getItemMeta());

													if (left > it.getAmount()) {

														if (newPer >= it.getAmount()) {

															left -= it.getAmount();
															c.put(it.getItemMeta(), left);
															newPer -= it.getAmount();
															remove.add(it);

														} else {

															left -= newPer;
															c.put(it.getItemMeta(), left);
															int temp = it.getAmount() - newPer;
															newPer = 0;
															it.setAmount(temp);
															break;

														}

													} else {

														int more = it.getAmount() - left;
														c.remove(it.getItemMeta());

														if (c.isEmpty()) {

															it.setAmount(more);

														} else {

															if (c.containsKey(im)) {

																int plainLeft = c.get(im);

																if (more <= plainLeft) {

																	if (newPer >= more) {

																		plainLeft -= more;
																		if (plainLeft == 0) {

																			c.remove(im);

																		} else {

																			c.put(im, plainLeft);

																		}
																		remove.add(it);

																	} else {

																		plainLeft -= newPer;
																		if (plainLeft == 0) {
																			c.remove(im);
																		} else {
																			c.put(im, plainLeft);
																		}
																		int temp = it.getAmount() - newPer;
																		newPer = 0;
																		it.setAmount(temp);
																		break;

																	}

																} else {

																	if (newPer >= plainLeft) {

																		more -= plainLeft;
																		c.remove(im);
																		newPer -= plainLeft;
																		it.setAmount(more);

																	} else {

																		more -= newPer;
																		plainLeft -= newPer;
																		if (plainLeft == 0) {
																			c.remove(im);
																		} else {
																			c.put(im, plainLeft);
																		}
																		it.setAmount(more);
																		newPer = 0;
																		break;

																	}
																}

															} else {

																it.setAmount(more);

															}

														}

													}

												} else {

													if (c.containsKey(im)) {

														int plainLeft = c.get(im);

														if (it.getAmount() <= plainLeft) {

															if (newPer >= it.getAmount()) {

																plainLeft -= it.getAmount();
																if (plainLeft == 0) {

																	c.remove(im);

																} else {

																	c.put(im, plainLeft);

																}
																remove.add(it);

															} else {

																plainLeft -= newPer;
																if (plainLeft == 0) {
																	c.remove(im);
																} else {
																	c.put(im, plainLeft);
																}
																int temp = it.getAmount() - newPer;
																newPer = 0;
																it.setAmount(temp);
																break;

															}

														} else {

															int more = it.getAmount();

															if (newPer >= plainLeft) {

																more -= plainLeft;
																c.remove(im);
																newPer -= plainLeft;
																it.setAmount(more);

															} else {

																more -= newPer;
																plainLeft -= newPer;
																if (plainLeft == 0) {
																	c.remove(im);
																} else {
																	c.put(im, plainLeft);
																}
																it.setAmount(more);
																newPer = 0;
																break;

															}
														}

													} else {

														r = ClearResult.PART_CLEARED;
														break;

													}
												}
											}
										}
									}
								}

								for (ItemStack it : remove) {

									dispenser.getInventory().remove(it);
									p.getInventory().addItem(it);

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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
							List<ItemStack> remove = new ArrayList<ItemStack>();

							int newPer = per;

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (newPer <= 0)
									break;

								if (it != null) {

									if (it.getType() == Material.TNT) {

										if (c.isEmpty()) {

											r = ClearResult.PART_CLEARED;
											break;

										} else {

											if (c.containsKey(it.getItemMeta())) {

												int left = c.get(it.getItemMeta());

												if (left > it.getAmount()) {

													if (newPer >= it.getAmount()) {

														left -= it.getAmount();
														c.put(it.getItemMeta(), left);
														newPer -= it.getAmount();
														remove.add(it);

													} else {

														left -= newPer;
														c.put(it.getItemMeta(), left);
														int temp = it.getAmount() - newPer;
														newPer = 0;
														it.setAmount(temp);
														break;

													}

												} else {

													int more = it.getAmount() - left;
													c.remove(it.getItemMeta());

													if (c.isEmpty()) {

														it.setAmount(more);

													} else {

														if (c.containsKey(im)) {

															int plainLeft = c.get(im);

															if (more <= plainLeft) {

																if (newPer >= more) {

																	plainLeft -= more;
																	if (plainLeft == 0) {

																		c.remove(im);

																	} else {

																		c.put(im, plainLeft);

																	}
																	remove.add(it);

																} else {

																	plainLeft -= newPer;
																	if (plainLeft == 0) {
																		c.remove(im);
																	} else {
																		c.put(im, plainLeft);
																	}
																	int temp = it.getAmount() - newPer;
																	newPer = 0;
																	it.setAmount(temp);
																	break;

																}

															} else {

																if (newPer >= plainLeft) {

																	more -= plainLeft;
																	c.remove(im);
																	newPer -= plainLeft;
																	it.setAmount(more);

																} else {

																	more -= newPer;
																	plainLeft -= newPer;
																	if (plainLeft == 0) {
																		c.remove(im);
																	} else {
																		c.put(im, plainLeft);
																	}
																	it.setAmount(more);
																	newPer = 0;
																	break;

																}
															}

														} else {

															it.setAmount(more);

														}

													}

												}

											} else {

												if (c.containsKey(im)) {

													int plainLeft = c.get(im);

													if (it.getAmount() <= plainLeft) {

														if (newPer >= it.getAmount()) {

															plainLeft -= it.getAmount();
															if (plainLeft == 0) {

																c.remove(im);

															} else {

																c.put(im, plainLeft);

															}
															remove.add(it);

														} else {

															plainLeft -= newPer;
															if (plainLeft == 0) {
																c.remove(im);
															} else {
																c.put(im, plainLeft);
															}
															int temp = it.getAmount() - newPer;
															newPer = 0;
															it.setAmount(temp);
															break;

														}

													} else {

														int more = it.getAmount();

														if (newPer >= plainLeft) {

															more -= plainLeft;
															c.remove(im);
															newPer -= plainLeft;
															it.setAmount(more);

														} else {

															more -= newPer;
															plainLeft -= newPer;
															if (plainLeft == 0) {
																c.remove(im);
															} else {
																c.put(im, plainLeft);
															}
															it.setAmount(more);
															newPer = 0;
															break;

														}
													}

												} else {

													r = ClearResult.PART_CLEARED;
													break;

												}
											}
										}
									}
								}
							}

							for (ItemStack it : remove) {

								dispenser.getInventory().remove(it);
								p.getInventory().addItem(it);

							}

						}
					}
				}
			}

		}

		return r;

	}

	public ClearResult clearDispensersCreative(Player p, int radius) {

		int count = this.countTNT(p, radius);

		if (count == 0) {

			return ClearResult.NO_TNT;

		}

		ClearResult r = ClearResult.CLEARED;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it != null) {

										if (it.getType() == Material.TNT) {

											it.setType(Material.AIR);
											
										}
									}
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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (it != null) {

									if (it.getType() == Material.TNT) {

										it.setType(Material.AIR);
										
									}
								}
							}
						}
					}
				}
			}

		}

		return r;

	}

	public ClearResult clearDispensersCreative(Player p, int radius, int per) {

		int count = this.countTNT(p, radius);

		if (count == 0) {

			return ClearResult.NO_TNT;

		}

		ClearResult r = ClearResult.CLEARED;

		if (plugin.natural) {

			for(int i = -radius; i <= radius; i++) {
				for(int j = -radius; j <= radius; j++) {
					for(int k = -radius; k <= radius; k++) {
						if(p.getLocation().getBlock().getRelative(i, j, k).getType() == Material.DISPENSER) {

							if ((i * i) + (j * j) + (k * k) <= radius * radius) {

								InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
								int pe = per;

								for (ItemStack it : dispenser.getInventory().getContents()) {

									if (it != null) {

										if (it.getType() == Material.TNT) {

											int amount = it.getAmount();
											
											if (amount > pe) {
												
												it.setAmount(amount - pe);
												break;
												
											} else {
												
												it.setType(Material.AIR);
												
											}
											
										}
									}
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

							InventoryHolder dispenser = (InventoryHolder) p.getLocation().getBlock().getRelative(i, j, k).getState();
							int pe = per;

							for (ItemStack it : dispenser.getInventory().getContents()) {

								if (it != null) {

									if (it.getType() == Material.TNT) {

										int amount = it.getAmount();
										
										if (amount > pe) {
											
											it.setAmount(amount - pe);
											break;
											
										} else {
											
											it.setType(Material.AIR);
											
										}
										
									}
								}
							}
						}
					}
				}
			}

		}

		return r;

	}
	
}
