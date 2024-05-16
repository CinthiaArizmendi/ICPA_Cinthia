package com.example.icpa_cinthia


import DataManager
import MisOficios
import SQLlite.Oficio
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerNormal: Spinner
    private lateinit var listaSpinner: ArrayList<String>
    private lateinit var dataManager: DataManager
    private lateinit var lista: ArrayList<MisOficios>
    private var palabrasClave = mutableMapOf<String,String>()
    private lateinit var minilista: ArrayList<String>
    private var clik_Otro = false
    private var textoAnterior = ""
    val listaPalabrasAsociadas = mutableListOf<String>()
    private lateinit var layoutProblem: TextInputLayout
    private var Si_oNoPalabaraAsosiada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        dataManager = DataManager(this)
        val inputProblematica = findViewById<TextInputEditText>(R.id.inputProblematica)
        spinnerNormal = findViewById(R.id.spinnerNormal)
        layoutProblem = findViewById(R.id.layoutProblematica)
        llenadoSpinner()
        getOficios()
        obtenerOficiosDB()
        inputProblematica.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("beforeTextChanged $p0")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("onTextChanged $p0")
            }

            override fun afterTextChanged(p0: Editable?) {
                // Se obtiene el texto actual del EditText y se convierte en una lista de palabras separadas por espacios
                val p = p0.toString()
                val listaDePalabrasSeparadasPorEspacio: List<String> = p.split(" ")
                println("palaabras $listaDePalabrasSeparadasPorEspacio")
                // Se verifica si se eliminó alguna palabra en comparación con el texto anterior
                val palabrasEliminadas = encontrarPalabrasEliminadas(textoAnterior, p)
                if (clik_Otro == false) {
                    if (palabrasEliminadas.isNotEmpty()) {// Si se eliminaron palabras
                        println("Palabras eliminadas: $palabrasEliminadas")
                        // Eliminar las palabras asociadas de listaPalabrasAsociadas
                        for (palabraEliminada in palabrasEliminadas) {
                            val palabraAsociadaEliminada = palabrasClave[palabraEliminada]
                            if (palabraAsociadaEliminada != null) {
                                listaPalabrasAsociadas.remove(palabraAsociadaEliminada)
                                println("Palabra asociada eliminada: $palabraAsociadaEliminada")
                            }
                        }

                        // Actualizar el Spinner con la nueva lista
                        spinnerNormal.setAdapter(
                            ArrayAdapter<String>(this@MainActivity,
                                android.R.layout.simple_spinner_dropdown_item,
                                listaPalabrasAsociadas
                            )
                        )
                    }

                    // Se actualiza el texto anterior con el texto actual
                    textoAnterior = p
                    println("textoAnterior $textoAnterior")
                    // listaTextos.clear()
                    layoutProblem.error = null
                    var contienePalabraAsociada = false
                    // Se itera sobre cada palabra en el texto actual
                    for (indice in 0 until listaDePalabrasSeparadasPorEspacio.size) {
                        println("plabras iteradas $indice}")
                        var palabraSinEspacios = listaDePalabrasSeparadasPorEspacio[indice].trim() //El .trim sirve para eliminar espacios de una cadena de texto
                        if (palabraSinEspacios.isNotBlank()) { //Verifica que no esta vacia
                            var palabraAsociada: String? = null //Alamacena la palabra que este asociada a un oficio
                            // Se busca la palabra asociada en el diccionario palabrasClave
                            for ((clave, palabra) in palabrasClave) {
                                if (clave == palabraSinEspacios) {
                                    palabraAsociada = palabra
                                    break
                                }
                            }
                            if (palabraAsociada != null) {
                                println("La palabra asociada a '$palabraSinEspacios' es: $palabraAsociada")
                                Si_oNoPalabaraAsosiada = true

                                if (!listaPalabrasAsociadas.contains(palabraAsociada)) {
                                    listaPalabrasAsociadas.add(0, palabraAsociada)
                                    //listaPalabrasAsociadas.add("Otro")
                                    println("palabra agregada a lista")
                                    //buttonObtenerSeleccion.visibility = View.VISIBLE

                                }
                                spinnerNormal.setAdapter(
                                    ArrayAdapter<String>(this@MainActivity,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        listaPalabrasAsociadas
                                    )
                                )
                                contienePalabraAsociada = true


                            } else {
                                // Si la palabra no tiene asociación
                                if (listaPalabrasAsociadas.isEmpty()) {
                                    if (clik_Otro != true) {
                                        println("no coincide...")
                                        llenarSpinner()
                                        Si_oNoPalabaraAsosiada = false
                                       // buttonObtenerSeleccion.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                    // Si el texto actual está en blanco y no contiene ninguna palabra asociada
                    if (p0.toString().isBlank() && !contienePalabraAsociada) {
                        // Llamar al método cuando el texto está vacío y no contiene ninguna palabra asociada
                        if (clik_Otro == false) {
                            llenarSpinner()
                            listaPalabrasAsociadas.clear()
                            println("borrado..")
                           // buttonObtenerSeleccion.visibility = View.GONE
                        }
                    }

                }/* else {// Si clik_Otro es true
                    for (palabraEliminada in palabrasEliminadas) {
                        val palabraAsociadaEliminada = palabrasClave[palabraEliminada]
                        if (palabraAsociadaEliminada != null) {
                            listaPalabrasAsociadas.remove(palabraAsociadaEliminada)
                            println("Palabra asociada eliminada: $palabraAsociadaEliminada")

                        }
                    }


                    layoutProblem.error = null
                    var contienePalabraAsociada = false
                    for (i in 0 until parts.size) {
                        var pa = parts[i].trim()
                        if (pa.isNotBlank()) {
                            var palabraAsociada: String? = null
                            // Se busca la palabra asociada en el diccionario palabrasClave
                            for ((clave, palabra) in palabrasClave) {
                                if (clave == pa) {
                                    palabraAsociada = palabra
                                    break
                                }
                            }
                            if (palabraAsociada != null) {
                                println("La palabra asociada a '$pa' es: $palabraAsociada")
                                banPalabaraAsosiada = true

                                if (!listaPalabrasAsociadas.contains(palabraAsociada)) {
                                    listaPalabrasAsociadas.add(0, palabraAsociada)
                                    //listaPalabrasAsociadas.add("Otro")
                                    println("palabra agregada a lista")
                                    buttonObtenerSeleccion.visibility = View.VISIBLE

                                }
                                // Se muestra un AlertDialog si no se ha ejecutado previamente
                                if (alertEjecutado == false) {
                                    val alertDialogBuilder =
                                        AlertDialog.Builder(requireContext())

                                    // Se establece el mensaje del AlertDialog
                                    alertDialogBuilder.setMessage(
                                        "La palabra coincide con el oficio ${
                                            listaPalabrasAsociadas.get(
                                                0
                                            )
                                        } ¿Desea Camabiarlo?"
                                    )

                                    // Se configura el botón "Aceptar" del AlertDialog
                                    alertDialogBuilder.setPositiveButton(
                                        "Aceptar",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            // Acciones a realizar si el usuario hace clic en "Aceptar"
                                            spinner.setAdapter(
                                                ArrayAdapter<String>(
                                                    requireContext(),
                                                    android.R.layout.simple_spinner_dropdown_item,
                                                    listaPalabrasAsociadas
                                                )
                                            )
                                            alertEjecutado = true
                                            dialog.dismiss()
                                        })

                                    // Configurar el botón "Cancelar"
                                    alertDialogBuilder.setNegativeButton(
                                        "Cancelar",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            // Acciones a realizar si el usuario hace clic en "Cancelar"
                                            // Por ejemplo, puedes cancelar la operación
                                            alertEjecutado = true
                                            dialog.dismiss()
                                        })

                                    // Mostrar el AlertDialog
                                    val alertDialog = alertDialogBuilder.create()
                                    alertDialog.show()
                                    contienePalabraAsociada = true
                                }
                            }
                        }
                    }
                }*/
            }
        })

        val boton_servicio_urgente = findViewById<MaterialButton>(R.id.boton_servicio_urgente)
        val button_presupuesto = findViewById<MaterialButton>(R.id.button_presupuesto)

        boton_servicio_urgente.setOnClickListener {
            showMensaje("ALERTA")
        }

        button_presupuesto.setOnClickListener {
            showMensaje("waoss :0")
        }
    }

    private fun showMensaje(mensaje:String){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
    fun obtenerOficiosDB() {
        lista = ArrayList()
        lista = dataManager.getAllOficios()
        llenarSpinner()
        Diccionario()
    }

    fun Diccionario() {
        var palClaves = ""
        var oficio = ""
        // Iterar sobre cada elemento en la lista
        for (i in 0 until lista!!.size) {
            // Obtener las palabras clave y el nombre del oficio del elemento actual en la lista
            palClaves = lista[i].palabrasClaves
            //  pal = pal+ palClaves +"|"
            oficio = lista[i].nombreOfi
            // Dividir las palabras clave en una lista utilizando ", " como separador
            val parts: List<String> = palClaves.split(", ")
            // Iterar sobre cada palabra clave en la lista de palabras clave
            for (i in 0 until parts.size) {
                var pa = parts[i]
                // Asociar la palabra clave con el nombre del oficio en el diccionario palabrasClave
                palabrasClave[pa] = oficio
                // palC.add(pa)
            }
        }
    }
    private fun llenarSpinner() {
        minilista = ArrayList()
        minilista.add(getString(R.string.txt_HomeFragment_txt2))
        minilista.add(getString(R.string.txt_HomeFragment_txt3))
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            minilista
        )
        spinnerNormal.adapter = adapter

        spinnerNormal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                println("Tamaño de la lista ${lista.size}")
                // Verifica si la opción seleccionada es "Otro"
                if (selectedItem == getString(R.string.txt_HomeFragment_txt3)) {
                    // Aquí puedes manejar el evento cuando se selecciona "Otro"
                    // Por ejemplo, mostrar un cuadro de diálogo, iniciar otra actividad, etc.
                    println("Tamaño de la lista ${lista.size}")
                    SpinerADapter(lista)
                    spinnerNormal.setSelection(0, true)
                    //buttonObtenerSeleccion.visibility = View.VISIBLE
                    clik_Otro = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Implementa este método si deseas manejar el caso en el que no se selecciona nada.
                }
            }

    }

    private fun llenadoSpinner(){
        listaSpinner = ArrayList()
        listaSpinner.add(getString(R.string.txt_seleOficio))
        listaSpinner.add(getString(R.string.txt_otro))

        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, listaSpinner
        )
        spinnerNormal.adapter = adapter
    }
    fun SpinerADapter(lista: ArrayList<MisOficios>) {
        val aa = AdapterSpinnercopia(this@MainActivity, lista)
        spinnerNormal.adapter= aa

    }
    private fun getOficios () { // Declarando una función para obtener una lista de oficios
        val gson = GsonBuilder() // Creando una instancia de GsonBuilder
            .setLenient() // Configurando Gson para que sea tolerante
            .create() // Construyendo la instancia de Gson
        val interceptor = HttpLoggingInterceptor() // Creando un interceptor de registro HTTP
        interceptor.level = HttpLoggingInterceptor.Level.BODY // Configurando el nivel de registro para incluir cuerpos de solicitud y respuesta
        val client: OkHttpClient = OkHttpClient.Builder().build() // Creando una instancia de OkHttpClient

        val retrofit = Retrofit.Builder() // Creando un constructor de Retrofit
            .baseUrl("${getString(R.string.MiURL)}/webservice/") // Estableciendo la URL base para la API
            .client(client) // Estableciendo el OkHttpClient para el constructor
            .addConverterFactory(GsonConverterFactory.create(gson)) // Agregando un convertidor Gson para manejar JSON
            .build() // Construyendo la instancia de Retrofit
        val presupuestoGET = retrofit.create(ObtenerOficiosInterface::class.java) // Creando una instancia de la interfaz de la API
        val call = presupuestoGET.misOficios() // Realizando una llamada para obtener la lista de oficios
        call?.enqueue(object : Callback<List<Oficio?>?> { // Encolando la llamada para su ejecución asíncrona
            override fun onResponse(call: Call<List<Oficio?>?>, response: Response<List<Oficio?>?>) { // Manejando la respuesta
                if (response.isSuccessful) { // Comprobando si la respuesta es exitosa
                    response.body()?.let { body ->
                        println("debug 1")
                        // Accediendo al cuerpo de la respuesta
                        val listaArrayOficios = response.body() as ArrayList<Oficio> // Convirtiendo el cuerpo de la respuesta en una lista de oficios
                        var oficio = "" // Declarando una variable para contener un solo oficio
                        var oficios: MisOficios // Declarando una variable para contener múltiples oficios
                        // dataManager.deleteAllOficios() // Eliminando todos los oficios existentes (comentado)
                        for (i in 0 until listaArrayOficios!!.size){ // Iterando a través de la lista de oficios
                            oficios = MisOficios(i,listaArrayOficios[i].PalabrasClaves,listaArrayOficios[i].nombreO,listaArrayOficios[i].Descripcion) // Creando una instancia de MisOficios
                            dataManager.insertOrUpdateOficio(listaArrayOficios[i].PalabrasClaves,listaArrayOficios[i].nombreO,listaArrayOficios[i].Descripcion) // Insertando o actualizando un oficio
                        println("oficio ${listaArrayOficios[i].nombreO} ${listaArrayOficios[i].PalabrasClaves}")
                        }

                    }
                }else{
                    println("respuesta vacia")

                }
            }
            override fun onFailure(call: Call<List<Oficio?>?>, t: Throwable) { // Manejando el fallo
                Log.d("error del retrofit", t.toString()) // Registrando el error
                // Toast.makeText(requireActivity(), t.toString(), Toast.LENGTH_LONG).show() // Mostrando un mensaje de error (comentado)

            }
        })

    }

    private fun encontrarPalabrasEliminadas(textoAnterior: String, textoActual: String): List<String> {
        // Dividir el texto anterior en una lista de palabras utilizando el espacio como delimitador
        val palabrasAntes = textoAnterior.split(" ")

        // Dividir el texto actual en una lista de palabras utilizando el espacio como delimitador
        val palabrasDespues = textoActual.split(" ")

        // Filtrar las palabras en palabrasAntes para mantener solo aquellas que no están presentes en palabrasDespues
        val palabrasEliminadas = palabrasAntes.filterNot { palabrasDespues.contains(it) }

        // Devolver la lista de palabras eliminadas
        return palabrasEliminadas
    }

}