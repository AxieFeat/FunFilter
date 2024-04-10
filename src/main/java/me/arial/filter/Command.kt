package me.arial.filter

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class Command : TabExecutor {
    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>?
    ): MutableList<String> {
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (!sender.hasPermission("funfilter.admin")) {
            return true
        }

        Main.instance!!.reloadConfig()
        Main.instance!!.load()

        sender.sendMessage("Плагин перезагружен!")
        return true
    }
}