# AlchemicalCauldron [![Travis CI](https://secure.travis-ci.org/Indiv0/alchemical-cauldron.png)](http://travis-ci.org/#!/Indiv0/alchemical-cauldron)

AlchemicalCauldron is a plugin for Bukkit which is used for converting items thrown into a cauldron into other items (e.g. dirt into iron ore).
It is meant to encourage and enhance roleplay on servers by introducing an alchemy aspect into the game.


## Administrators

### Download

You can find various releases of the plugin at my [maven repository](http://maven.nikitapek.in/repository/internal/in/nikitapek/alchemical-cauldron/).
Alternatively, you can find downloads with detailed changelog information in the [files](http://dev.bukkit.org/bukkit-plugins/alchemicalcauldron/files/) section of the plugin's BukkitDev page.

### Installation

Simply drop the latest .jar into the /plugins directory of your server.

### Usage

Further plugin information can be found at the plugin's [BukkitDev](http://dev.bukkit.org/bukkit-plugins/alchemicalcauldron/) page.

## Developers

AlchemicalCauldron does not have a formal API at the moment, but you can download the latest version via maven by adding the following snippets to your plugin's pom.xml.

### Repository

    <repositories>
      <repository>
        <id>internal</id>
        <name>Indiv0's Repo</name>
        <url>http://maven.nikitapek.in/repository/internal/</url>
      </repository>
    </repositories>

### Dependency

    <dependency>
      <groupId>in.nikitapek</groupId>
      <artifactId>alchemical-cauldron</artifactId>
      <version>1.12.0</version>
    </dependency>
