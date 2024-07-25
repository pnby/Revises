package me.pink.revises.database.repositories

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import me.pink.revises.Revises
import me.pink.revises.database.models.Check
import me.pink.revises.utils.config.ReviseConfig
import java.sql.SQLException
import java.util.*


object CheckRepository {
    private lateinit var  connectionSource: ConnectionSource
    private lateinit var checkDao: Dao<Check, UUID>

    init {
        initConnectionSource(
            ReviseConfig.Database.databaseHost,
            ReviseConfig.Database.databasePort.toString(),
            ReviseConfig.Database.databaseName,
            ReviseConfig.Database.databaseUsername,
            ReviseConfig.Database.databasePassword
        )
        try {
            checkDao = DaoManager.createDao(connectionSource, Check::class.java)
        } catch (e: RuntimeException) {
            Revises.instance.logger.severe("Error when creating a Dao (are the database credentials valid?)")
        }
    }

    @Throws(SQLException::class)
    fun createTables() {
        TableUtils.createTableIfNotExists(connectionSource, Check::class.java)
    }

    @Throws(SQLException::class)
    fun mockRequest(): Boolean {
        try {
            val check = Check(UUID.randomUUID(), "Suspect", "Inspector", "", null, null)
            checkDao.create(check)
            checkDao.delete(check)
            return true
        } catch (e: SQLException) {
            e.printStackTrace()
            return false
        }
    }

    @Throws(SQLException::class)
    fun close() {
        connectionSource.close()
    }

    @Throws(SQLException::class)
    fun addCheck(check: Check): UUID {
        checkDao.create(check)
        return check.uid!!
    }

    @Throws(SQLException::class)
    fun updateCheck(check: Check): UUID {
        checkDao.update(check)
        return check.uid!!
    }

    @Throws(SQLException::class)
    fun deleteCheck(check: Check): Int {
        return checkDao.delete(check)
    }

    @Throws(SQLException::class)
    fun getCheckById(uid: UUID): Check? {
        return checkDao.queryForId(uid)
    }

    @Throws(SQLException::class)
    fun getAllChecks(): List<Check> {
        return checkDao.queryForAll()
    }

    @Throws(SQLException::class)
    fun getChecksByMemberName(playerName: String): MutableList<Check>? {
        return checkDao.queryBuilder().where().eq("suspectName", playerName).or().eq("inspectorName", playerName).query()
    }

    private fun initConnectionSource(hostname: String, port: String, database: String, username: String, password: String): ConnectionSource {
        when (ReviseConfig.Database.databaseStrategy.lowercase()) {
            "mysql" -> {
                Revises.instance.logger.info("Loading MySQL database strategy")
                try {
                    connectionSource =
                        JdbcConnectionSource("jdbc:mysql://$hostname:$port/$database", username, password)
                } catch (e: RuntimeException) {
                    Revises.instance.logger.severe("MySQL connection failed, defaulting to sqLite")
                    connectionSource = JdbcConnectionSource("jdbc:sqlite:${Revises.instance.dataFolder}/Revises.db")
                    return connectionSource
                }
                return connectionSource
            }
            "postgresql" -> {
                Revises.instance.logger.info("Loading Postgres database strategy")
                try {
                    connectionSource =
                        JdbcConnectionSource("jdbc:postgresql://$hostname:$port/$database", username, password)
                } catch (e: RuntimeException) {
                    Revises.instance.logger.severe("Postgres connection failed, defaulting to sqLite")
                    connectionSource = JdbcConnectionSource("jdbc:sqlite:${Revises.instance.dataFolder}/Revises.db")
                    return connectionSource
                }
                return connectionSource
            }
            "sqlite" -> {
                Revises.instance.logger.info("Loading sqLite database strategy")
                connectionSource = JdbcConnectionSource("jdbc:sqlite:${Revises.instance.dataFolder}/Revises.db")
                return connectionSource
            }
            else -> {
                Revises.instance.logger.severe("The database strategy could not be found, defaulting to sqLite")
                connectionSource = JdbcConnectionSource("jdbc:sqlite:${Revises.instance.dataFolder}/Revises.db")
                return connectionSource
            }
        }

    }

}