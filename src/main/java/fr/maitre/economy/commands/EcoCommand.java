package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EcoCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public EcoCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("economy.admin")) {
            sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.no-permission")));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(plugin.colorize("&6&lCommandes Économie Admin:"));
            sender.sendMessage(plugin.colorize("&e/eco give <joueur> <montant> &7- Donner de l'argent"));
            sender.sendMessage(plugin.colorize("&e/eco take <joueur> <montant> &7- Retirer de l'argent"));
            sender.sendMessage(plugin.colorize("&e/eco set <joueur> <montant> &7- Définir le solde"));
            sender.sendMessage(plugin.colorize("&e/eco reset <joueur> &7- Réinitialiser le solde"));
            sender.sendMessage(plugin.colorize("&e/eco reload &7- Recharger la configuration"));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        // /eco reload
        if (subCommand.equals("reload")) {
            plugin.reload();
            sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.reload-success")));
            return true;
        }
        
        // Commandes nécessitant un joueur
        if (args.length < 2) {
            sender.sendMessage(plugin.colorize("&cUtilisation: /eco " + subCommand + " <joueur> [montant]"));
            return true;
        }
        
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        
        switch (subCommand) {
            case "give":
                if (args.length < 3) {
                    sender.sendMessage(plugin.colorize("&cUtilisation: /eco give <joueur> <montant>"));
                    return true;
                }
                
                double giveAmount;
                try {
                    giveAmount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        plugin.getConfig().getString("messages.invalid-amount")));
                    return true;
                }
                
                if (giveAmount <= 0) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&cLe montant doit être positif."));
                    return true;
                }
                
                plugin.getEconomyManager().addBalance(target, giveAmount);
                String messageGive = plugin.getConfig().getString("messages.eco-give")
                    .replace("{amount}", plugin.formatMoney(giveAmount))
                    .replace("{currency}", plugin.getCurrencyName(giveAmount))
                    .replace("{player}", target.getName());
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageGive));
                break;
                
            case "take":
                if (args.length < 3) {
                    sender.sendMessage(plugin.colorize("&cUtilisation: /eco take <joueur> <montant>"));
                    return true;
                }
                
                double takeAmount;
                try {
                    takeAmount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        plugin.getConfig().getString("messages.invalid-amount")));
                    return true;
                }
                
                if (takeAmount <= 0) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&cLe montant doit être positif."));
                    return true;
                }
                
                if (!plugin.getEconomyManager().removeBalance(target, takeAmount)) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&cLe joueur n'a pas assez d'argent."));
                    return true;
                }
                
                String messageTake = plugin.getConfig().getString("messages.eco-take")
                    .replace("{amount}", plugin.formatMoney(takeAmount))
                    .replace("{currency}", plugin.getCurrencyName(takeAmount))
                    .replace("{player}", target.getName());
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageTake));
                break;
                
            case "set":
                if (args.length < 3) {
                    sender.sendMessage(plugin.colorize("&cUtilisation: /eco set <joueur> <montant>"));
                    return true;
                }
                
                double setAmount;
                try {
                    setAmount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        plugin.getConfig().getString("messages.invalid-amount")));
                    return true;
                }
                
                if (setAmount < 0) {
                    sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&cLe montant ne peut pas être négatif."));
                    return true;
                }
                
                plugin.getEconomyManager().setBalance(target, setAmount);
                String messageSet = plugin.getConfig().getString("messages.eco-set")
                    .replace("{amount}", plugin.formatMoney(setAmount))
                    .replace("{currency}", plugin.getCurrencyName(setAmount))
                    .replace("{player}", target.getName());
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageSet));
                break;
                
            case "reset":
                plugin.getEconomyManager().resetBalance(target);
                String messageReset = plugin.getConfig().getString("messages.eco-reset")
                    .replace("{player}", target.getName());
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + messageReset));
                break;
                
            default:
                sender.sendMessage(plugin.colorize("&cSous-commande inconnue. Utilisez /eco pour voir les commandes."));
                break;
        }
        
        return true;
    }
}
