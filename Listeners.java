package net.masa3mc.altcheck;

import static org.bukkit.ChatColor.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class Listeners implements Listener {

	private final Util u = new Util();
	private final AltCheck ins = AltCheck.instance;

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		YamlConfiguration yml = u.getDataYml();
		String[] ip1 = event.getAddress().toString().split("/");
		String ip = ip1[1].replace('.', '_');
		String data = "" + p.getUniqueId();
		Configuration conf = ins.getConfig();
		if (conf.getBoolean("CountryFilter")) {
			String country = "";
			HashMap<String, String> cache = AltCheck.CountryCache;
			if (cache.size() > 30) {
				cache.clear();
			}
			if (cache.containsKey(p.getName())) {
				country = cache.get(p.getName());
			} else {
				country = u.getCountry(ip1[1]).country_name;
				cache.put(p.getName(), country);
			}
			if (!conf.getStringList("WhitelistedCountry").contains(country)) {
				p.kickPlayer(conf.getString("CountryKickMessage"));
				event.setKickMessage(translateAlternateColorCodes('&', conf.getString("CountryKickMessage")));
				event.setResult(Result.KICK_OTHER);
				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players.hasPermission("AltCheck.admin")) {
						players.sendMessage(AltCheck.ALTCHECK_PREFIX
								+ translateAlternateColorCodes('&',
										"&7" + p.getName() + "(" + ip1[1]
												+ ") was kicked by AltCheck-CountryFilter."));
					}
				}
			}
		}
		if (!yml.getStringList(ip).contains(data)) {
			List<String> arraylist = yml.getStringList(ip);
			arraylist.add(data);
			yml.set(ip, arraylist);
			try {
				yml.save(u.getDataFile());
			} catch (IOException e) {
				ins.getLogger().warning("IOException: Couldn't write to DataFile.");
			}
		}
		if (!conf.getStringList("IgnoreUUID").contains("" + p.getUniqueId())) {
			if (yml.getStringList(ip).size() > conf.getInt("maxAlt")) {
				p.kickPlayer(conf.getString("kickMessage"));
				event.setKickMessage(translateAlternateColorCodes('&', conf.getString("kickMessage")));
				event.setResult(Result.KICK_OTHER);
				for (Player players : Bukkit.getOnlinePlayers()) {
					if (players.hasPermission("AltCheck.admin")) {
						players.sendMessage(AltCheck.ALTCHECK_PREFIX
								+ translateAlternateColorCodes('&',
										"&7" + p.getName() + "(" + ip1[1] + ") was kicked by AltCheck- Alt."));
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Thread t = new Thread() {
			public void run() {
				final YamlConfiguration yml = u.getDataYml();
				final String ip_ = u.getPlayerIP(p);
				final String ip = u.getPlayerIP(p).replace('.', '_');
				if (ip_.equals("127.0.0.1") || ip_.startsWith("192.168.") || ip_.startsWith("10.")
						|| ip_.startsWith("172.31.")) {
					for (Player players : Bukkit.getOnlinePlayers()) {
						if (players.hasPermission("AltCheck.admin")) {
							players.sendMessage(AltCheck.ALTCHECK_PREFIX
									+ translateAlternateColorCodes('&', "&7" + p.getName() + " (LocalNetwork) has "
											+ yml.getStringList(ip).size() + " accounts."));
						}
					}
				} else {
					String country = AltCheck.CountryCache.get(p.getName());
					if (country == null) {
						CountryJson json = u.getCountry(p);
						if (json == null) {
							for (Player players : Bukkit.getOnlinePlayers()) {
								if (players.hasPermission("AltCheck.admin")) {
									players.sendMessage(AltCheck.ALTCHECK_PREFIX
											+ translateAlternateColorCodes('&', "&7" + p.getName() + " (Unknow) has "
													+ yml.getStringList(ip).size() + " accounts."));
								}

							}
						} else {
							for (Player players : Bukkit.getOnlinePlayers()) {
								if (players.hasPermission("AltCheck.admin")) {
									players.sendMessage(AltCheck.ALTCHECK_PREFIX
											+ translateAlternateColorCodes('&',
													"&7" + p.getName() + " (" + json.country_name + ") has "
															+ yml.getStringList(ip).size() + " accounts."));
								}

							}
						}
					} else {
						for (Player players : Bukkit.getOnlinePlayers()) {
							if (players.hasPermission("AltCheck.admin")) {
								players.sendMessage(AltCheck.ALTCHECK_PREFIX + translateAlternateColorCodes('&',
										"&7" + p.getName() + " (" + country + ") has " + yml.getStringList(ip).size()
												+ " accounts."));
							}
						}
					}
				}
			}
		};
		t.start();

	}

}