package me.pink.revises.utils

import org.bukkit.permissions.Permission

object RevisePermissions {
    val START = Permission("revise.start")
    val BYPASS_CHECK = Permission("revise.bypass.check")
    val ADD_TIME = Permission("revise.addtime")
    val BYPASS_ADD_TIME = Permission("revise.bypass.addtime")
    val BYPASS_COOLDOWN = Permission("revise.bypass.cooldown")
    val CHECK_LOGS = Permission("revise.checklogs")
    val RELOAD = Permission("revise.reload")
}