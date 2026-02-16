package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import fr.maitre.economy.data.EconomyData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class BalTopCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public BalTopCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                    "&cNuméro de page invalide."));
                return true;
            }
        }
        
        if (page < 1) {
            page = 1;
        }
        
        int perPage = plugin.getConfig().getInt("baltop.per-page", 10);
        List<Map.Entry<java.util.UUID, EconomyData>> topBalances = plugin.getDataManager().getTopBalances(0);
        
        int maxPage = (int) Math.ceil((double) topBalances.size() / perPage);
        if (page > maxPage) {
            page = maxPage;
        }
        
        if (topBalances.isEmpty()) {
            sender.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                "&cAucune donnée économique disponible."));
            return true;
        }
        
        // Afficher l'en-tête
        List<String> header = plugin.getConfig().getStringList("baltop.format.header");
        for (String line : header) {
            line = line.replace("{page}", String.valueOf(page))
                      .replace("{maxpage}", String.valueOf(maxPage));
            sender.sendMessage(plugin.colorize(line));
        }
        
        // Afficher les joueurs
        String lineFormat = plugin.getConfig().getString("baltop.format.line");
        int startIndex = (page - 1) * perPage;
        int endIndex = Math.min(startIndex + perPage, topBalances.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<java.util.UUID, EconomyData> entry = topBalances.get(i);
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            double balance = entry.getValue().getBalance();
            
            String line = lineFormat.replace("{rank}", String.valueOf(i + 1))
                                   .replace("{player}", player.getName())
                                   .replace("{balance}", plugin.formatMoney(balance))
                                   .replace("{currency}", plugin.getCurrencyName(balance));
            sender.sendMessage(plugin.colorize(line));
        }
        
        // Afficher le pied de page
        List<String> footer = plugin.getConfig().getStringList("baltop.format.footer");
        for (String line : footer) {
            sender.sendMessage(plugin.colorize(line));
        }
        
        return true;
    }
}
