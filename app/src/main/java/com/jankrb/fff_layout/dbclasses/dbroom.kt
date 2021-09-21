package com.jankrb.fff_layout.dbclasses

import android.app.Application
import androidx.room.*


//Tabellen definieren
@Entity
data class Scan (
    @PrimaryKey(autoGenerate = true) val scan_id: Int = 0,
    val latitude: String?,
    val longitude: String?,
    val altitude: String?, // Höhe
    val timestamp: String
)

//Datenbankzugriffe definieren, DAO = Data access object
@Dao
interface ScanDao{
    @Query("SELECT * FROM Scan")
    suspend fun getAll(): List<Scan>

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

