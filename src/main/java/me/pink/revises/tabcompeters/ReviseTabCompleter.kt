package me.pink.revises.tabcompeters

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import me.pink.revises.Revises
import me.pink.revises.utils.config.Configurations.Companion.parseReasons


class ReviseTabCompleter : TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (command.name.equals("revise", ignoreCase = true)) {
            when (args.size) {
                1 -> {
                    return listOf("start", "stoptime", "addtime", "finish", "logs", "stop", "reload").filter { it.startsWith(args[0], ignoreCase = true) }
                }
                2 -> {
                    if (args[0].equals("start", ignoreCase = true) || args[0].equals("logs", ignoreCase = true)) {
                        return Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
                    } else if (args[0].equals("finish", ignoreCase = true)) {
                        val reasonMap = parseReasons(Revises.instance.reasonsFile)

                        val suggestions = reasonMap.values.flatten().distinct()
                        return suggestions.filter { it.startsWith(args[1], ignoreCase = true) }
                    }
                }
                3 -> {
                    if (args[0].equals("addtime", ignoreCase = true)) {
                        return listOf("10m", "20m", "30m", "60m").filter { it.startsWith(args[2], ignoreCase = true) }
                    }
                }
            }
        }
        return emptyList()
    }
}