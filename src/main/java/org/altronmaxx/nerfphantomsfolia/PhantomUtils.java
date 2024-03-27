package org.altronmaxx.nerfphantomsfolia;

import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.sql.SQLException;
import java.util.Collection;

public class PhantomUtils {

    public static boolean togglePhantomSpawn(Player player) {
        return togglePhantomSpawn(player, true);
    }

    public static boolean togglePhantomSpawn(Player player, boolean persist) {
        boolean isDisabled = Nerfphantoms_folia.getInstance().phantomDisabled.contains(player);

        if (isDisabled) {
            Nerfphantoms_folia.getInstance().phantomDisabled.remove(player);
        } else {
            // Initial stat reset, subsequent calls will be done by scheduled task
            if (Nerfphantoms_folia.getInstance().isWorldEnabled(player.getWorld())) {
                // We could be in an async context here, schedule the bukkit api access sync.
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }
            Nerfphantoms_folia.getInstance().phantomDisabled.add(player);
        }

        // Store setting in database (async)
        if (persist && Nerfphantoms_folia.getInstance().storage != null) {
            try {
                Nerfphantoms_folia.getInstance().storage.setPhantomDisabled(player.getUniqueId(), !isDisabled);
            } catch (SQLException e) {
                Nerfphantoms_folia.getInstance().logger.info("Error while updating player data in storage");
                throw new RuntimeException(e);
            }
        }

        return !isDisabled;
    }
    public static int killAllPhantoms(World world) {
        Collection<Phantom> phantoms = world.getEntitiesByClass(Phantom.class);
        int n = phantoms.size();
        for (Phantom phantom : phantoms) {
            phantom.remove();
        }
        return n;
    }
    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }
    public static void nerf(CreatureSpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity.getType() != EntityType.PHANTOM) {
            return;
        }
        // Phantom spawn
        // Natural spawn?
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (!Nerfphantoms_folia.getInstance().config.getBoolean("allowNaturalSpawn")) {
                // Disable natural spawn
                event.setCancelled(true);
                return;
            }
        } else {
            if (Nerfphantoms_folia.getInstance().config.getBoolean("onlyNerfNatural")) return;
        }
        // Nerf
        Phantom phantom = (Phantom) event.getEntity();

        phantom.setSilent(Nerfphantoms_folia.getInstance().config.getBoolean("muteSound"));
        phantom.setAI(!Nerfphantoms_folia.getInstance().config.getBoolean("disableAI"));
        phantom.setHealth(Nerfphantoms_folia.getInstance().config.getDouble("health"));
        if (Nerfphantoms_folia.getInstance().config.getBoolean("fixedSize.enabled")) {
            phantom.setSize(Nerfphantoms_folia.getInstance().config.getInt("fixedSize.value"));
        }
    }
}
