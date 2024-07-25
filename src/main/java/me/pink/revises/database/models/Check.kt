package me.pink.revises.database.models

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.time.LocalDateTime
import java.util.UUID

@DatabaseTable(tableName = "users")
data class Check(
    @DatabaseField(id = true)
    var uid: UUID? = null,

    @DatabaseField
    var suspectName: String? = null,

    @DatabaseField
    var inspectorName: String? = null,

    @DatabaseField
    var result: String? = null,

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    var startCheckTime: LocalDateTime? = null,

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    var stopCheckTime: LocalDateTime? = null,
) {
    init {
        if (startCheckTime == null) {
            startCheckTime = LocalDateTime.now()
        }
    }
}