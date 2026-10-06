package gcd.testing.firstproject

import androidx.room3.Room
import androidx.room3.RoomDatabase
import gcd.testing.firstproject.config.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )?.path + "/my_room.db"
    
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath
    )
}
