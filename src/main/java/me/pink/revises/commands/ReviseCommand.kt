package me.pink.revises.commands

import me.pink.revises.Revises
import me.pink.revises.convertColor
import me.pink.revises.database.models.Check
import me.pink.revises.database.repositories.CheckRepository
import me.pink.revises.managers.AFKTrackManager
import me.pink.revises.managers.CheckManager
import me.pink.revises.managers.CooldownManager
import me.pink.revises.utils.RevisePermissions
import me.pink.revises.utils.config.Configurations.Companion.parseReasons
import me.pink.revises.utils.config.ReviseConfig
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.time.LocalDateTime
import java.util.*


class ReviseCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("Использование: /revise <start|stoptime|addtime|finish|logs|stop>")
            return true
        }

        when (args[0].lowercase()) {
            "start" -> handleStartCommand(sender, args)
            "stoptime" -> handleStopTimeCommand(sender)
            "addtime" -> handleAddTimeCommand(sender, args)
            "finish" -> handleFinishCommand(sender, args)
            "logs" -> handleLogsCommand(sender, args)
            "stop" -> handleStopCommand(sender, args)
            "reload" -> handleReloadCommand(sender)
            else -> sender.sendMessage("Неизвестная подкоманда. Использование: /revise <start|stoptime|addtime|finish|logs|stop>")
        }
        return true
    }

    private fun handleStartCommand(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission(RevisePermissions.START)) {
            sender.sendMessage(ReviseConfig.Messages.noPermissions)
            return
        }
        if (args.size < 2) {
            sender.sendMessage("Использование: /revise start <игрок>")
            return
        }
        val playerName = args[1]
        val player = Bukkit.getPlayer(playerName)

        if (sender == player) {
            return
        }

        if (player != null && sender is Player) {
            if (player.hasPermission(RevisePermissions.BYPASS_CHECK)) {
                sender.sendMessage(ReviseConfig.denyReviseBypass.convertColor())
                return
            }

            if (AFKTrackManager.inAFKPlayers(player.uniqueId)) {
                handleAFKSystem(sender, player)
                return
            }

            if (CheckManager.isUnderCheck(sender)) {
                sender.sendMessage("Вы уже проверяете игрока")
                return
            }

            if (!sender.hasPermission(RevisePermissions.BYPASS_COOLDOWN)) {
                val cooldownTime = ReviseConfig.systemStartCooldown
                if (CooldownManager.isOnCooldown(sender, "start")) {
                    val remainingTime = CooldownManager.getRemainingTime(sender, "start")
                    sender.sendMessage(ReviseConfig.systemStartCooldownMessage.convertColor().replace("%time%", remainingTime.toString()))
                    return
                }
                CooldownManager.setCooldown(sender, "start", cooldownTime)
            }

            sender.sendMessage(ReviseConfig.chatOnStartMessage.convertColor().replace("%suspect%", playerName))

            val checkId = initialCheckLogging(player.name, sender.name)
            CheckManager.startCheck(player, sender, ReviseConfig.defaultCheckTime.toLong() * 1000, checkId)
        } else {
            sender.sendMessage("Игрок $playerName не найден.")
        }
    }

    private fun handleAFKSystem(sender: Player, player: Player) {
        if (ReviseConfig.AFKSystemIsToggle) {
            if (AFKTrackManager.inAFKPlayers(player.uniqueId)) {
                sender.sendMessage(ReviseConfig.messageOnAFK.replace("%player%", player.name).convertColor())
                object : BukkitRunnable() {
                    override fun run() {
                        if (!AFKTrackManager.inAFKPlayers(player.uniqueId)) {
                            if (sender.isOnline) {
                                if (ReviseConfig.runCheck) {
                                    sender.sendMessage(
                                        ReviseConfig.onAFKLeft.replace("%player%", player.name).convertColor()
                                    )
                                    val checkId = initialCheckLogging(player.name, sender.name)
                                    CheckManager.startCheck(
                                        player,
                                        sender,
                                        ReviseConfig.defaultCheckTime.toLong() * 1000,
                                        checkId
                                    )
                                    cancel()
                                }
                            }
                        }
                    }
                }.runTaskTimer(Revises.instance, 0L, 22L)
            }
        }
    }

    private fun handleStopTimeCommand(sender: CommandSender) {
        if (sender !is Player) {
            sender.sendMessage(ReviseConfig.Messages.unknownSender)
            return
        }

        val session = CheckManager.getCheckSession(sender)
        if (session == null) {
            sender.sendMessage(ReviseConfig.Messages.wrongUsage)
            return
        }
        CheckManager.stopTime(session)

        sender.sendMessage("Время остановлено.")
    }

    private fun handleAddTimeCommand(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) {
            sender.sendMessage(ReviseConfig.Messages.unknownSender)
            return
        }

        if (!sender.hasPermission(RevisePermissions.ADD_TIME)) {
            sender.sendMessage(ReviseConfig.Messages.noPermissions)
            return
        }
        if (args.size < 2) {
            sender.sendMessage("Использование: /revise addtime <время>")
            return
        }
        val timeInput = args[1]
        val timeInMinutes = parseTime(timeInput)

        if (timeInMinutes == null) {
            sender.sendMessage("Указано некорректное время.")
            return
        }

        val maxTimeInMinutes = ReviseConfig.additionalTimeMaximum / 60
        val maxTimeInHours = maxTimeInMinutes / 60
        if (timeInMinutes > maxTimeInMinutes) {
            sender.sendMessage(
                ReviseConfig.additionalTimeExceededMaximumTime.convertColor()
                    .replace("%max_time%", maxTimeInHours.toString())
            )
            return
        }

        val session = CheckManager.getCheckSession(sender)

        if (session == null) {
            sender.sendMessage(ReviseConfig.Messages.wrongUsage)
            return
        }

        val timeIsAdded = CheckManager.addTime(session.suspect, sender, timeInMinutes * 60)
        if (timeIsAdded) {
            sender.sendMessage(
                ReviseConfig.additionalTimeMessageToModerator.convertColor()
                    .replace("%time%", timeInMinutes.toString())
            )

            ReviseConfig.additionalTimeMessageToPlayer.forEach {
                session.suspect.sendMessage(it.toString().convertColor().replace("%time%", timeInMinutes.toString()))
            }

        } else {
            sender.sendMessage(
                ReviseConfig.additionalTimeExceededMaximumTime
                    .convertColor()
                    .replace("%max_time%", maxTimeInHours.toString())
            ) // Часы
        }
    }

    private fun parseTime(timeInput: String): Int? {
        val regex = """(\d+)([hm])""".toRegex()
        val matchResult = regex.matchEntire(timeInput) ?: return null

        val (amount, unit) = matchResult.destructured
        val time = amount.toIntOrNull() ?: return null

        return when (unit) {
            "m" -> time
            else -> null
        }
    }

    private fun handleFinishCommand(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission(RevisePermissions.START)) {
            sender.sendMessage(ReviseConfig.Messages.noPermissions)
            return
        }

        if (sender !is Player) {
            sender.sendMessage(ReviseConfig.Messages.unknownSender)
            return
        }

        if (args.size < 2) {
            sender.sendMessage("Использование: /revise finish <причина>")
            return
        }

        val reasonKey = args[1]
        val reasonMap = parseReasons(Revises.instance.reasonsFile)

        val checkSession = CheckManager.getCheckSession(sender)

        if (checkSession == null) {
            sender.sendMessage("Вы не находитесь на проверке")
            return
        }

        val pushCmd = reasonMap.keys.firstOrNull { reasonMap[it]!!.contains(reasonKey) }

        if (pushCmd == null) {
            sender.sendMessage("Причина '$reasonKey' не найдена.")
            return
        }

        CheckManager.disapproveSuspect(checkSession, pushCmd.replace("%suspect%", checkSession.suspect.name), "guilty")
    }

    private fun handleLogsCommand(sender: CommandSender, args: Array<out String>) {
        if (!sender.hasPermission(RevisePermissions.CHECK_LOGS)) {
            sender.sendMessage("У вас нет прав для использования этой команды.")
            return
        }
        if (args.size < 3) {
            sender.sendMessage("Использование: /revise logs <игрок/модератор> <Страница>")
            return
        }
        val playerName = args[1]
        val page = args[2].toIntOrNull() ?: 1

        if (sender is Player) {
            val cooldownTime = ReviseConfig.systemLogsCooldown
            if (!sender.hasPermission(RevisePermissions.BYPASS_COOLDOWN)) {
                if (CooldownManager.isOnCooldown(sender, "logs")) {
                    val remainingTime = CooldownManager.getRemainingTime(sender, "logs")
                    sender.sendMessage(
                        ReviseConfig.systemLogsCooldownMessage.convertColor()
                            .replace("%time%", remainingTime.toString())
                    )
                    return
                }
                CooldownManager.setCooldown(sender, "logs", cooldownTime)
            }
        }

        sender.sendMessage("Получение записей по никнейму $playerName...")
        val checks = CheckRepository.getChecksByMemberName(playerName)
        sender.sendMessage("Найдено ${checks?.size} записей")
        if (checks != null) {
            displayPage(sender, checks, page)
        } else {
            sender.sendMessage("Записи не найдены.")
        }
    }

    private fun displayPage(sender: CommandSender, checks: List<Check>, page: Int, pageSize: Int = 5) {
        val totalPages = (checks.size + pageSize - 1) / pageSize
        if (page > totalPages || page < 1) {
            sender.sendMessage("Неверная страница. Доступные страницы: 1-$totalPages")
            return
        }

        val start = (page - 1) * pageSize
        val end = (start + pageSize).coerceAtMost(checks.size)
        val pageChecks = checks.subList(start, end)

        sender.sendMessage("Страница $page из $totalPages:")
        for (check in pageChecks) {
            for (line in ReviseConfig.loggingMessage) {
                val formattedMessage = formatLogMessage(line.toString(), check)
                sender.sendMessage(formattedMessage)
            }
        }

        if (page > 1) {
            sender.sendMessage("Для предыдущей страницы используйте /revise logs ${sender.name} ${page - 1}")
        }
        if (page < totalPages) {
            sender.sendMessage("Для следующей страницы используйте /revise logs ${sender.name} ${page + 1}")
        }
    }

    private fun formatLogMessage(template: String, check: Check): String {
        return template
            .replace("%moderator%", check.inspectorName.toString())
            .replace("%suspect%", check.suspectName.toString())
            .replace("%date%", "Нп - ${check.startCheckTime} Кп - ${check.stopCheckTime}")
            .replace("%result%", check.result.toString())
            .convertColor()
    }

    private fun handleStopCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size == 1 && sender is Player) {
            val checkSession = CheckManager.getCheckSession(sender)

            if (checkSession == null) {
                sender.sendMessage(ReviseConfig.Messages.wrongUsage)
                return
            }

            CheckManager.approveSuspect(checkSession)
        }
    }

    private fun initialCheckLogging(suspectName: String, inspectorName: String): UUID {
        val check = Check(
            uid = UUID.randomUUID(),
            suspectName = suspectName,
            inspectorName = inspectorName,
            startCheckTime = LocalDateTime.now()
        )
        return CheckRepository.addCheck(check)
    }

    private fun handleReloadCommand(sender: CommandSender) {
        if (sender is Player && sender.hasPermission(RevisePermissions.RELOAD)) {
            Revises.instance.configurations.reload()
            sender.sendMessage("&a[Revise] &bКонфигурация перезагружена".convertColor())
            Revises.instance.server.consoleSender.sendMessage("&a[Revise] &bКонфигурация перезагружена".convertColor())
        } else if (sender is ConsoleCommandSender) {
            Revises.instance.configurations.reload()
            Revises.instance.server.consoleSender.sendMessage("&a[Revise] &bКонфигурация перезагружена".convertColor())
        } else {
            sender.sendMessage(ReviseConfig.Messages.noPermissions.convertColor())
        }
    }
}