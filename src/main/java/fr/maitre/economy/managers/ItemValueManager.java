package fr.maitre.economy.managers;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItemValueManager {
    
    private final EconomyPlugin plugin;
    private final Map<String, ItemValue> itemValues;
    private File itemsFile;
    private FileConfiguration itemsConfig;
    
    public ItemValueManager(EconomyPlugin plugin) {
        this.plugin = plugin;
        this.itemValues = new HashMap<>();
        
        // Créer le fichier items.yml
        itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            try {
                itemsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Impossible de créer items.yml: " + e.getMessage());
            }
        }
        itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
    }
    
    public void loadItemValues() {
        itemValues.clear();
        
        // Charger depuis la config principale
        ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("economy-items.items");
        if (configSection != null) {
            for (String key : configSection.getKeys(false)) {
                double value = configSection.getDouble(key + ".value", 0);
                boolean sellable = configSection.getBoolean(key + ".sellable", true);
                boolean buyable = configSection.getBoolean(key + ".buyable", false);
                double buyPrice = configSection.getDouble(key + ".buy-price", value * 1.1);
                
                ItemValue itemValue = new ItemValue(value, sellable, buyable, buyPrice);
                itemValues.put(key, itemValue);
            }
        }
        
        // Charger depuis items.yml (prioritaire)
        if (itemsConfig.contains("items")) {
            for (String key : itemsConfig.getConfigurationSection("items").getKeys(false)) {
                double value = itemsConfig.getDouble("items." + key + ".value", 0);
                boolean sellable = itemsConfig.getBoolean("items." + key + ".sellable", true);
                boolean buyable = itemsConfig.getBoolean("items." + key + ".buyable", false);
                double buyPrice = itemsConfig.getDouble("items." + key + ".buy-price", value * 1.1);
                
                ItemValue itemValue = new ItemValue(value, sellable, buyable, buyPrice);
                itemValues.put(key, itemValue);
            }
        }
        
        plugin.getLogger().info("Chargement de " + itemValues.size() + " items économiques.");
    }
    
    public void saveItemValues() {
        for (Map.Entry<String, ItemValue> entry : itemValues.entrySet()) {
            String key = entry.getKey();
            ItemValue value = entry.getValue();
            
            itemsConfig.set("items." + key + ".value", value.getValue());
            itemsConfig.set("items." + key + ".sellable", value.isSellable());
            itemsConfig.set("items." + key + ".buyable", value.isBuyable());
            itemsConfig.set("items." + key + ".buy-price", value.getBuyPrice());
        }
        
        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder items.yml: " + e.getMessage());
        }
    }
    
    public void setItemValue(String itemId, double value, boolean sellable, boolean buyable, double buyPrice) {
        ItemValue itemValue = new ItemValue(value, sellable, buyable, buyPrice);
        itemValues.put(itemId, itemValue);
        saveItemValues();
    }
    
    public void setItemValue(String itemId, double value) {
        setItemValue(itemId, value, true, false, value * 1.1);
    }
    
    public ItemValue getItemValue(String itemId) {
        return itemValues.get(itemId);
    }
    
    public double getValue(String itemId) {
        ItemValue value = itemValues.get(itemId);
        return value != null ? value.getValue() : 0;
    }
    
    public boolean hasValue(String itemId) {
        return itemValues.containsKey(itemId);
    }
    
    public void removeItemValue(String itemId) {
        itemValues.remove(itemId);
        itemsConfig.set("items." + itemId, null);
        saveItemValues();
    }
    
    public Map<String, ItemValue> getAllItemValues() {
        return new HashMap<>(itemValues);
    }
    
    public String getItemId(ItemStack item) {
        if (plugin.getItemHook() != null) {
            return plugin.getItemHook().getItemId(item);
        }
        return null;
    }
    
    public static class ItemValue {
        private final double value;
        private final boolean sellable;
        private final boolean buyable;
        private final double buyPrice;
        
        public ItemValue(double value, boolean sellable, boolean buyable, double buyPrice) {
            this.value = value;
            this.sellable = sellable;
            this.buyable = buyable;
            this.buyPrice = buyPrice;
        }
        
        public double getValue() {
            return value;
        }
        
        public boolean isSellable() {
            return sellable;
        }
        
        public boolean isBuyable() {
            return buyable;
        }
        
        public double getBuyPrice() {
            return buyPrice;
        }
    }
}
