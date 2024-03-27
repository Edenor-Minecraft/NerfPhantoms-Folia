package org.altronmaxx.nerfphantomsfolia;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class EventsHandler implements Listener {
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (Nerfphantoms_folia.getInstance().isWorldEnabled(world)) {
            PhantomUtils.nerf(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (victim.getType() != EntityType.PLAYER || damager.getType() != EntityType.PHANTOM) {
            return;
        }

        if (!Nerfphantoms_folia.getInstance().isWorldEnabled(victim.getWorld())) {
            return;
        }

        // Phantom damages player
        // => Modify damage
        double damageModifier = Nerfphantoms_folia.getInstance().config.getDouble("damageModifier");
        double nerfedDamage = PhantomUtils.roundToHalf(event.getDamage() * damageModifier);
        event.setDamage(nerfedDamage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Nerfphantoms_folia.getInstance().phantomDisabled.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("nerfphantoms.disablespawn.auto")) {
            PhantomUtils.togglePhantomSpawn(player, false);
            return;
        }

        if(Nerfphantoms_folia.getInstance().storage == null) {
            return;
        }
        // Check storage for disabled player setting (async)
        try {
            if (Nerfphantoms_folia.getInstance().storage.getPhantomDisabled(player.getUniqueId())) {
                PhantomUtils.togglePhantomSpawn(player, false);
            }
        } catch (SQLException throwables) {
            Nerfphantoms_folia.getInstance().logger.info("Error while fetching player data from storage");
            throwables.printStackTrace();
        }
    }
}
