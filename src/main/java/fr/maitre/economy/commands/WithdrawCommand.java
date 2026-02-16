package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WithdrawCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public WithdrawCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.colorize("&cCette commande est réservée aux joueurs."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getConfig().getBoolean("withdraw.enabled", true)) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cLe retrait d'argent physique est désactivé."));
            return true;
        }
        
        if (!player.hasPermission("economy.withdraw")) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.no-permission")));
            return true;
        }
        
        if (args.length != 1) {
            player.sendMessage(plugin.colorize("&cUtilisation: /withdraw <montant>"));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.invalid-amount")));
            return true;
        }
        
        double minAmount = plugin.getConfig().getDouble("withdraw.min-amount", 10);
        double maxAmount = plugin.getConfig().getDouble("withdraw.max-amount", 100000);
        
        if (amount < minAmount) {
            String message = plugin.getConfig().getString("messages.withdraw-too-low")
                .replace("{min}", plugin.formatMoney(minAmount))
                .replace("{currency}", plugin.getCurrencyName(minAmount));
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return true;
        }
        
        if (amount > maxAmount) {
            String message = plugin.getConfig().getString("messages.withdraw-too-high")
                .replace("{max}", plugin.formatMoney(maxAmount))
                .replace("{currency}", plugin.getCurrencyName(maxAmount));
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return true;
        }
        
        if (!plugin.getEconomyManager().hasBalance(player, amount)) {
            double missing = amount - plugin.getEconomyManager().getBalance(player);
            String message = plugin.getConfig().getString("messages.insufficient-funds")
                .replace("{amount}", plugin.formatMoney(missing))
                .replace("{currency}", plugin.getCurrencyName(missing));
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return true;
        }
        
        // Retirer l'argent du compte
        if (!plugin.getEconomyManager().removeBalance(player, amount)) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cErreur lors du retrait."));
            return true;
        }
        
        // Créer l'item physique
        ItemStack moneyItem = createMoneyItem(amount);
        
        // Donner l'item au joueur
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), moneyItem);
            player.sendMessage(plugin.colorize("&eVotre inventaire est plein! L'argent a été déposé au sol."));
        } else {
            player.getInventory().addItem(moneyItem);
        }
        
        String message = plugin.getConfig().getString("messages.withdraw-success")
            .replace("{amount}", plugin.formatMoney(amount))
            .replace("{currency}", plugin.getCurrencyName(amount));
        player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
        
        return true;
    }
    
    private ItemStack createMoneyItem(double amount) {
        ItemStack item;
        
        // Essayer d'utiliser un item personnalisé
        String moneyItemId = plugin.getConfig().getString("withdraw.money-item", "");
        if (plugin.getItemHook() != null && !moneyItemId.isEmpty()) {
            ItemStack customItem = plugin.getItemHook().getItemStack(moneyItemId);
            if (customItem != null) {
                item = customItem.clone();
            } else {
                item = new ItemStack(Material.PAPER);
            }
        } else {
            item = new ItemStack(Material.PAPER);
        }
        
        // Personnaliser l'item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.colorize("&6&l" + plugin.formatMoney(amount)));
            
            List<String> lore = new ArrayList<>();
            lore.add(plugin.colorize("&7Billet de banque"));
            lore.add(plugin.colorize("&7Clic droit pour encaisser"));
            lore.add("");
            lore.add(plugin.colorize("&eValeur: &a" + plugin.formatMoney(amount)));
            meta.setLore(lore);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
