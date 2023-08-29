package com.safarione.chat.client

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "messages")
internal data class MessageRecord(
    @PrimaryKey
    val id: String,
    val content: String,
    val from: String,
    val to: String?,
    val time: Long,
    @ColumnInfo("group_chat")
    val groupChat: String?
)

@Entity(tableName = "group_chats")
internal data class GroupChatRecord(
    @PrimaryKey
    val name: String,
    @ColumnInfo("time_created")
    val timeCreated: Long
)

@Dao
internal interface MessageDao {
    @Query("SELECT * FROM MESSAGES WHERE id = :id")
    fun get(id: String): MessageRecord?

    @Query("SELECT * FROM messages")
    fun getAll(): List<MessageRecord>

    @Query("SELECT * FROM messages WHERE `from` = :from AND group_chat IS NULL")
    fun loadSingleUserChatMessages(from: String): List<MessageRecord>

    @Query("SELECT * FROM messages WHERE group_chat = :name")
    fun loadMultiUserChatMessages(name: String): List<MessageRecord>

    @Insert
    fun insert(message: MessageRecord)

    @Insert
    fun insertAll(messages: List<MessageRecord>)
}

@Dao
internal interface GroupChatDao {
    @Query("SELECT * FROM group_chats")
    fun getAll(): List<GroupChatRecord>

    @Insert
    fun insert(groupChat: GroupChatRecord)

    @Insert
    fun insertAll(groupChats: List<GroupChatRecord>)
}

@Database(entities = [MessageRecord::class, GroupChatRecord::class], version = 1)
internal abstract class Database: RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun groupChatDao(): GroupChatDao
}