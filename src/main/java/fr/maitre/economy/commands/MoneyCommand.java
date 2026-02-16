package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MoneyCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public MoneyCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.colorize("&cCette commande est réservée aux joueurs."));
            return true;
        }
        
        Player player = (Player) sender;
        
        // /money ou /money <joueur>
        if (args.length == 0) {
            displayWallet(player, player);
            return true;
        }
        
        // Vérifier un autre joueur
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                    plugin.getConfig().getString("messages.player-not-found")));
                return true;
            }
            
            displayWallet(player, target);
            return true;
        }
        
        return false;
    }
    
    private void displayWallet(Player viewer, OfflinePlayer target) {
        double balance = plugin.getEconomyManager().getBalance(target.getUniqueId());
        int rank = plugin.getEconomyManager().getRank(target.getUniqueId());
        int total = plugin.getDataManager().getAllData().size();
        
        // Afficher le portefeuille stylisé
        if (plugin.getConfig().getBoolean("wallet-display.enabled", true)) {
            List<String> format = plugin.getConfig().getStringList("wallet-display.format");
            
            // Envoyer l'image si configurée
            String chatImage = plugin.getConfig().getString("wallet-display.chat-image", "");
            if (!chatImage.isEmpty()) {
                viewer.sendMessage(plugin.colorize(chatImage));
            }
            
            for (String line : format) {
                line = line.replace("{player}", target.getName())
                          .replace("{balance}", plugin.formatMoney(balance))
                          .replace("{currency}", plugin.getCurrencyName(balance))
                          .replace("{rank}", String.valueOf(rank))
                          .replace("{total}", String.valueOf(total));
                viewer.sendMessage(plugin.colorize(line));
            }
        } else {
            // Format simple
            if (target.getUniqueId().equals(viewer.getUniqueId())) {
                String message = plugin.getConfig().getString("messages.balance-self")
                    .replace("{balance}", plugin.formatMoney(balance))
                    .replace("{currency}", plugin.getCurrencyName(balance));
                viewer.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            } else {
                String message = plugin.getConfig().getString("messages.balance-other")
                    .replace("{player}", target.getName())
                    .replace("{balance}", plugin.formatMoney(balance))
                    .replace("{currency}", plugin.getCurrencyName(balance));
                viewer.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            }
        }
    }
}
