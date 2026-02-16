package fr.maitre.economy.managers;

import fr.maitre.economy.EconomyPlugin;
import fr.maitre.economy.data.EconomyData;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class EconomyManager {
    
    private final EconomyPlugin plugin;
    
    public EconomyManager(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    public double getBalance(UUID uuid) {
        EconomyData data = plugin.getDataManager().getData(uuid);
        return data.getBalance();
    }
    
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }
    
    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
    
    public boolean hasBalance(OfflinePlayer player, double amount) {
        return hasBalance(player.getUniqueId(), amount);
    }
    
    public void setBalance(UUID uuid, double amount) {
        double maxBalance = plugin.getConfig().getDouble("settings.max-balance", 1000000000);
        double minBalance = plugin.getConfig().getDouble("settings.min-balance", 0);
        
        amount = Math.max(minBalance, Math.min(maxBalance, amount));
        
        EconomyData data = plugin.getDataManager().getData(uuid);
        data.setBalance(amount);
    }
    
    public void setBalance(OfflinePlayer player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }
    
    public boolean addBalance(UUID uuid, double amount) {
        if (amount < 0) return false;
        
        EconomyData data = plugin.getDataManager().getData(uuid);
        double newBalance = data.getBalance() + amount;
        double maxBalance = plugin.getConfig().getDouble("settings.max-balance", 1000000000);
        
        if (newBalance > maxBalance) {
            data.setBalance(maxBalance);
            return false;
        }
        
        data.addBalance(amount);
        return true;
    }
    
    public boolean addBalance(OfflinePlayer player, double amount) {
        return addBalance(player.getUniqueId(), amount);
    }
    
    public boolean removeBalance(UUID uuid, double amount) {
        if (amount < 0) return false;
        
        EconomyData data = plugin.getDataManager().getData(uuid);
        if (data.getBalance() < amount) {
            return false;
        }
        
        double newBalance = data.getBalance() - amount;
        double minBalance = plugin.getConfig().getDouble("settings.min-balance", 0);
        
        if (newBalance < minBalance) {
            return false;
        }
        
        data.removeBalance(amount);
        return true;
    }
    
    public boolean removeBalance(OfflinePlayer player, double amount) {
        return removeBalance(player.getUniqueId(), amount);
    }
    
    public boolean transferBalance(UUID from, UUID to, double amount) {
        if (amount <= 0) return false;
        
        if (!hasBalance(from, amount)) {
            return false;
        }
        
        if (removeBalance(from, amount)) {
            addBalance(to, amount);
            return true;
        }
        
        return false;
    }
    
    public boolean transferBalance(OfflinePlayer from, OfflinePlayer to, double amount) {
        return transferBalance(from.getUniqueId(), to.getUniqueId(), amount);
    }
    
    public void resetBalance(UUID uuid) {
        double startingBalance = plugin.getConfig().getDouble("settings.starting-balance", 1000);
        setBalance(uuid, startingBalance);
    }
    
    public void resetBalance(OfflinePlayer player) {
        resetBalance(player.getUniqueId());
    }
    
    public int getRank(UUID uuid) {
        var topBalances = plugin.getDataManager().getTopBalances(0);
        for (int i = 0; i < topBalances.size(); i++) {
            if (topBalances.get(i).getKey().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }
}
