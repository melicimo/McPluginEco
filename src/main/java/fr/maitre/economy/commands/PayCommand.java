package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public PayCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.colorize("&cCette commande est réservée aux joueurs."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length != 2) {
            player.sendMessage(plugin.colorize("&cUtilisation: /pay <joueur> <montant>"));
            return true;
        }
        
        if (!plugin.getConfig().getBoolean("settings.allow-player-transfers", true)) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cLes transferts entre joueurs sont désactivés."));
            return true;
        }
        
        // Vérifier le joueur cible
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.player-not-found")));
            return true;
        }
        
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cVous ne pouvez pas vous envoyer de l'argent à vous-même."));
            return true;
        }
        
        // Vérifier le montant
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.invalid-amount")));
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cLe montant doit être positif."));
            return true;
        }
        
        double minTransfer = plugin.getConfig().getDouble("settings.min-transfer-amount", 1);
        if (amount < minTransfer) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cMontant minimum: " + plugin.formatMoney(minTransfer)));
            return true;
        }
        
        // Vérifier le solde
        if (!plugin.getEconomyManager().hasBalance(player, amount)) {
            double missing = amount - plugin.getEconomyManager().getBalance(player);
            String message = plugin.getConfig().getString("messages.insufficient-funds")
                .replace("{amount}", plugin.formatMoney(missing))
                .replace("{currency}", plugin.getCurrencyName(missing));
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return true;
        }
        
        // Effectuer le transfert
        if (plugin.getEconomyManager().transferBalance(player, target, amount)) {
            String messageSent = plugin.getConfig().getString("messages.pay-sent")
                .replace("{amount}", plugin.formatMoney(amount))
                .replace("{currency}", plugin.getCurrencyName(amount))
                .replace("{player}", target.getName());
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageSent));
            
            // Notifier le destinataire s'il est en ligne
            if (target.isOnline()) {
                Player targetPlayer = (Player) target;
                String messageReceived = plugin.getConfig().getString("messages.pay-received")
                    .replace("{amount}", plugin.formatMoney(amount))
                    .replace("{currency}", plugin.getCurrencyName(amount))
                    .replace("{player}", player.getName());
                targetPlayer.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageReceived));
            }
            
            return true;
        }
        
        player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
            "&cErreur lors du transfert."));
        return true;
    }
}
