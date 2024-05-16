package SQLlite
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Oficio {

    @SerializedName("nombreO")
    @Expose
    var nombreO: String = ""

    @Expose
    @SerializedName("Descripcion")
    var Descripcion: String = ""
    @SerializedName("PalabrasClaves")
    @Expose
    var PalabrasClaves: String = ""
}