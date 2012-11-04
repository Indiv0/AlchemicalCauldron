Description
  AlchemicalCauldron is a plugin for bukkit which is used for converting items
  thrown into a cauldron into other items (e.g. dirt into iron ore).

Permission
  NONE
Commands
  NONE

Usage
  Simply run the plugin, edit the "config.yml" that is created by the plugin in order to
  adjust the item input/output ratios in order to suit your needs.
  Reload the server in order to update the config.
  Throw items towards a cauldron in order to convert them into other items.
  
  Ratios:
    Items can be input through their name or their item ID.
    Input item ratios (0.0-1.0) control how often an item is converted (e.g. 0.46 for dirt will
    convert it 46% of the time).
    Output item ratios use a slightly more complicated system, but in general the higher the number (0.0-1.0),
    the greater the chance of that item being created.

Source
  https://github.com/Indiv0/AlchemicalCauldron
Download
  https://github.com/downloads/Indiv0/AlchemicalCauldron/AlchemicalCauldron.jar

Changelog
  1.0
    Added base functionality
    Added ratio support
    Added configuration file support/ratio storage

Verified compatibility
  1.0
    CB 1.3.2 RB-1.0

Bugs
  None known.

ToDo
  Make process more seamless (i.e. allow items to fall for a short period of time before conversion)
  Add variable distance from cauldron control (currently hard-coded to 1 block away)
  Add more specific item/cauldron collision checking (AFAIK not possible with current events,
    unless using a complicated and resource intensive HashMap/Coordinate system)