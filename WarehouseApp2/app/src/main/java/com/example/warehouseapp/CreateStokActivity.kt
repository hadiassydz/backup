package com.example.warehouseapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateStokActivity : AppCompatActivity() {
    private lateinit var etBarang: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var spinnerUkuran: Spinner
    private lateinit var etStok: EditText
    private lateinit var btnSubmit: Button
    private lateinit var labelHeader: TextView
    private var editMode = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_stok)

        etBarang = findViewById(R.id.et_barang)
        spinnerKategori = findViewById(R.id.spinner_kategori)
        spinnerUkuran = findViewById(R.id.spinner_ukuran)
        etStok = findViewById(R.id.et_stok)
        btnSubmit = findViewById(R.id.btn_submit)
        labelHeader = findViewById(R.id.labelHeader)

        // Setup Spinner untuk kategori dan ukuran
        val kategoriList = arrayOf("Kaos", "Kemeja", "Celana", "Rok", "Hijab")
        val ukuranList = arrayOf("XS", "S", "M", "L", "XL", "XXL")

        // Adapter untuk kategori
        val kategoriAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriList)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategori.adapter = kategoriAdapter

        // Adapter untuk ukuran
        val ukuranAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ukuranList)
        ukuranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUkuran.adapter = ukuranAdapter

        val id = intent.getStringExtra("id")
        if (!id.isNullOrBlank()) {
            editMode = true
            val title = intent.getStringExtra("title").toString()
            labelHeader.text = "Ubah Stok: $title"
            etBarang.setText(title)
            // Ambil kategori dan ukuran dari intent jika dalam mode edit
            spinnerKategori.setSelection(kategoriList.indexOf(intent.getStringExtra("description").toString()))
            spinnerUkuran.setSelection(ukuranList.indexOf(intent.getStringExtra("ukuran").toString()))
            etStok.setText(intent.getIntExtra("stok", 0).toString()) // Mengambil stok untuk mode edit
        }

        btnSubmit.setOnClickListener {
            if (etBarang.text.isEmpty()) {
                Toast.makeText(applicationContext, "Harap isi nama barang terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stok = etStok.text.toString().toIntOrNull()
            if (stok == null || stok < 0) { // Validasi stok
                Toast.makeText(applicationContext, "Harap masukkan jumlah stok yang valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stokModel = StokModel(
                Id = if (editMode) id.toString() else null,
                Barang = etBarang.text.toString(),
                Kategori = spinnerKategori.selectedItem.toString(),
                Ukuran = spinnerUkuran.selectedItem.toString(),
                Stok = stok // Menggunakan nilai stok yang sudah divalidasi
            )

            Log.d(TAG, "Saving stok: ID: ${stokModel.Id}, Barang: ${stokModel.Barang}, Kategori: ${stokModel.Kategori}, Ukuran: ${stokModel.Ukuran}, Stok: ${stokModel.Stok}") // Log untuk debugging

            if (editMode) {
                update(stokModel)
            } else {
                create(stokModel)
            }
        }
    }

    private fun update(stokModel: StokModel) {
        val db = Firebase.firestore
        db.collection("stok").document(stokModel.Id.toString()).set(stokModel)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Berhasil merubah data!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }

    private fun create(stokModel: StokModel) {
        val db = Firebase.firestore
        db.collection("stok")
            .add(stokModel)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Berhasil menambahkan data!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}