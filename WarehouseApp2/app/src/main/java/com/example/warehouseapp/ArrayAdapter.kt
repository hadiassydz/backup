import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouseapp.R

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerKategori: Spinner
    private lateinit var spinnerUkuran: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerKategori = findViewById(R.id.spinner_kategori)
        spinnerUkuran = findViewById(R.id.spinner_ukuran)

        // Data untuk kategori
        val kategoriList = arrayOf("Kaos", "Kemeja", "Celana", "Rok", "Hijab")
        val kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriList)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategori.adapter = kategoriAdapter

        // Data untuk ukuran
        val ukuranList = arrayOf("XS", "S", "M", "L", "XL", "XXL")
        val ukuranAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ukuranList)
        ukuranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUkuran.adapter = ukuranAdapter
    }
}