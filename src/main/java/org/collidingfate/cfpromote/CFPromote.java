/**
 * CFPromote - Click sign, get promoted.
 * Copyright (c) 2013, Dion Williams
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package org.collidingfate.cfpromote;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CFPromote extends JavaPlugin implements Listener {
	
	protected PluginManager pm;
	protected Logger log;
	protected Permission vaultPerms;
	private String groupName;
	private String signLabel;
	
	@Override
	public void onEnable() {
		pm = getServer().getPluginManager();
		log = getLogger();
		
		FileConfiguration config = getConfig();
		config.options().copyDefaults(true);
		config.options().copyHeader(true);
		saveConfig();
		
		groupName = config.getString("group-name");
		signLabel = config.getString("sign-label");
		
		if (groupName.isEmpty()) {
			log.severe("Unable to start CFPromote; group-name is not set in the configuration file");
			pm.disablePlugin(this);
			return;
		}
		
		if (setupVault() == false) {
			log.severe("Unable to start CFPromote; Vault dependency failed");
			pm.disablePlugin(this);
			return;
		}
		
		if (!vaultPerms.hasGroupSupport()) {
			log.severe("Unable to start CFPromote; Vault reports the permissions plugin in use doesn't have groups support");
			pm.disablePlugin(this);
			return;
		}
		
		pm.registerEvents(this, this);
		
		log.info("Enabled!");
	}
	
	@Override
	public void onDisable() {
		// Release all our handles now. This is helpful for the garbage
		// collector if the plugin object is kept after being disabled.
		pm = null;
		log = null;
		vaultPerms = null;
		groupName = null;
		signLabel = null;
		
		getLogger().info("Disabled!");
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSignChangedEvent(SignChangeEvent event) {
		// Check the sign for the promotion label
		for ( String line : event.getLines() ) {
			if ( signLabel.equalsIgnoreCase(line) ) {
				if ( !event.getPlayer().hasPermission("cfpromote.placesign") ) {
					event.getPlayer().sendMessage(ChatColor.DARK_RED + "You can't make a promote sign!");
					event.setCancelled(true);
				}
				return;
			}
		}
	}
	
	private boolean setupVault() {
		if (pm.getPlugin("Vault") == null) {
			return false;
		}
		
		RegisteredServiceProvider<Permission> permrsp = getServer().getServicesManager().getRegistration(Permission.class);
		vaultPerms = permrsp.getProvider();
		if (vaultPerms == null) return false;
		
		return true;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		Block block = event.getClickedBlock();
		
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) return;
		
		Sign state = (Sign)block.getState();
		
		// Check the sign for the promotion label
		for ( String line : state.getLines() ) {
			if ( signLabel.equalsIgnoreCase(line) ) {
				Player player = event.getPlayer();
				
				if (!player.hasPermission("cfpromote.getpromoted")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to be promoted.");
					return;
				}
				
				// Search for the group to ensure it exists
				for (String group : vaultPerms.getGroups()) {
					if (group.equalsIgnoreCase(groupName)) {
						if (vaultPerms.playerAddGroup(player, groupName)) {
							player.sendMessage(ChatColor.GREEN + "You have been promoted!");
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Unable to promote you, tell the server admin!");
							getServer().broadcast(ChatColor.RED + "CFPromote: Tried to promote player " + player.getName() + " but Vault says no!", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
							log.warning(ChatColor.RED + "Tried to promote player " + player.getName() + " but Vault says no!");
						}
						return;
					}
				}
				
				// Group doesn't exist
				player.sendMessage(ChatColor.DARK_RED + "Unable to promote you, tell the server admin!");
				getServer().broadcast(ChatColor.RED + "CFPromote: Tried to promote player " + player.getName() + " but the group " + groupName + " doesn't exist!", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
				log.warning(ChatColor.RED + "Tried to promote player " + player.getName() + " but the group " + groupName + " doesn't exist!");
				return;
			}
		}
	}
}
