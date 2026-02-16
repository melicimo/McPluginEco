package fr.maitre.economy.data;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {
    
    private final EconomyPlugin plugin;
    private final Map<UUID, EconomyData> economyCache;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public DataManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        this.economyCache = new ConcurrentHashMap<>();
        
        // Créer le fichier de données
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer le fichier data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void loadData() {
        economyCache.clear();
        
        if (dataConfig.contains("players")) {
            for (String key : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double balance = dataConfig.getDouble("players." + key + ".balance");
                    
                    EconomyData data = new EconomyData(uuid, balance);
                    economyCache.put(uuid, data);
                } catch (Exception e) {
                    plugin.getLogger().warning("Erreur lors du chargement des données pour " + key + ": " + e.getMessage());
                }
            }
        }
        
        plugin.getLogger().info("Chargement de " + economyCache.size() + " comptes économiques.");
    }
    
    public void saveData() {
        for (Map.Entry<UUID, EconomyData> entry : economyCache.entrySet()) {
            UUID uuid = entry.getKey();
            EconomyData data = entry.getValue();
            
            dataConfig.set("players." + uuid.toString() + ".balance", data.getBalance());
            dataConfig.set("players." + uuid.toString() + ".lastUpdated", data.getLastUpdated());
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder data.yml: " + e.getMessage());
        }
    }
    
    public EconomyData getData(UUID uuid) {
        if (!economyCache.containsKey(uuid)) {
            double startingBalance = plugin.getConfig().getDouble("settings.starting-balance", 1000);
            EconomyData data = new EconomyData(uuid, startingBalance);
            economyCache.put(uuid, data);
            return data;
        }
        return economyCache.get(uuid);
    }
    
    public void setData(UUID uuid, EconomyData data) {
        economyCache.put(uuid, data);
    }
    
    public Map<UUID, EconomyData> getAllData() {
        return new HashMap<>(economyCache);
    }
    
    public List<Map.Entry<UUID, EconomyData>> getTopBalances(int limit) {
        List<Map.Entry<UUID, EconomyData>> list = new ArrayList<>(economyCache.entrySet());
        list.sort((a, b) -> Double.compare(b.getValue().getBalance(), a.getValue().getBalance()));
        
        if (limit > 0 && limit < list.size()) {
            return list.subList(0, limit);
        }
        return list;
    }
    
    public void removeData(UUID uuid) {
        economyCache.remove(uuid);
        dataConfig.set("players." + uuid.toString(), null);
    }
}
