CFPromote
=========

CFPromote is a very simple Bukkit plugin that allows servers to have
interactive signs that let players get promoted to a specific permission group.

This plugin is also intended for newbie Bukkit plugin developers to learn from.


Features
--------

* Players can click on promotion signs to get promoted to a higher rank
* Promotion labels on signs can occupy any line, and the rest can be decorative!
* No commands necessary
* Very simple configuration
* Minimal permissions setup needed


Configuration
-------------

The plugin can be configured by editing config.yml in its data folder
(typically plugins/CFPromote/config.yml).

When the plugin runs for the first time, it will save its default configuration 
to the data folder.

| Key        | Description                                                                        | Default     |
| ---------- | ---------------------------------------------------------------------------------- | ----------- |
| sign-label | The exact line of text to look for on a sign to identify it as a promotion sign.   | '[Promote]' |
| group-name | The name of the permission group to assign players that click on a promotion sign. | ''          |

**You must specify a value for group-name, otherwise the plugin won't start!**


Permission Nodes
----------------

| Node                  | Description                                                                                 | Default  |
| --------------------- | ------------------------------------------------------------------------------------------- | -------- |
| cfpromote.getpromoted | Players with this permission node will get promoted when interacting with a promotion sign. | Nobody   |
| cfpromote.placesign   | This permission node allows server staff to place down a promotion sign.                    | Only OPs |


Compiling
---------

This project uses [Maven](http://maven.apache.org/) to handle dependencies.

CFPromote has the following Maven dependencies:

* [Bukkit](https://github.com/Bukkit/Bukkit)
* [Vault](https://github.com/MilkBowl/Vault)

Simply run `mvn clean install` on the project root to build the plugin.


Licence
-------

This project is licensed under a MIT/X11 licence. Please see [LICENCE.txt][]
for more information.

