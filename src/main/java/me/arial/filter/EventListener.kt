package me.arial.filter

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.regex.Matcher
import java.util.regex.Pattern


class EventListener : Listener {

    init {
        Bukkit.getServer().pluginManager.registerEvents(this, Main.instance!!)

        val protocolManager = ProtocolLibrary.getProtocolManager()

        protocolManager.addPacketListener(
            object : PacketAdapter(
                Main.instance!!,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.CHAT
            ) {
                override fun onPacketSending(e: PacketEvent) {
                    if (!e.player.hasPermission("funfilter.view")) {
                        val container = e.packet
                        val newPacket = container.deepClone()

                        val chatComponent: WrappedChatComponent? = newPacket.chatComponents.read(0) ?: return
                        val message = parseMessage(
                            chatComponent!!.json,
                            Main.instance!!.blockedWords,
                            Main.instance!!.config.getString("toBlock")!!
                        )

                        chatComponent.json = message;
                        newPacket.chatComponents.write(0, chatComponent);

                        e.packet = newPacket
                    }
                }
            }
        )
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        if (!e.isAsynchronous) return

        val player = e.player

        if (Main.instance!!.config.getBoolean("symbolBlocker.enabled") && !player.hasPermission("funfilter.symbolblocker.bypass")) {
            val pattern: Pattern = Pattern.compile(
                Main.instance!!.config.getString("symbolBlocker.regex")!!
            )
            val matcher: Matcher = pattern.matcher(e.message)

            while (!matcher.find()) {
                player.sendMessage(
                    Color.parseString(
                        Main.instance!!.config.getString("symbolBlocker.message")!!
                    )
                )
                e.isCancelled = true
                return
            }
        }

//        if (!player.hasPermission("funfilter.blocker.bypass")) {
//            e.message = parseMessage(
//                e.message,
//                Main.instance!!.blockedWords,
//                Main.instance!!.config.getString("toBlock")!!
//            )
//        }
    }

    private fun parseMessage(message: String, words: List<String>, replace: String): String {
        var updatedMessage = message
        for (word in words) {
            val regex = Regex("\\b$word\\b", RegexOption.IGNORE_CASE)
            updatedMessage = regex.replace(updatedMessage) { matchResult ->
                matchResult.value.replaceRange(1, matchResult.value.length - 1, replace.repeat(matchResult.value.length - 2))
            }
        }
        return updatedMessage
    }

}