package org.altronmaxx.nerfphantomsfolia;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class Nerfphantoms_folia extends JavaPlugin implements Listener{

    public final Logger logger = Logger.getLogger(this.getName());
    public Storage storage;
    public FileConfiguration config;
    Set<Player> phantomDisabled = ConcurrentHashMap.newKeySet();
    public static Nerfphantoms_folia instance;

    private StatResetTask task;
    @Override
    public void onEnable() {
        instance = this;

        //new Metrics(this);
        initConfig();

        // Initialize database if enabled
        ConfigurationSection databaseCfg = config.getConfigurationSection("database");
        if(databaseCfg != null && databaseCfg.getBoolean("enabled")) {
            storage = new Storage(databaseCfg);
            try {
                storage.init(this);
                logger.info("Database connection established");
            } catch(SQLException ex) {
                storage = null;
                logger.info("Error while connection to database");
                throw new RuntimeException(ex);
            }
        }

        new NerfphantomsCommand();
        getServer().getPluginManager().registerEvents(this, this);
        task = new StatResetTask(this);
        Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(this, value -> task.run(), 1L, 1200L);
        Bukkit.getPluginManager().registerEvents(new EventsHandler(), this);
    }

    public static Nerfphantoms_folia getInstance(){
        return instance;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    boolean isWorldEnabled(World world) {
        if (world == null) {
            return false;
        }
        List<String> enabledWorlds = config.getStringList("enabledWorlds");
        // If no worlds are defined in "enabledWorlds", disable allowlist functionality and treat
        // all worlds as enabled.
        return enabledWorlds.isEmpty() || enabledWorlds.contains(world.getName());
    }
    private void initConfig() {
        config = this.getConfig();

        // Warn if "enabledWorlds" contains unknown worlds.
        // If config isn't initialized at this point "enabledWorlds" will be an empty list.
        List<String> enabledWorlds = config.getStringList("enabledWorlds");
        for(String worldName : enabledWorlds) {
            if(Bukkit.getWorld(worldName) == null) {
                logger.warning("Config entry \"enabledWorlds\" contains unknown world '" + worldName + "'.");
            }
        }

        MemoryConfiguration defaultConfig = getMemoryConfiguration();

        ConfigurationSection db = defaultConfig.createSection("database");
        db.set("enabled", true);
        db.set("type", "sqlite");
        db.set("host", "localhost");
        db.set("port", 3306);
        db.set("name", "nerfphantoms");
        db.set("username", "user");
        db.set("password", "123456");

        config.setDefaults(defaultConfig);
        config.options().copyDefaults(true);
        saveConfig();
    }

    @NotNull
    private static MemoryConfiguration getMemoryConfiguration() {
        MemoryConfiguration defaultConfig = new MemoryConfiguration();

        ArrayList<String> worldNames = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            worldNames.add(world.getName());
        }

        defaultConfig.set("enabledWorlds", worldNames);
        defaultConfig.set("allowNaturalSpawn", true);
        defaultConfig.set("onlyNerfNatural", true);
        defaultConfig.set("muteSound", false);
        defaultConfig.set("disableAI", false);
        defaultConfig.set("health", 20d);
        defaultConfig.set("damageModifier", 1.0);
        defaultConfig.set("fixedSize.enabled", false);
        defaultConfig.set("fixedSize.value", 1);
        return defaultConfig;
    }
}
