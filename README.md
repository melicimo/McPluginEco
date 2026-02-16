# 💰 EconomyPlugin - Système d'Économie Minecraft

Plugin d'économie complet et personnalisable pour Minecraft 1.21.1 avec support complet d'ItemsAdder et Oraxen.

## 🌟 Fonctionnalités

### ✨ Système d'Économie Complet
- 💵 Gestion de l'argent virtuel avec soldes personnalisés
- 🔄 Transferts entre joueurs
- 📊 Classement des joueurs les plus riches (BaltTop)
- 💳 Portefeuille visuel personnalisable avec images
- 🏦 Retrait d'argent physique (billets)

### 🎨 Support Items Personnalisés
- ✅ **ItemsAdder** - Support complet
- ✅ **Oraxen** - Support complet
- 🎯 Détection automatique du plugin installé
- 🔧 Fallback mode vanilla si aucun plugin détecté

### 🖥️ GUI de Gestion Avancé
- 📦 Interface graphique intuitive pour gérer les items économiques
- ➕ Ajout d'items par glisser-déposer
- ✏️ Configuration complète de chaque item:
  - 💰 Valeur de vente
  - ✅ Vendable (oui/non)
  - 🛒 Achetable (oui/non)
  - 💎 Prix d'achat personnalisé
- 💾 Sauvegarde automatique des configurations
- 🔄 Actualisation en temps réel

### 🎮 Commandes

#### Commandes Joueurs
```
/money [joueur]           - Afficher votre portefeuille (ou celui d'un autre)
/pay <joueur> <montant>   - Envoyer de l'argent à un joueur
/baltop [page]            - Classement des joueurs les plus riches
/withdraw <montant>       - Retirer de l'argent en billet physique
```

#### Commandes Admin
```
/eco give <joueur> <montant>  - Donner de l'argent
/eco take <joueur> <montant>  - Retirer de l'argent
/eco set <joueur> <montant>   - Définir le solde
/eco reset <joueur>           - Réinitialiser le solde
/eco reload                   - Recharger la configuration

/economyitems                 - Ouvrir le GUI de gestion des items économiques
```

### 🎯 Permissions

```yaml
economy.admin      - Accès aux commandes d'administration
economy.use        - Utiliser les commandes de base (défaut: true)
economy.withdraw   - Retirer de l'argent physique (défaut: true)
```

## 📦 Installation

1. **Télécharger les dépendances:**
   - Spigot/Paper 1.21.1+
   - (Optionnel) ItemsAdder 3.6.1+
   - (Optionnel) Oraxen 1.171.0+

2. **Installer le plugin:**
   - Placer `EconomyPlugin.jar` dans le dossier `plugins/`
   - Redémarrer le serveur

3. **Configuration:**
   - Éditer `plugins/EconomyPlugin/config.yml`
   - Personnaliser les messages, la monnaie, etc.

## ⚙️ Configuration

### Personnalisation de la Monnaie

```yaml
settings:
  starting-balance: 1000          # Solde de départ
  currency-symbol: "€"            # Symbole (€, $, etc.)
  currency-name: "Euro"           # Nom singulier
  currency-name-plural: "Euros"   # Nom pluriel
  max-balance: 1000000000        # Solde maximum
  min-balance: 0                  # Solde minimum
```

### Portefeuille Visuel

```yaml
wallet-display:
  enabled: true
  # Image dans le chat (ItemsAdder/Oraxen)
  chat-image: ":wallet:"
  
  # Icône du portefeuille
  wallet-icon: "itemsadder:custom:wallet"
  
  # Format d'affichage
  format:
    - "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━"
    - "&6&l⦿ PORTEFEUILLE"
    - "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━"
    - ""
    - "&7Joueur: &f{player}"
    - "&7Solde: &a{balance} {currency}"
    - ""
    - "&7Rang: &e#{rank} &8/ &e{total}"
    - ""
    - "&8&m━━━━━━━━━━━━━━━━━━━━━━━━━"
```

### Items Économiques (Exemples)

```yaml
economy-items:
  enabled: true
  items:
    "itemsadder:custom:gold_coin":
      value: 100              # Valeur de vente
      sellable: true          # Joueurs peuvent vendre
      buyable: true           # Joueurs peuvent acheter
      buy-price: 110          # Prix d'achat
    
    "itemsadder:custom:diamond_coin":
      value: 1000
      sellable: true
      buyable: true
      buy-price: 1100
    
    "oraxen:ruby":
      value: 500
      sellable: true
      buyable: false
```

## 🎨 Utilisation du GUI

### Ouvrir le GUI
```
/economyitems
```

### Ajouter un Item
1. Ouvrir le GUI avec `/economyitems`
2. Prendre un item personnalisé (ItemsAdder/Oraxen) dans votre inventaire
3. Le glisser dans un slot vide du GUI
4. Configurer la valeur et les options
5. Cliquer sur "Valider"

### Modifier un Item
1. Cliquer sur l'item dans le GUI
2. Modifier les paramètres
3. Cliquer sur "Valider"

### Supprimer un Item
1. Shift + Clic sur l'item dans le GUI

## 🖼️ Ajouter des Images (ItemsAdder/Oraxen)

### Avec ItemsAdder

1. **Créer l'image du portefeuille:**
```yaml
# plugins/ItemsAdder/data/items_packs/custom/wallet.yml
items:
  wallet:
    display_name: "Portefeuille"
    resource:
      material: PAPER
      generate: false
      textures:
        - custom/wallet.png
    specific_properties:
      font:
        unicode: ":wallet:"
```

2. **Ajouter l'image:**
   - Placer `wallet.png` dans `contents/custom/textures/`
   - Exécuter `/iazip`

3. **Utiliser dans le config.yml:**
```yaml
wallet-display:
  chat-image: ":wallet:"
  wallet-icon: "itemsadder:custom:wallet"
```

### Avec Oraxen

1. **Créer l'item dans Oraxen:**
```yaml
# plugins/Oraxen/items/wallet.yml
wallet:
  displayname: "Portefeuille"
  material: PAPER
  Pack:
    generate_model: true
    parent_model: "item/generated"
    textures:
      - wallet.png
```

2. **Ajouter l'image:**
   - Placer `wallet.png` dans le pack de textures
   - Recharger Oraxen

3. **Utiliser dans le config.yml:**
```yaml
wallet-display:
  wallet-icon: "oraxen:wallet"
```

## 🛠️ Compilation

### Prérequis
- Java 21
- Maven 3.8+

### Build
```bash
cd EconomyPlugin
mvn clean package
```

Le fichier JAR sera généré dans `target/EconomyPlugin-1.0.0.jar`

## 📊 Base de Données

Le plugin utilise SQLite par défaut. Les données sont stockées dans:
- `plugins/EconomyPlugin/data.yml` - Soldes des joueurs
- `plugins/EconomyPlugin/items.yml` - Valeurs des items

### Configuration MySQL (optionnel)

```yaml
database:
  type: MYSQL
  mysql:
    host: "localhost"
    port: 3306
    database: "economy"
    username: "root"
    password: "password"
```

## 🎯 Exemples d'Utilisation

### Créer un Système de Shop

1. Créer des items dans ItemsAdder/Oraxen
2. Les ajouter avec `/economyitems`
3. Configurer les prix d'achat/vente
4. Les joueurs peuvent vendre leurs items

### Système de Monnaie Physique

1. Configurer le retrait dans `config.yml`
2. Les joueurs utilisent `/withdraw <montant>`
3. Ils reçoivent un billet physique
4. Clic droit pour l'encaisser

### Économie de Serveur

1. Donner de l'argent de départ aux nouveaux joueurs
2. Utiliser `/eco give` pour récompenser
3. `/baltop` pour voir les plus riches
4. Créer des quêtes avec récompenses économiques

## 🐛 Support & Bugs

Pour tout problème ou suggestion:
1. Vérifier que vous utilisez la bonne version de Minecraft (1.21.1)
2. Vérifier que ItemsAdder/Oraxen est à jour
3. Consulter les logs du serveur

## 📝 Notes Importantes

- ✅ Compatible avec Paper, Spigot, Purpur
- ✅ Fonctionne sans ItemsAdder/Oraxen (mode vanilla)
- ✅ Sauvegarde automatique toutes les 5 minutes
- ✅ Support multilingue (messages personnalisables)
- ✅ Optimisé pour les performances

## 🎉 Crédits

Développé par **Maitre**
Version: **1.0.0**
Minecraft: **1.21.1**

---

**Bon jeu! 🎮**
