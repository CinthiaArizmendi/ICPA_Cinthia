
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log

class DataManager(context: Context) {
    val context: Context = context
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)


    fun insertOrUpdateOficio(palabrasClaves: String, nombreOfi: String, Descripcion: String) {
        try {
            val db = databaseHelper.writableDatabase
            // Consulta para verificar si ya existe un registro con el mismo valor de nombreOfi
            val query =
                "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE LOWER(${DatabaseHelper.COLUMN_NAME}) = LOWER(?)"
            val cursor = db.rawQuery(query, arrayOf(nombreOfi.trim()))

            if (cursor.moveToFirst()) {
                //datos existentes
            } else {
                val values = ContentValues()
                values.put(DatabaseHelper.COLUMN_PC, palabrasClaves)
                values.put(DatabaseHelper.COLUMN_NAME, nombreOfi)
                values.put(DatabaseHelper.COLUMN_DESCRIPCION, Descripcion)

                db.insert(DatabaseHelper.TABLE_NAME, null, values)

            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            Log.e("MiApp", "Error al insertar o actualizar oficio: ${e.message}")
        }
    }


    @SuppressLint("Range")
    fun getAllOficios(): ArrayList<MisOficios> {
        val db = databaseHelper.readableDatabase
        val oficiosList = mutableListOf<MisOficios>()

        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME}"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
            val palabrasClaves = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PC))
            val nombreOfi = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
            val Descripcion =
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPCION))
            val oficio = MisOficios(id, palabrasClaves, nombreOfi, Descripcion)
            //println(" -------$id $nombreOfi")
            oficiosList.add(oficio)
        }
        cursor.close()
        db.close()
        return oficiosList as ArrayList<MisOficios>
    }


    fun obtenerDescripcion(oficioBuscado: String): String {
        val db = databaseHelper.readableDatabase
        var descripcion = "No se encontró descripción"

        val selectQuery =
            "SELECT ${DatabaseHelper.COLUMN_DESCRIPCION} FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_NAME} = ?"
        val selectionArgs = arrayOf(oficioBuscado)

        val cursor = db?.rawQuery(selectQuery, selectionArgs)

        try {
            val cursor = db?.rawQuery(selectQuery, selectionArgs)
            cursor?.use {
                if (it.moveToFirst()) {
                    descripcion =
                        it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPCION))
                }
            }
        } catch (e: Exception) {
            Log.e("MiApp", "Error al realizar la consulta a la base de datos: ${e.message}")
        }


        db.close()
        return descripcion
    }
}
