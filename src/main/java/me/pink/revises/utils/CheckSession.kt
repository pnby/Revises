package me.pink.revises.utils

import org.bukkit.entity.Player
import java.util.*

data class CheckSession(
    val uid: UUID,
    val suspect: Player,
    val inspector: Player,
    var remainingTimeMillis: Long,
    var addedTimeSecs: Int,
    var timeStopped: Boolean = false,
)