# 🔨 Guide de Compilation - EconomyPlugin

## Prérequis

### Logiciels Nécessaires
- ☕ **Java 21** (OpenJDK ou Oracle JDK)
- 📦 **Maven 3.8+**
- 💻 Un terminal/cmd

### Vérifier les Installations

```bash
# Vérifier Java
java -version
# Doit afficher: openjdk version "21" ou java version "21"

# Vérifier Maven
mvn -version
# Doit afficher: Apache Maven 3.8.x ou supérieur
```

---

## 🚀 Compilation Simple

### Méthode 1: Compilation Rapide

```bash
# 1. Extraire le ZIP
unzip EconomyPlugin.zip
cd EconomyPlugin

# 2. Compiler
mvn clean package

# 3. Le fichier JAR est dans target/
# target/EconomyPlugin-1.0.0.jar
```

### Méthode 2: Sans Tests (Plus Rapide)

```bash
mvn clean package -DskipTests
```

---

## 📝 Étapes Détaillées

### Étape 1: Préparation
```bash
cd EconomyPlugin

# Nettoyer les anciennes compilations
mvn clean
```

### Étape 2: Téléchargement des Dépendances
```bash
# Maven va télécharger automatiquement:
# - Spigot API 1.21.1
# - ItemsAdder API
# - Oraxen API
mvn dependency:resolve
```

### Étape 3: Compilation
```bash
# Compile le code source
mvn compile
```

### Étape 4: Packaging
```bash
# Crée le fichier JAR final
mvn package

# Le JAR sera créé dans:
# target/EconomyPlugin-1.0.0.jar
```

---

## 🛠️ Résolution de Problèmes

### Problème: Java version incorrecte

**Erreur:**
```
[ERROR] Source option 21 is no longer supported
```

**Solution:**
```bash
# Installer Java 21
# Windows: https://adoptium.net/
# Linux: sudo apt install openjdk-21-jdk
# Mac: brew install openjdk@21

# Définir JAVA_HOME
export JAVA_HOME=/path/to/java-21
export PATH=$JAVA_HOME/bin:$PATH
```

### Problème: Maven non trouvé

**Erreur:**
```
mvn: command not found
```

**Solution:**
```bash
# Windows: Télécharger depuis https://maven.apache.org/
# Linux: sudo apt install maven
# Mac: brew install maven

# Ajouter Maven au PATH
export PATH=/path/to/maven/bin:$PATH
```

### Problème: Dépendances non trouvées

**Erreur:**
```
[ERROR] Failed to execute goal ... Could not resolve dependencies
```

**Solution:**
```bash
# Nettoyer le cache Maven
mvn dependency:purge-local-repository

# Forcer le téléchargement
mvn clean install -U
```

### Problème: Compilation échoue

**Erreur:**
```
[ERROR] Failed to execute goal ... compilation failure
```

**Solution:**
```bash
# Vérifier que tous les fichiers sont présents
ls src/main/java/fr/maitre/economy/

# Recompiler depuis zéro
mvn clean compile

# Si ça persiste, vérifier les logs Maven
mvn clean package -X
```

---

## 🎯 Personnalisation Avant Compilation

### Changer le Nom du Plugin

**Éditer:** `pom.xml`
```xml
<artifactId>MonNouveauPlugin</artifactId>
<name>MonNouveauPlugin</name>
```

**Éditer:** `src/main/resources/plugin.yml`
```yaml
name: MonNouveauPlugin
```

### Changer la Version

**Éditer:** `pom.xml`
```xml
<version>2.0.0</version>
```

### Ajouter des Dépendances

**Éditer:** `pom.xml`
```xml
<dependencies>
    <!-- Vos nouvelles dépendances ici -->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>exemple</artifactId>
        <version>1.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

---

## 🏗️ Utilisation d'un IDE

### IntelliJ IDEA

1. **Ouvrir le Projet**
   ```
   File > Open > Sélectionner le dossier EconomyPlugin
   ```

2. **Importer Maven**
   ```
   IntelliJ détecte automatiquement le pom.xml
   ```

3. **Compiler**
   ```
   View > Tool Windows > Maven
   Lifecycle > package
   ```

### Eclipse

1. **Ouvrir le Projet**
   ```
   File > Import > Existing Maven Projects
   ```

2. **Compiler**
   ```
   Clic droit sur le projet > Run As > Maven build
   Goals: clean package
   ```

### VS Code

1. **Installer Extension**
   ```
   Extension: Java Extension Pack
   Extension: Maven for Java
   ```

2. **Ouvrir le Projet**
   ```
   File > Open Folder > EconomyPlugin
   ```

3. **Compiler**
   ```
   Ctrl+Shift+P > Maven: Execute commands...
   clean package
   ```

---

## 📦 Après Compilation

### Fichiers Générés

```
target/
├── EconomyPlugin-1.0.0.jar          ← Le plugin final
├── original-EconomyPlugin-1.0.0.jar ← Version sans shade
└── maven-archiver/
```

### Installation du Plugin

```bash
# 1. Copier le JAR
cp target/EconomyPlugin-1.0.0.jar /path/to/server/plugins/

# 2. Redémarrer le serveur
# ou
# reload confirm (si vous utilisez PluginManager)
```

### Vérification

```bash
# Dans le serveur
/plugins
# Doit afficher: EconomyPlugin v1.0.0 (en vert)
```

---

## 🔄 Automatisation (Avancé)

### Script de Build Automatique

**build.sh (Linux/Mac):**
```bash
#!/bin/bash
echo "🔨 Compilation d'EconomyPlugin..."
mvn clean package

if [ $? -eq 0 ]; then
    echo "✅ Compilation réussie!"
    echo "📦 JAR: target/EconomyPlugin-1.0.0.jar"
else
    echo "❌ Erreur de compilation"
    exit 1
fi
```

**build.bat (Windows):**
```batch
@echo off
echo Compilation d'EconomyPlugin...
call mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo Compilation reussie!
    echo JAR: target\EconomyPlugin-1.0.0.jar
) else (
    echo Erreur de compilation
    exit /b 1
)
```

### GitHub Actions (CI/CD)

**Créer:** `.github/workflows/build.yml`
```yaml
name: Build Plugin

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
        distribution: 'adopt'
    
    - name: Build with Maven
      run: mvn clean package
    
    - name: Upload JAR
      uses: actions/upload-artifact@v2
      with:
        name: EconomyPlugin
        path: target/*.jar
```

---

## 📊 Profil Maven

### Profil de Développement

**Ajouter dans pom.xml:**
```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <maven.test.skip>true</maven.test.skip>
        </properties>
    </profile>
</profiles>
```

**Utiliser:**
```bash
mvn clean package -Pdev
```

---

## 💡 Astuces

### Compilation Plus Rapide
```bash
# Paralléliser la compilation
mvn clean package -T 4

# Utiliser le cache
mvn clean package -o (offline)
```

### Debug de la Compilation
```bash
# Mode verbose
mvn clean package -X

# Afficher l'arbre des dépendances
mvn dependency:tree
```

### Nettoyer Complètement
```bash
# Supprimer tous les fichiers générés
mvn clean

# Supprimer aussi le cache local
rm -rf ~/.m2/repository/fr/maitre
```

---

**Compilation réussie! 🎉**
