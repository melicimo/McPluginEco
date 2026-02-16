package fr.maitre.economy.commands;

import fr.maitre.economy.EconomyPlugin;
import fr.maitre.economy.gui.EconomyItemsGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyItemsCommand implements CommandExecutor {
    
    private final EconomyPlugin plugin;
    
    public EconomyItemsCommand(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.colorize("&cCette commande est réservée aux joueurs."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("economy.admin")) {
            player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.no-permission")));
            return true;
        }
        
        // Ouvrir le GUI
        EconomyItemsGUI gui = new EconomyItemsGUI(plugin);
        gui.open(player);
        
        return true;
    }
}
