package com.example.warehouseapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var listStok: ListView
    private lateinit var btnCreateStok: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listStok = findViewById(R.id.list_stok)
        btnCreateStok = findViewById(R.id.btn_create_stok)

        btnCreateStok.setOnClickListener {
            Log.d(TAG, "Tombol + ditekan")
            val intent = Intent(this, CreateStokActivity::class.java)
            startActivity(intent)
        }

        fetchDataFromFirestore()

        listStok.setOnItemClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as StokModel

            val intent = Intent(this, EditStokActivity::class.java)
            intent.putExtra("stokId", item.Id)
            intent.putExtra("stokBarang", item.Barang)
            intent.putExtra("stokKategori", item.Kategori)
            intent.putExtra("stokUkuran", item.Ukuran)
            intent.putExtra("stokStok", item.Stok.toString())
            startActivity(intent)
        }

        // Tambahkan listener untuk long click
        listStok.setOnItemLongClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as StokModel
            showDeleteConfirmationDialog(item)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        fetchDataFromFirestore()  // Memperbarui tampilan dengan data terbaru
    }

    private fun fetchDataFromFirestore() {
        val db = Firebase.firestore
        db.collection("stok")
            .get()
            .addOnSuccessListener { result ->
                val items = ArrayList<StokModel>()

                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    items.add(
                        StokModel(
                            Id = document.id,
                            Barang = document.data["barang"] as? String ?: "Unknown",
                            Kategori = document.data["kategori"] as? String ?: "Unknown",
                            Ukuran = document.data["ukuran"] as? String ?: "Unknown",
                            Stok = document.data["stok"] as? Int ?: 0
                        )
                    )
                }

                val adapter = StokAdapter(this, R.layout.stok_item, items)
                listStok.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun showDeleteConfirmationDialog(item: StokModel) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus item '${item.Barang}'?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                item.Id?.let { item.Barang?.let { it1 -> deleteItem(it, it1) } }
                fetchDataFromFirestore() // Memperbarui tampilan setelah penghapusan
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.dismiss() }

        val alert = dialogBuilder.create()
        alert.setTitle("Konfirmasi Hapus")
        alert.show()
    }

    private fun deleteItem(id: String, title: String) {
        val db = Firebase.firestore
        db.collection("stok").document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Berhasil menghapus item: $title", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal menghapus item: $title.", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Error deleting document.", exception)
            }
    }
}