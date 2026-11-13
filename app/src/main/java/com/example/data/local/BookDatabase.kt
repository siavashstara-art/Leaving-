package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey val chapterId: String,
  val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val chapterId: String,
  val content: String,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
  @PrimaryKey val chapterId: String,
  val isCompleted: Boolean = false,
  val scrollFraction: Float = 0f,
  val lastReadAt: Long = System.currentTimeMillis(),
)

@Dao
interface BookUserDao {
  @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
  fun observeBookmarks(): Flow<List<BookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addBookmark(bookmark: BookmarkEntity)

  @Query("DELETE FROM bookmarks WHERE chapterId = :chapterId")
  suspend fun removeBookmark(chapterId: String)

  @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
  fun observeNotes(): Flow<List<NoteEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertNote(note: NoteEntity)

  @Query("DELETE FROM notes WHERE id = :noteId")
  suspend fun deleteNote(noteId: Long)

  @Query("SELECT * FROM reading_progress ORDER BY lastReadAt DESC")
  fun observeProgress(): Flow<List<ReadingProgressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertProgress(progress: ReadingProgressEntity)
}

@Database(
  entities = [BookmarkEntity::class, NoteEntity::class, ReadingProgressEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class BookDatabase : RoomDatabase() {
  abstract fun bookUserDao(): BookUserDao

  companion object {
    @Volatile private var instance: BookDatabase? = null

    fun getInstance(context: Context): BookDatabase {
      return instance
        ?: synchronized(this) {
          instance
            ?: Room.databaseBuilder(
                context.applicationContext,
                BookDatabase::class.java,
                "honar_zistan_db",
              )
              .build()
              .also { instance = it }
        }
    }
  }
}
