package me.arial.filter

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    companion object {
        var instance: Main? = null
    }

    val blockedWords: MutableList<String> = mutableListOf()

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        EventListener()
        getCommand("funfilter")!!.setExecutor(Command())
        getCommand("funfilter")!!.tabCompleter = Command()

        load()
    }

    fun load() {
        blockedWords.clear()

        config.getStringList("blockedWords").forEach {
            blockedWords.add(it)
        }
    }
}