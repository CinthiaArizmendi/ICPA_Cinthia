import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//primer clase
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val context: Context = context
    companion object {
        private const val DATABASE_NAME = "proyecto_kerkly.db"//  -->aqui nombre de tu base de datos
        private const val DATABASE_VERSION = 1

        // Define la estructura de la tabla
        const val TABLE_NAME = "Oficio"
        const val COLUMN_ID = "id"
        const val COLUMN_PC = "palabras_claves"
        const val COLUMN_NAME = "nombreOfi"
        const val COLUMN_DESCRIPCION = "Descripcion"

    }
        override fun onCreate(db: SQLiteDatabase?) {
            // Crea la tabla
            val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER  PRIMARY KEY AUTOINCREMENT, $COLUMN_PC TEXT, $COLUMN_NAME TEXT,$COLUMN_DESCRIPCION TEXT)"
            db?.execSQL(createTableQuery)
        }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


    fun isTableExists(tableName: String): Boolean {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'", null)
            val tableExists = cursor.moveToFirst()
            cursor.close()
            db.close()
            return tableExists
        }

    }
