package me.pink.revises.utils.config

import me.pink.revises.Revises
import me.pink.revises.convertColor

object ReviseConfig {
    var defaultCheckTime = Revises.instance.getIntFromConfig("check.time.period")
    var endTimeMessage = Revises.instance.getStringFromMessages("endTime")
    var messageDelay = Revises.instance.getStringFromMessages("check.initial.period")
    var denyReviseBypass = Revises.instance.getStringFromMessages("check.deny")

    var teleportInCheckToggle = Revises.instance.getBoolFromConfig("check.teleport.toggle")
    var teleportInCheckTpToModer = Revises.instance.getBoolFromConfig("check.teleport.TpToModerator")
    var teleportInCheckTpToCoords = Revises.instance.getBoolFromConfig("check.teleport.tpToCoords")

    var coordsX = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.x")
    var coordsY = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.y")
    var coordsZ = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.z")
    var yaw = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.yaw")
    var pitch = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.pitch")
    var coordsWorldName = Revises.instance.getStringFromConfig("check.teleport.tpInCoords.world")

    var checkMessage = Revises.instance.getListFromMessages("check.initial.message")

    var titlesIsToggle =  Revises.instance.getBoolFromConfig("check.titles.toggle")
    var titlesTitle = Revises.instance.getStringFromConfig("check.titles.title")
    var titlesSubTitle = Revises.instance.getStringFromConfig("check.titles.subTitle")

    var actionBarIsToggle = Revises.instance.getBoolFromConfig("check.actionbar.toggle")
    var actionBarText = Revises.instance.getStringFromConfig("check.actionbar.text")

    var bossBarIsToggle = Revises.instance.getBoolFromConfig("check.bossbar.toggle")
    var bossBarSuspectText = Revises.instance.getStringFromConfig("check.bossbar.suspect.content")
    var bossBarTextOnStop = Revises.instance.getStringFromConfig("check.bossbar.suspect.stopTimeContent")
    var bossBarInspectorText = Revises.instance.getStringFromConfig("check.bossbar.inspector.content")
    var bossBarColor = Revises.instance.getStringFromConfig("check.bossbar.color")

    var commandsIsNotToggle = Revises.instance.getBoolFromConfig("check.commands.toggle")
    var commandsDenyMessage = Revises.instance.getStringFromConfig("check.commands.denyMessage")
    var allowedCommands = Revises.instance.getListFromConfig("check.commands.allowCommands")

    var effectsIsToggle = Revises.instance.getBoolFromConfig("check.effects.toggle")
    var effects = Revises.instance.getListFromConfig("check.effects.types")

    var chatEnable = Revises.instance.getBoolFromConfig("check.chat.enable")
    var chatOnStartMessage = Revises.instance.getStringFromConfig("check.chat.startMessage")

    var chatHoverIsToggle = Revises.instance.getBoolFromConfig("check.chat.hover.toggle")
    var chatCopyIsToggle = Revises.instance.getBoolFromConfig("check.chat.copying.toggle")

    var hoverText = Revises.instance.getStringFromConfig("check.chat.hover.text")

    var suspectMove = Revises.instance.getBoolFromConfig("check.suspect.move")
    var suspectInteraction = Revises.instance.getBoolFromConfig("check.suspect.interaction")
    var suspectDamage = Revises.instance.getBoolFromConfig("check.suspect.damage")
    var suspectInvInteraction = Revises.instance.getBoolFromConfig("check.suspect.inventory.interaction")
    var suspectInvDrop = Revises.instance.getBoolFromConfig("check.suspect.inventory.drop")
    var suspectInvClick = Revises.instance.getBoolFromConfig("check.suspect.inventory.click")
    var suspectInvMoveItem = Revises.instance.getBoolFromConfig("check.suspect.inventory.moveItem")
    var suspectInvPickup = Revises.instance.getBoolFromConfig("check.suspect.inventory.pickupItem")

    var chatSideModeratorYou = Revises.instance.getListFromMessages("chat.moderator.outgoing")
    var chatSideModeratorPlayer = Revises.instance.getListFromMessages("chat.moderator.incoming")

    var chatSidePlayerYou = Revises.instance.getListFromMessages("chat.suspect.incoming")
    var chatSidePlayerModerator = Revises.instance.getListFromMessages("chat.suspect.outgoing")


    var AFKSystemIsToggle = Revises.instance.getBoolFromConfig("system.afk.toggle")
    var considerAFK = Revises.instance.getIntFromConfig("system.afk.timeout")
    var messageOnAFK = Revises.instance.getStringFromConfig("system.afk.message")
    var onAFKLeft = Revises.instance.getStringFromConfig("system.afk.left.message")
    var runCheck = Revises.instance.getBoolFromConfig("system.afk.left.runCheck")


    var prizeToggle = Revises.instance.getBoolFromConfig("check.prize.toggle")
    var prizeItem = Revises.instance.getStringFromConfig("check.prize.item")
    var prizeItemHideFlags = Revises.instance.getBoolFromConfig("check.prize.item.hideFlags")
    var prizeItemName = Revises.instance.getStringFromConfig("check.prize.item.name")
    var prizeItemLore = Revises.instance.getListFromConfig("check.prize.item.lore")

    var loggingToggle = Revises.instance.getBoolFromConfig("logging.toggle")
    var loggingMessage = Revises.instance.getListFromConfig("logging.message")
    var loggingResultsBan = Revises.instance.getStringFromConfig("logging.results.ban")
    var loggingResultsVerified = Revises.instance.getStringFromConfig("logging.results.verified")
    var loggingResultsEndTime = Revises.instance.getStringFromConfig("logging.results.endTime")

    var additionalTimeMaximum = Revises.instance.getIntFromConfig("check.time.maximum")
    var additionalTimeMessageToModerator = Revises.instance.getStringFromMessages("check.time.messageToModerator")
    var additionalTimeMessageToPlayer = Revises.instance.getListFromMessages("check.time.messageToPlayer")
    var additionalTimeExceededMaximumTime = Revises.instance.getStringFromMessages("check.time.exceededMaximumTime")

    var systemAutoBan = Revises.instance.getBoolFromConfig("system.autoBan.toggle")
    var systemAutoBanCommand = Revises.instance.getStringFromConfig("system.autoBan.command")

    var systemExitMessage = Revises.instance.getStringFromConfig("system.autoBan.exitMessage")
    var systemExitReasonMessage = Revises.instance.getStringFromConfig("system.autoBan.exitReasonMessage")
    var systemStartCooldown = Revises.instance.getIntFromConfig("system.cooldowns.commands.start.period")
    var systemStartCooldownMessage = Revises.instance.getStringFromConfig("system.cooldowns.commands.start.message")
    var systemLogsCooldown = Revises.instance.getIntFromConfig("system.cooldowns.commands.logs.period")
    var systemLogsCooldownMessage = Revises.instance.getStringFromConfig("system.cooldowns.commands.logs.message")

    object Database {
        var databaseStrategy = Revises.instance.getStringFromConfig("database.strategy")
        var databaseHost = Revises.instance.getStringFromConfig("database.host")
        var databaseName = Revises.instance.getStringFromConfig("database.name")
        var databasePort = Revises.instance.getIntFromConfig("database.port")
        var databaseUsername = Revises.instance.getStringFromConfig("database.username")
        var databasePassword = Revises.instance.getStringFromConfig("database.password")
    }

    object Messages {
        var noPermissions = Revises.instance.getStringFromMessages("commands.noPermission").convertColor()
        var unknownSender = Revises.instance.getStringFromMessages("commands.unknownSender").convertColor()
        var wrongUsage = Revises.instance.getStringFromMessages("commands.wrongUsage").convertColor()
    }

    fun reload() {
        defaultCheckTime = Revises.instance.getIntFromConfig("check.time.period")
        endTimeMessage = Revises.instance.getStringFromMessages("endTime")
        messageDelay = Revises.instance.getStringFromMessages("check.initial.period")
        denyReviseBypass = Revises.instance.getStringFromMessages("check.deny")

        teleportInCheckToggle = Revises.instance.getBoolFromConfig("check.teleport.toggle")
        teleportInCheckTpToModer = Revises.instance.getBoolFromConfig("check.teleport.TpToModerator")
        teleportInCheckTpToCoords = Revises.instance.getBoolFromConfig("check.teleport.tpToCoords")

        coordsX = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.x")
        coordsY = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.y")
        coordsZ = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.coords.z")
        yaw = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.yaw")
        pitch = Revises.instance.getDoubleFromConfig("check.teleport.tpInCoords.pitch")
        coordsWorldName = Revises.instance.getStringFromConfig("check.teleport.tpInCoords.world")

        checkMessage = Revises.instance.getListFromMessages("check.initial.message")

        titlesIsToggle =  Revises.instance.getBoolFromConfig("check.titles.toggle")
        titlesTitle = Revises.instance.getStringFromConfig("check.titles.title")
        titlesSubTitle = Revises.instance.getStringFromConfig("check.titles.subTitle")

        actionBarIsToggle = Revises.instance.getBoolFromConfig("check.actionbar.toggle")
        actionBarText = Revises.instance.getStringFromConfig("check.actionbar.text")

        bossBarIsToggle = Revises.instance.getBoolFromConfig("check.bossbar.toggle")
        bossBarSuspectText = Revises.instance.getStringFromConfig("check.bossbar.suspect.content")
        bossBarTextOnStop = Revises.instance.getStringFromConfig("check.bossbar.suspect.stopTimeContent")
        bossBarInspectorText = Revises.instance.getStringFromConfig("check.bossbar.inspector.content")
        bossBarColor = Revises.instance.getStringFromConfig("check.bossbar.color")

        commandsIsNotToggle = Revises.instance.getBoolFromConfig("check.commands.toggle")
        commandsDenyMessage = Revises.instance.getStringFromConfig("check.commands.denyMessage")
        allowedCommands = Revises.instance.getListFromConfig("check.commands.allowCommands")

        effectsIsToggle = Revises.instance.getBoolFromConfig("check.effects.toggle")
        effects = Revises.instance.getListFromConfig("check.effects.types")

        chatEnable = Revises.instance.getBoolFromConfig("check.chat.enable")
        chatOnStartMessage = Revises.instance.getStringFromConfig("check.chat.startMessage")

        chatHoverIsToggle = Revises.instance.getBoolFromConfig("check.chat.hover.toggle")
        chatCopyIsToggle = Revises.instance.getBoolFromConfig("check.chat.copying.toggle")

        hoverText = Revises.instance.getStringFromConfig("check.chat.hover.text")

        suspectMove = Revises.instance.getBoolFromConfig("check.suspect.move")
        suspectInteraction = Revises.instance.getBoolFromConfig("check.suspect.interaction")
        suspectDamage = Revises.instance.getBoolFromConfig("check.suspect.damage")
        suspectInvInteraction = Revises.instance.getBoolFromConfig("check.suspect.inventory.interaction")
        suspectInvClick = Revises.instance.getBoolFromConfig("check.suspect.inventory.click")
        suspectInvMoveItem = Revises.instance.getBoolFromConfig("check.suspect.inventory.moveItem")
        suspectInvPickup = Revises.instance.getBoolFromConfig("check.suspect.inventory.pickupItem")
        suspectInvDrop = Revises.instance.getBoolFromConfig("check.suspect.inventory.drop")

        chatSideModeratorYou = Revises.instance.getListFromMessages("chat.moderator.outgoing")
        chatSideModeratorPlayer = Revises.instance.getListFromMessages("chat.moderator.incoming")

        chatSidePlayerYou = Revises.instance.getListFromMessages("chat.suspect.incoming")
        chatSidePlayerModerator = Revises.instance.getListFromMessages("chat.suspect.outgoing")


        AFKSystemIsToggle = Revises.instance.getBoolFromConfig("system.afk.toggle")
        considerAFK = Revises.instance.getIntFromConfig("system.afk.timeout")
        messageOnAFK = Revises.instance.getStringFromConfig("system.afk.message")
        onAFKLeft = Revises.instance.getStringFromConfig("system.afk.left.message")
        runCheck = Revises.instance.getBoolFromConfig("system.afk.left.runCheck")


        prizeToggle = Revises.instance.getBoolFromConfig("check.prize.toggle")
        prizeItem = Revises.instance.getStringFromConfig("check.prize.item")
        prizeItemHideFlags = Revises.instance.getBoolFromConfig("check.prize.item.hideFlags")
        prizeItemName = Revises.instance.getStringFromConfig("check.prize.item.name")
        prizeItemLore = Revises.instance.getListFromConfig("check.prize.item.lore")

        loggingToggle = Revises.instance.getBoolFromConfig("logging.toggle")
        loggingMessage = Revises.instance.getListFromConfig("logging.message")
        loggingResultsBan = Revises.instance.getStringFromConfig("logging.results.ban")
        loggingResultsVerified = Revises.instance.getStringFromConfig("logging.results.verified")
        loggingResultsEndTime = Revises.instance.getStringFromConfig("logging.results.endTime")

        additionalTimeMaximum = Revises.instance.getIntFromConfig("check.time.maximum")
        additionalTimeMessageToModerator = Revises.instance.getStringFromMessages("check.time.messageToModerator")
        additionalTimeMessageToPlayer = Revises.instance.getListFromMessages("check.time.messageToPlayer")
        additionalTimeExceededMaximumTime = Revises.instance.getStringFromMessages("check.time.exceededMaximumTime")

        systemAutoBan = Revises.instance.getBoolFromConfig("system.autoBan.toggle")
        systemAutoBanCommand = Revises.instance.getStringFromConfig("system.autoBan.command")

        systemExitMessage = Revises.instance.getStringFromConfig("system.autoBan.exitMessage")
        systemExitReasonMessage = Revises.instance.getStringFromConfig("system.autoBan.exitReasonMessage")
        systemStartCooldown = Revises.instance.getIntFromConfig("system.cooldowns.commands.start.period")
        systemStartCooldownMessage = Revises.instance.getStringFromConfig("system.cooldowns.commands.start.message")
        systemLogsCooldown = Revises.instance.getIntFromConfig("system.cooldowns.commands.logs.period")
        systemLogsCooldownMessage = Revises.instance.getStringFromConfig("system.cooldowns.commands.logs.message")

        Messages.noPermissions = Revises.instance.getStringFromMessages("commands.noPermission").convertColor()
        Messages.unknownSender = Revises.instance.getStringFromMessages("commands.unknownSender").convertColor()
        Messages.wrongUsage = Revises.instance.getStringFromMessages("commands.wrongUsage").convertColor()
    }

}
