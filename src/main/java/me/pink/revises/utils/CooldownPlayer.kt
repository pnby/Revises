package me.pink.revises.utils

import org.bukkit.entity.Player

data class CooldownPlayer (
    val player: Player,
    val lowerCaseNickName: String = player.name.lowercase(),
    val cooldownCommand: String,
    var remainingTime: Long
)