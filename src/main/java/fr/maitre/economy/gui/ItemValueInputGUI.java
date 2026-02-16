package fr.maitre.economy.gui;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemValueInputGUI implements Listener {
    
    private final EconomyPlugin plugin;
    private final Player player;
    private final String itemId;
    private Inventory inventory;
    private double currentValue;
    private boolean sellable;
    private boolean buyable;
    private double buyPrice;
    
    public ItemValueInputGUI(EconomyPlugin plugin, Player player, String itemId) {
        this.plugin = plugin;
        this.player = player;
        this.itemId = itemId;
        
        // Charger les valeurs existantes si l'item existe déjà
        var existingValue = plugin.getItemValueManager().getItemValue(itemId);
        if (existingValue != null) {
            this.currentValue = existingValue.getValue();
            this.sellable = existingValue.isSellable();
            this.buyable = existingValue.isBuyable();
            this.buyPrice = existingValue.getBuyPrice();
        } else {
            this.currentValue = 0;
            this.sellable = true;
            this.buyable = false;
            this.buyPrice = 0;
        }
        
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void start() {
        openGUI();
    }
    
    private void openGUI() {
        inventory = Bukkit.createInventory(null, 27, plugin.colorize("&6Configurer: " + itemId));
        
        // Item preview
        ItemStack preview = null;
        if (plugin.getItemHook() != null) {
            preview = plugin.getItemHook().getItemStack(itemId);
        }
        if (preview == null) {
            preview = new ItemStack(Material.BARRIER);
        }
        
        ItemMeta previewMeta = preview.getItemMeta();
        if (previewMeta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(plugin.colorize("&7ID: &f" + itemId));
            lore.add("");
            previewMeta.setLore(lore);
            preview.setItemMeta(previewMeta);
        }
        inventory.setItem(4, preview);
        
        // Bouton valeur
        updateValueButton();
        
        // Bouton sellable
        updateSellableButton();
        
        // Bouton buyable
        updateBuyableButton();
        
        // Bouton prix achat
        updateBuyPriceButton();
        
        // Bouton valider
        ItemStack confirm = createItem(Material.LIME_CONCRETE, "&a&lValider", Arrays.asList(
            "",
            "&7Sauvegarder les paramètres",
            "&7de cet item économique."
        ));
        inventory.setItem(22, confirm);
        
        // Bouton annuler
        ItemStack cancel = createItem(Material.RED_CONCRETE, "&c&lAnnuler", Arrays.asList(
            "",
            "&7Annuler et retourner",
            "&7au menu principal."
        ));
        inventory.setItem(18, cancel);
        
        player.openInventory(inventory);
    }
    
    private void updateValueButton() {
        ItemStack valueButton = createItem(Material.GOLD_INGOT, "&e&lValeur de vente", Arrays.asList(
            "",
            "&7Valeur actuelle: &a" + plugin.formatMoney(currentValue),
            "",
            "&eClick: &fModifier la valeur"
        ));
        inventory.setItem(10, valueButton);
    }
    
    private void updateSellableButton() {
        ItemStack sellableButton = createItem(
            sellable ? Material.GREEN_WOOL : Material.RED_WOOL,
            "&e&lVendable aux joueurs",
            Arrays.asList(
                "",
                "&7Statut: " + (sellable ? "&a✓ Activé" : "&c✗ Désactivé"),
                "",
                "&eClick: &fBasculer"
            )
        );
        inventory.setItem(12, sellableButton);
    }
    
    private void updateBuyableButton() {
        ItemStack buyableButton = createItem(
            buyable ? Material.GREEN_WOOL : Material.RED_WOOL,
            "&e&lAchetable par les joueurs",
            Arrays.asList(
                "",
                "&7Statut: " + (buyable ? "&a✓ Activé" : "&c✗ Désactivé"),
                "",
                "&eClick: &fBasculer"
            )
        );
        inventory.setItem(14, buyableButton);
    }
    
    private void updateBuyPriceButton() {
        ItemStack buyPriceButton = createItem(
            Material.DIAMOND,
            "&e&lPrix d'achat",
            Arrays.asList(
                "",
                "&7Prix actuel: &a" + plugin.formatMoney(buyPrice),
                "",
                buyable ? "&eClick: &fModifier le prix" : "&7(Achat désactivé)"
            )
        );
        inventory.setItem(16, buyPriceButton);
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null) return;
        
        int slot = event.getSlot();
        
        switch (slot) {
            case 10: // Modifier la valeur
                player.closeInventory();
                askForValue();
                break;
                
            case 12: // Toggle sellable
                sellable = !sellable;
                updateSellableButton();
                break;
                
            case 14: // Toggle buyable
                buyable = !buyable;
                if (buyable && buyPrice == 0) {
                    buyPrice = currentValue * 1.1; // Prix par défaut
                }
                updateBuyableButton();
                updateBuyPriceButton();
                break;
                
            case 16: // Modifier le prix d'achat
                if (buyable) {
                    player.closeInventory();
                    askForBuyPrice();
                }
                break;
                
            case 22: // Confirmer
                plugin.getItemValueManager().setItemValue(itemId, currentValue, sellable, buyable, buyPrice);
                String message = plugin.getConfig().getString("messages.item-value-set")
                    .replace("{value}", plugin.formatMoney(currentValue))
                    .replace("{currency}", plugin.getCurrencyName(currentValue));
                player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + message));
                player.closeInventory();
                
                // Retourner au GUI principal
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    new EconomyItemsGUI(plugin).open(player);
                }, 5L);
                break;
                
            case 18: // Annuler
                player.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    new EconomyItemsGUI(plugin).open(player);
                }, 5L);
                break;
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            InventoryClickEvent.getHandlerList().unregister(this);
            InventoryCloseEvent.getHandlerList().unregister(this);
        }
    }
    
    private void askForValue() {
        ConversationFactory factory = new ConversationFactory(plugin)
            .withModality(true)
            .withFirstPrompt(new ValuePrompt())
            .withTimeout(60)
            .withLocalEcho(false)
            .addConversationAbandonedListener(event -> {
                if (!event.gracefulExit()) {
                    player.sendMessage(plugin.colorize("&cSaisie annulée."));
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> openGUI(), 5L);
            });
        
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
    }
    
    private void askForBuyPrice() {
        ConversationFactory factory = new ConversationFactory(plugin)
            .withModality(true)
            .withFirstPrompt(new BuyPricePrompt())
            .withTimeout(60)
            .withLocalEcho(false)
            .addConversationAbandonedListener(event -> {
                if (!event.gracefulExit()) {
                    player.sendMessage(plugin.colorize("&cSaisie annulée."));
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> openGUI(), 5L);
            });
        
        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
    }
    
    private class ValuePrompt extends NumericPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return plugin.colorize("&eEntrez la valeur de vente de l'item (ou 'annuler'):");
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
            currentValue = input.doubleValue();
            if (buyPrice == 0 || buyPrice < currentValue) {
                buyPrice = currentValue * 1.1;
            }
            player.sendMessage(plugin.colorize("&aValeur définie à: " + plugin.formatMoney(currentValue)));
            return Prompt.END_OF_CONVERSATION;
        }
        
        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.doubleValue() >= 0;
        }
        
        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return plugin.colorize("&cLa valeur doit être un nombre positif.");
        }
        
        @Override
        protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
            if (invalidInput.equalsIgnoreCase("annuler") || invalidInput.equalsIgnoreCase("cancel")) {
                return null;
            }
            return plugin.colorize("&cVeuillez entrer un nombre valide.");
        }
    }
    
    private class BuyPricePrompt extends NumericPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return plugin.colorize("&eEntrez le prix d'achat de l'item (ou 'annuler'):");
        }
        
        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
            buyPrice = input.doubleValue();
            player.sendMessage(plugin.colorize("&aPrix d'achat défini à: " + plugin.formatMoney(buyPrice)));
            return Prompt.END_OF_CONVERSATION;
        }
        
        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.doubleValue() >= 0;
        }
        
        @Override
        protected String getFailedValidationText(ConversationContext context, Number invalidInput) {
            return plugin.colorize("&cLe prix doit être un nombre positif.");
        }
        
        @Override
        protected String getInputNotNumericText(ConversationContext context, String invalidInput) {
            if (invalidInput.equalsIgnoreCase("annuler") || invalidInput.equalsIgnoreCase("cancel")) {
                return null;
            }
            return plugin.colorize("&cVeuillez entrer un nombre valide.");
        }
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.colorize(name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(plugin.colorize(line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
