package com.jankrb.fff_layout.dbclasses

import android.app.Application
import androidx.room.*


//Tabellen definieren
@Entity
data class Scan (
    @PrimaryKey(autoGenerate = true) val scan_id: Int = 0,
    val insectName: String,
    val insectId: String,
    val latitude: String,
    val longitude: String,
    val altitude: String,
    val timestamp: String,
    @ColumnInfo(defaultValue = "0") val synced: Int
)

//Datenbankzugriffe definieren, DAO = Data access object
@Dao
interface ScanDao{
    @Query("SELECT * FROM Scan")
    suspend fun getAll(): List<Scan>

    @Query("SELECT * FROM Scan WHERE synced = 0")
    suspend fun getUnsynced(): List<Scan>

    @Query("SELECT COUNT(*) FROM Scan WHERE synced = 0")
    suspend fun getNumberOfUnsynced(): Int

    @Query("SELECT COUNT(DISTINCT insectId) FROM Scan")
    suspend fun getNumberOfTypes(): Int

    @Query("UPDATE Scan SET synced = :synced WHERE scan_id = :scan_id")
    suspend fun setSynced(scan_id: Int, synced: Int)

    @Query("SELECT COUNT(*) FROM Scan")
    suspend fun getNumberOfColumns(): Int



    @Insert
    suspend fun insertAll(vararg scans: Scan)

    @Delete
    suspend fun delete(scan: Scan)
}

//Datenbank Klasse definieren
@Database(entities = arrayOf(Scan::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun scanDao(): ScanDao
}



//RoomDatabase Singleton Objekt, welches beim Start der App erzeugt wird
//Objekt der Application class wird als erstes erzeugt, im normalfall nicht
//manuell notwendig
class DBApplication: Application()
{
    companion object {
        var db : AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "Scan-db").build()
        //Log.d("INFO","Singleton erzeugt!")
    }
}

//globale Variable, die zusichert, dass es Zugriff auf das Singleton Objekt immer möglich ist
val dbvar: AppDatabase = DBApplication.Companion.db!!

