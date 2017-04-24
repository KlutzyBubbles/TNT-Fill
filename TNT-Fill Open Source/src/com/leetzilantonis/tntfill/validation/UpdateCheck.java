package com.leetzilantonis.tntfill.validation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.leetzilantonis.tntfill.Main;

public class UpdateCheck {

	Main plugin;

	private String currentVersion;
	private String readurl = "https://raw.githubusercontent.com/LeePMC/TNT-Fill/master/UpdateChecker.txt";

	public UpdateCheck(Main instance, String version) {
		plugin = instance;
		currentVersion = version;
	}

	public void startUpdateCheck() {
		if (plugin.getCustomConfig().getBoolean("updateCheck")) {

			String[] version = currentVersion.split("\\.");
			String major = version[1];
			String minor = version[2];
			Logger log = plugin.getLogger();
			try {
				log.info("Checking for a new version of TNT-Fill...");
				URL url = new URL(readurl);
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String str;
				while ((str = br.readLine()) != null) {
					String line = str;
					String ver = line.substring(0, 5);
					String[] v = ver.split("\\.");
					int ma = Integer.parseInt(v[1]);
					int mi = Integer.parseInt(v[2]);
					if (Integer.parseInt(major) < ma) { // Way behind in updates

						String msg = plugin.lm.getUntranslatedMessage("pluginNeedsUpdates");
						msg = msg.replace("{currentVersion}", currentVersion);
						msg = msg.replace("{newVersion}", ver);
						sendUpdateMsg(msg);

					} else if (Integer.parseInt(minor) < mi) { // Not too bad, but still not up to date

						if (Integer.parseInt(major) > ma) { // Making sure its not a new major

							String msg = plugin.lm.getUntranslatedMessage("pluginNeedsUpdates");
							msg = msg.replace("{currentVersion}", currentVersion);
							msg = msg.replace("{newVersion}", ver);
							sendUpdateMsg(msg);

						} else { // assuming up to date

							String msg = plugin.lm.getUntranslatedMessage("pluginIsUpdated");
							msg = msg.replace("{currentVersion}", currentVersion);
							sendUpdateMsg(msg);

						}

					} else { // Assuming up to date

						String msg = plugin.lm.getUntranslatedMessage("pluginIsUpdated");
						msg = msg.replace("{currentVersion}", currentVersion);
						sendUpdateMsg(msg);

					}
				}
				br.close();
			} catch (IOException e) {
				log.warning("TNT-Fill's Auto update checker seems to have a broken link, please post in the discussion about this issue!");;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void sendUpdateMsg(String msg) {
		if (plugin.getCustomConfig().getBoolean("sendUpdates")) {
			for (Player online : plugin.getServer().getOnlinePlayers()) {
				if (online.hasPermission("tntfill.sendupdate") || online.isOp()) {
					online.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
				}
			}
		}
		plugin.getLogger().warning(ChatColor.translateAlternateColorCodes('&', msg));
	}
}