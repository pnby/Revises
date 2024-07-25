package me.pink.revises.utils.config

import me.pink.revises.Revises
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.*

class Configurations(private val plugin: Plugin) {
    private val configurations: MutableMap<ConfigurationType, Pair<FileConfiguration, File>> =
        EnumMap(ConfigurationType::class.java)

    private fun generateDefaultFile(name: String): File {
        val dataFolder = plugin.dataFolder
        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        val file = File(dataFolder, name)

        if (!file.exists()) {

            val resource = plugin.javaClass.classLoader.getResource(name)
            if (resource != null) {
                try {
                    plugin.saveResource(name, true)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            try {
                file.createNewFile()

                val configFile = YamlConfiguration.loadConfiguration(file)

                configFile.options().header("""
  ____            _               
 |  _ \ _____   _(_)___  ___  ___ 
 | |_) / _ \ \ / / / __|/ _ \/ __|
 |  _ <  __/\ V /| \__ \  __/\__ \
 |_| \_\___| \_/ |_|___/\___||___/

revise.start - право на команды: "/revise start", "/revise stop", "/revise finish"
revise.bypass.check - право на защиту от вызова на проверку
revise.addtime - право на команду "/revise addtime"
revise.bypass.addtime - право обходить лимит времени
revise.checklogs - право на команду /revise logs
revise.* - полные права от плагина

Разработчик: https://t.me/bytesend
                """
                )

                when (name) {
                    ConfigurationType.CONFIG.fileName -> {
                        configFile.set("check.time.period", 480)
                        configFile.set("check.time.maximum", 3600)

                        configFile.set("check.teleport.toggle", true)
                        configFile.set("check.teleport.TpToModerator", false)
                        configFile.set("check.teleport.tpToCoords", false)


                        configFile.set("check.teleport.tpInCoords.coords.x", -31.5)
                        configFile.set("check.teleport.tpInCoords.coords.y", 76.0)
                        configFile.set("check.teleport.tpInCoords.coords.z", 22.5)

                        configFile.set("check.teleport.tpInCoords.world", "world")
                        configFile.set("check.teleport.tpInCoords.yaw", 0.0)
                        configFile.set("check.teleport.tpInCoords.pitch", 0.0)


                        configFile.set("check.suspect.move", false)
                        configFile.set("check.suspect.interaction", false)
                        configFile.set("check.suspect.damage", false)
                        configFile.set("check.suspect.inventory.interaction", false)
                        configFile.set("check.suspect.inventory.drop", false)
                        configFile.set("check.suspect.inventory.click", false)
                        configFile.set("check.suspect.inventory.moveitem", false)
                        configFile.set("check.suspect.inventory.pickupItem", false)



                        configFile.set("check.titles.toggle", false)
                        configFile.set("check.titles.title", "")
                        configFile.set("check.titles.subTitle", "")


                        configFile.set("check.actionbar.toggle", false)
                        configFile.set("check.actionbar.text", "")


                        configFile.set("check.bossbar.toggle", true)
                        configFile.set("check.bossbar.suspect.content", "&eУ вас осталось времени: &6%time%")
                        configFile.set("check.bossbar.suspect.stopTimeContent", "&cВы находитесь на проверки")
                        configFile.set("check.bossbar.inspector.content", "&eДо конца проверки осталось &6%time%")
                        configFile.set("check.bossbar.color", "RED")




                        configFile.set("check.commands.toggle", false)
                        configFile.set("check.commands.denyMessage", "&c> &fКоманды во время проверки отключены")
                        configFile.set("check.commands.allowCommands", listOf("reload"))



                        configFile.set("check.effects.toggle", false)
                        configFile.set(
                            "check.effects.types", listOf(
                                "BLINDNESS"
                            )
                        )

                        configFile.set("check.chat.startMessage", "&fВы начали проверять игрока &9%suspect%")


                        configFile.set("check.chat.copying.toggle", false)
                        configFile.set("check.chat.hover.toggle", false)

                        configFile.set("check.chat.hover.text", "&fНажми, чтобы скопировать сообщение")


                        configFile.set("check.prize.toggle", true)
                        configFile.set("check.prize.item", "NETHER_STAR")
                        configFile.set("check.prize.item.hideFlags", true)
                        configFile.set("check.prize.item.name", "&c[★] &eТрофей &6%player%")
                        configFile.set(
                            "check.prize.item.lore", listOf(
                                "&c╔ &fПолучен от &cАдминистрации&f",
                                "&c╠ &fза честную игру на &6",
                                "&c╠ &fВедь после проверкки на читы",
                                "&c╠ &fВедь после проверкки на читы",
                                "&c╠ ",
                                "&c╠ &fНик: &6%player%",
                                "&c╚ &fДата: &6%date%, %time%",
                            )
                        )


                        configFile.set("logging.toggle", true)
                        configFile.set(
                            "logging.message", listOf(
                                "&c╔ &fПроверяющий: &6%moderator%",
                                "&c╠ &fЧитер: &c%suspect%",
                                "&c╠ &fДата: &e%date%",
                                "&c╚ &fИсход - %result%",
                            )
                        )
                        configFile.set("logging.results.ban", "&c&lОбнаружены читы")
                        configFile.set("logging.results.verified", "&aПроверка пройдена")
                        configFile.set("logging.results.endTime", "&eЗабанен за бездействие")


                        configFile.set("system.autoBan.toggle", true)
                        configFile.set("system.afk.toggle", true)
                        configFile.set("system.afk.timeout", 900)
                        configFile.set(
                            "system.afk.message",
                            "&fИгрок &9%player% &fAFK, вы будите оповещены когда он выйдет с AFK"
                        )
                        configFile.set(
                            "system.afk.left.message",
                            "&fИгрок &9%player%&f вышел из AFK и выл бызван на проверку автоматически"
                        )
                        configFile.set("system.afk.left.runCheck", true)


                        configFile.set("system.autoBan.command", "ban %suspect% Пункт 4.3 -s")

                        configFile.set("system.autoBan.exitMessage", "&c> &fИгрок вышел во время проверки!")
                        configFile.set("system.autoBan.exitReasonMessage", "&fПричина выхода: %exit_reason%")
                        configFile.set("system.cooldowns.commands.start.period", -1)
                        configFile.set("system.cooldowns.commands.start.message", "У вас кулдаун еще %time% секунд")
                        configFile.set("system.cooldowns.commands.logs.period", -1)
                        configFile.set("system.cooldowns.commands.logs.message", "У вас кулдаун еще %time% секунд")

                        configFile.set("database.strategy", "SQLITE") // SQLITE, MYSQL, POSTGRESQL
                        configFile.set("database.host", "localhost")
                        configFile.set("database.name", "postgres")
                        configFile.set("database.port", 5432)
                        configFile.set("database.username", "postgres")
                        configFile.set("database.password", "postgres")

                    }
                    ConfigurationType.MESSAGES.fileName -> {
                        configFile.set("commands.noPermission", "У вас нет прав для использования этой команды.")
                        configFile.set("commands.unknownSender", "Только игроки могут использовать эту команду.")
                        configFile.set("commands.wrongUsage", "Вы не находитесь на проверке")

                        configFile.set(
                            "endTime",
                            "&aТаймер закончился&f. Игрок &9%suspect%&f был забанен автоматически."
                        )

                        configFile.set(
                            "chat.moderator.outgoing", arrayListOf(
                                "&r ",
                                "&e[&6Вы &7» &6%suspect%&e] &7» &c%text%",
                                "&r "
                            )
                        )
                        configFile.set(
                            "chat.moderator.incoming", arrayListOf(
                                "&r ",
                                "&e[&6%suspect% &7» &6Вы&e] &7» &c%text%",
                                "&r "
                            )
                        )

                        configFile.set(
                            "chat.suspect.outgoing", arrayListOf(
                                "&r ",
                                "&e[&6Вы &7» &6%moderator%&e] &7» &c%text%",
                                "&r "
                            )
                        )

                        configFile.set(
                            "chat.suspect.incoming", arrayListOf(
                                "&r ",
                                "&e[&6%moderator% &7» &6Вы&e] &7» &c%text%",
                                "&r "
                            )
                        )

                        configFile.set(
                            "check.initial.message", arrayListOf(
                                "&r ",
                                "&r ",
                                "&c╔═══════════════════════════",
                                "&c╠ Вы были вызваны на проверку читов!",
                                "&c╠",
                                "&c╠ &6Выход из игры, игнор проверки - приведёт к &cбану",
                                "&c╠ &6Напишите '&eУ меня чит&6' - если используйете читы",
                                "&c╠ &7&o(за признание в читах - уменьшим срок бана)",
                                "&c╠",
                                "&c╠ &6Проверка выполняется через &9Discord&e:",
                                "&c╠ &6Войдите &9https://nometa.xyz/",
                                "&c╚═══════════════════════════"
                            )
                        )
                        configFile.set("check.initial.period", "10")
                        configFile.set("check.deny", "&c> &fВы не можете вызвать этого игрока на проверку!")
                        configFile.set(
                            "check.time.messageToModerator",
                            "&c> &fВы добавили время для проверки на &6%time% минут"
                        )
                        configFile.set(
                            "check.time.messageToPlayer", listOf(
                                "&r ",
                                "&c> &fВам добавили время проверки на &6%time%",
                                "&r ",
                            )
                        )
                        configFile.set(
                            "check.time.exceededMaximumTime",
                            "&c> &fМакисмально время для добавление на проверку это &6%max_time% час"
                        )
                    }
                    ConfigurationType.REASONS.fileName -> {
                        val reasons = mapOf(
                            "Nursultan" to mapOf(
                                "aliases" to listOf("нурсултан", "нурик", "nurik"),
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            ),
                            "Прочее(навсегда)" to mapOf(
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            ),
                            "Прочее(14 дней)" to mapOf(
                                "finishCommand" to "ban %suspect% 14d Пункт 4.3 -s"
                            ),
                            "exeInject" to mapOf(
                                "aliases" to listOf("Cortex", "кортекс", "DoomsDay", "думсдей"),
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            ),
                            "dllInject" to mapOf(
                                "aliases" to listOf("Undetectable", "ундэтектабл", "Vertzah", "верцах"),
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            ),
                            "cheats" to mapOf(
                                "aliases" to listOf(
                                    "Celestial",
                                    "селестиал",
                                    "целка",
                                    "целестиал",
                                    "celka",
                                    "expensive",
                                    "экспа",
                                    "экспенcив",
                                    "wildclient",
                                    "wild",
                                    "вилд",
                                    "вилдклиент",
                                    "hitbox",
                                    "hit",
                                    "хиты",
                                    "хитбоксы"
                                ),
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            ),
                            "mods" to mapOf(
                                "aliases" to listOf(
                                    "automyst",
                                    "automist",
                                    "автомист",
                                    "neat",
                                    "torohealt",
                                    "NoHurtCum",
                                    "WolrdEdit",
                                    "HurtAnimationRemover",
                                    "AutoFish",
                                    "ReplayMod",
                                    "xray",
                                    "advanced-xray",
                                    "Minimap",
                                    "миникарта",
                                    "минимапа",
                                    "ChestStealer",
                                    "ItemScroller",
                                    "MouseTweaks",
                                    "InvMove",
                                    "MouseWheelie",
                                    "Baritone",
                                    "Fabaritone",
                                    "FreeCam",
                                    "MobHealthBar",
                                    "HealthBarPlus",
                                    "BetterPvP",
                                    "ChunkAnimator",
                                    "InventoryProfilesNext",
                                    "InventoryTotem",
                                    "AutoTotem",
                                    "Schematica",
                                    "Litematica",
                                    "TopkaAutoBuy",
                                    "XorekAutoBuy",
                                    "TopkaAutoSell",
                                    "XorekAutoMyst",
                                    "TopkaCasino",
                                    "AutoSell",
                                    "DiamondGen",
                                    "AutoAttack",
                                    "AutoClicker",
                                    "Автокликер",
                                    "AutoClick"
                                ),
                                "finishCommand" to "ban %suspect% Пункт 4.3 -s"
                            )
                        )


                        configFile.set("reasons", reasons)
                    }
                }


                configFile.save(file)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return file
    }

    fun load(): File? {
        for (configurationType in ConfigurationType.entries) {
            if (configurations.containsKey(configurationType)) {
                continue
            }

            val configurationFile = generateDefaultFile(configurationType.fileName)
            val configuration = YamlConfiguration.loadConfiguration(configurationFile)

            configurations[configurationType] = Pair(configuration, configurationFile)
            return configurationFile
        }
        return null
    }

    fun reload() {
        configurations.clear()
        Revises.instance.initializeConfigurations()
        ReviseConfig.reload()
    }

    private fun getEntry(configurationType: ConfigurationType): Pair<FileConfiguration, File> {
        return configurations[configurationType]!!
    }

    fun get(configurationType: ConfigurationType): FileConfiguration {
        return getEntry(configurationType).first
    }

    fun save(configurationType: ConfigurationType) {
        val entry = getEntry(configurationType)
        entry.first.save(entry.second)
    }

    companion object {
        fun parseReasons(configFile: File): Map<String, List<String>> {
            val config = YamlConfiguration.loadConfiguration(configFile)
            val reasonsSection = config.getConfigurationSection("reasons") ?: return emptyMap()

            val reasonMap = mutableMapOf<String, MutableList<String>>()

            for (reasonKey in reasonsSection.getKeys(false)) {
                val reasonSection = reasonsSection.getConfigurationSection(reasonKey) ?: continue

                val aliases = reasonSection.getStringList("aliases")
                val command = reasonSection.getString("finishCommand") ?: continue

                val namesAndAliases = mutableListOf(reasonKey) + aliases
                reasonMap.getOrPut(command) { mutableListOf() }.addAll(namesAndAliases)
            }

            return reasonMap
        }
    }
}

