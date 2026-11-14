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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Entity(tableName = "audio_chapter_configs")
data class AudioChapterConfigEntity(
  @PrimaryKey val chapterId: String,
  val narratorType: String = "AUTHOR_VOICE",
  val voiceCharacteristic: String = "WARM_FRIENDLY",
  val authorVoiceAuthorized: Boolean = false,
  val workflowStatus: String = "NOT_STARTED",
  val approvedVersion: Int = 0,
  val approvedManuscriptHash: String = "",
  val checklistMask: Int = 0,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "audio_segments")
data class AudioSegmentEntity(
  @PrimaryKey val segmentId: String,
  val chapterId: String,
  val sectionId: String,
  val segmentIndex: Int,
  val titleFa: String,
  val titleEn: String,
  val editedScriptFa: String,
  val currentSourceHash: String,
  val recordedSourceHash: String = "",
  val narratorType: String = "AUTHOR_VOICE",
  val audioFilePath: String? = null,
  val audioFormat: String? = null,
  val durationMs: Long = 0L,
  val peakAmplitude: Int = 0,
  val isClippingWarning: Boolean = false,
  val isApproved: Boolean = false,
  val versionNumber: Int = 0,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "audio_version_archives")
data class AudioVersionArchiveEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val chapterId: String,
  val versionNumber: Int,
  val manuscriptHash: String,
  val archivePath: String,
  val formatLabel: String,
  val narratorSummary: String,
  val approvedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "listening_progress")
data class ListeningProgressEntity(
  @PrimaryKey val chapterId: String,
  val lastSegmentId: String,
  val lastSegmentIndex: Int = 0,
  val positionMs: Long = 0L,
  val playbackSpeed: Float = 1.0f,
  val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "audio_bookmarks")
data class AudioBookmarkEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val chapterId: String,
  val segmentId: String,
  val segmentIndex: Int,
  val positionMs: Long,
  val label: String,
  val createdAt: Long = System.currentTimeMillis(),
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

  @Query("SELECT * FROM audio_chapter_configs")
  fun observeAudioConfigs(): Flow<List<AudioChapterConfigEntity>>

  @Query("SELECT * FROM audio_chapter_configs WHERE chapterId = :chapterId")
  suspend fun getAudioConfig(chapterId: String): AudioChapterConfigEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAudioConfig(config: AudioChapterConfigEntity)

  @Query("SELECT * FROM audio_segments ORDER BY chapterId ASC, segmentIndex ASC")
  fun observeAllAudioSegments(): Flow<List<AudioSegmentEntity>>

  @Query("SELECT * FROM audio_segments WHERE chapterId = :chapterId ORDER BY segmentIndex ASC")
  suspend fun getSegmentsForChapter(chapterId: String): List<AudioSegmentEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAudioSegment(segment: AudioSegmentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertAudioSegments(segments: List<AudioSegmentEntity>)

  @Query("SELECT * FROM audio_version_archives ORDER BY approvedAt DESC")
  fun observeAudioVersionArchives(): Flow<List<AudioVersionArchiveEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAudioVersionArchive(archive: AudioVersionArchiveEntity)

  @Query("SELECT * FROM listening_progress ORDER BY updatedAt DESC")
  fun observeListeningProgress(): Flow<List<ListeningProgressEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertListeningProgress(progress: ListeningProgressEntity)

  @Query("SELECT * FROM audio_bookmarks ORDER BY createdAt DESC")
  fun observeAudioBookmarks(): Flow<List<AudioBookmarkEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAudioBookmark(bookmark: AudioBookmarkEntity)

  @Query("DELETE FROM audio_bookmarks WHERE id = :id")
  suspend fun deleteAudioBookmark(id: Long)
}

@Database(
  entities =
    [
      BookmarkEntity::class,
      NoteEntity::class,
      ReadingProgressEntity::class,
      AudioChapterConfigEntity::class,
      AudioSegmentEntity::class,
      AudioVersionArchiveEntity::class,
      ListeningProgressEntity::class,
      AudioBookmarkEntity::class,
    ],
  version = 2,
  exportSchema = false,
)
abstract class BookDatabase : RoomDatabase() {
  abstract fun bookUserDao(): BookUserDao

  companion object {
    @Volatile private var instance: BookDatabase? = null

    val MIGRATION_1_2 =
      object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS audio_chapter_configs (
              chapterId TEXT NOT NULL PRIMARY KEY,
              narratorType TEXT NOT NULL,
              voiceCharacteristic TEXT NOT NULL,
              authorVoiceAuthorized INTEGER NOT NULL,
              workflowStatus TEXT NOT NULL,
              approvedVersion INTEGER NOT NULL,
              approvedManuscriptHash TEXT NOT NULL,
              checklistMask INTEGER NOT NULL,
              updatedAt INTEGER NOT NULL
            )
            """
              .trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS audio_segments (
              segmentId TEXT NOT NULL PRIMARY KEY,
              chapterId TEXT NOT NULL,
              sectionId TEXT NOT NULL,
              segmentIndex INTEGER NOT NULL,
              titleFa TEXT NOT NULL,
              titleEn TEXT NOT NULL,
              editedScriptFa TEXT NOT NULL,
              currentSourceHash TEXT NOT NULL,
              recordedSourceHash TEXT NOT NULL,
              narratorType TEXT NOT NULL,
              audioFilePath TEXT,
              audioFormat TEXT,
              durationMs INTEGER NOT NULL,
              peakAmplitude INTEGER NOT NULL,
              isClippingWarning INTEGER NOT NULL,
              isApproved INTEGER NOT NULL,
              versionNumber INTEGER NOT NULL,
              updatedAt INTEGER NOT NULL
            )
            """
              .trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS audio_version_archives (
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              chapterId TEXT NOT NULL,
              versionNumber INTEGER NOT NULL,
              manuscriptHash TEXT NOT NULL,
              archivePath TEXT NOT NULL,
              formatLabel TEXT NOT NULL,
              narratorSummary TEXT NOT NULL,
              approvedAt INTEGER NOT NULL
            )
            """
              .trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS listening_progress (
              chapterId TEXT NOT NULL PRIMARY KEY,
              lastSegmentId TEXT NOT NULL,
              lastSegmentIndex INTEGER NOT NULL,
              positionMs INTEGER NOT NULL,
              playbackSpeed REAL NOT NULL,
              updatedAt INTEGER NOT NULL
            )
            """
              .trimIndent()
          )
          db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS audio_bookmarks (
              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
              chapterId TEXT NOT NULL,
              segmentId TEXT NOT NULL,
              segmentIndex INTEGER NOT NULL,
              positionMs INTEGER NOT NULL,
              label TEXT NOT NULL,
              createdAt INTEGER NOT NULL
            )
            """
              .trimIndent()
          )
        }
      }

    fun getInstance(context: Context): BookDatabase {
      return instance
        ?: synchronized(this) {
          instance
            ?: Room.databaseBuilder(
                context.applicationContext,
                BookDatabase::class.java,
                "honar_zistan_db",
              )
              .addMigrations(MIGRATION_1_2)
              .build()
              .also { instance = it }
        }
    }
  }
}
