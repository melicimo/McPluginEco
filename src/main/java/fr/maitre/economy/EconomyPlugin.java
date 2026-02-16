package fr.maitre.economy;

import fr.maitre.economy.commands.*;
import fr.maitre.economy.data.DataManager;
import fr.maitre.economy.data.EconomyData;
import fr.maitre.economy.hooks.ItemHook;
import fr.maitre.economy.listeners.PlayerListener;
import fr.maitre.economy.managers.EconomyManager;
import fr.maitre.economy.managers.ItemValueManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EconomyPlugin extends JavaPlugin {
    
    private static EconomyPlugin instance;
    private DataManager dataManager;
    private EconomyManager economyManager;
    private ItemValueManager itemValueManager;
    private ItemHook itemHook;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Sauvegarder la config par défaut
        saveDefaultConfig();
        
        // Initialiser les managers
        this.dataManager = new DataManager(this);
        this.economyManager = new EconomyManager(this);
        this.itemValueManager = new ItemValueManager(this);
        
        // Détecter et initialiser le hook d'items
        initializeItemHook();
        
        // Charger les données
        dataManager.loadData();
        itemValueManager.loadItemValues();
        
        // Enregistrer les commandes
        registerCommands();
        
        // Enregistrer les listeners
        registerListeners();
        
        // Message de démarrage
        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   EconomyPlugin v1.0.0             ║");
        getLogger().info("║   Développé par Maitre             ║");
        getLogger().info("║   Status: Activé ✓                 ║");
        if (itemHook != null) {
            getLogger().info("║   Hook: " + itemHook.getName() + " ✓              ║");
        } else {
            getLogger().info("║   Hook: Aucun (vanilla items)      ║");
        }
        getLogger().info("╚════════════════════════════════════╝");
    }
    
    @Override
    public void onDisable() {
        // Sauvegarder les données
        if (dataManager != null) {
            dataManager.saveData();
        }
        if (itemValueManager != null) {
            itemValueManager.saveItemValues();
        }
        
        getLogger().info("╔════════════════════════════════════╗");
        getLogger().info("║   EconomyPlugin v1.0.0             ║");
        getLogger().info("║   Status: Désactivé ✓              ║");
        getLogger().info("╚════════════════════════════════════╝");
    }
    
    private void initializeItemHook() {
        // Vérifier ItemsAdder en premier
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            try {
                this.itemHook = new ItemsAdderHook();
                getLogger().info("Hook ItemsAdder initialisé avec succès!");
                return;
            } catch (Exception e) {
                getLogger().warning("Erreur lors de l'initialisation d'ItemsAdder: " + e.getMessage());
            }
        }
        
        // Vérifier Oraxen ensuite
        if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            try {
                this.itemHook = new OraxenHook();
                getLogger().info("Hook Oraxen initialisé avec succès!");
                return;
            } catch (Exception e) {
                getLogger().warning("Erreur lors de l'initialisation d'Oraxen: " + e.getMessage());
            }
        }
        
        getLogger().info("Aucun plugin d'items personnalisés détecté. Mode vanilla activé.");
    }
    
    private void registerCommands() {
        getCommand("money").setExecutor(new MoneyCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("economyitems").setExecutor(new EconomyItemsCommand(this));
        getCommand("baltop").setExecutor(new BalTopCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    public void reload() {
        // Recharger la config
        reloadConfig();
        
        // Recharger les données
        dataManager.loadData();
        itemValueManager.loadItemValues();
        
        // Réinitialiser le hook
        initializeItemHook();
    }
    
    public static EconomyPlugin getInstance() {
        return instance;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public ItemValueManager getItemValueManager() {
        return itemValueManager;
    }
    
    public ItemHook getItemHook() {
        return itemHook;
    }
    
    public String formatMoney(double amount) {
        String symbol = getConfig().getString("settings.currency-symbol", "€");
        return String.format("%.2f%s", amount, symbol);
    }
    
    public String getCurrencyName(double amount) {
        if (amount <= 1) {
            return getConfig().getString("settings.currency-name", "Euro");
        }
        return getConfig().getString("settings.currency-name-plural", "Euros");
    }
    
    public String colorize(String message) {
        return message.replace("&", "§");
    }
}
