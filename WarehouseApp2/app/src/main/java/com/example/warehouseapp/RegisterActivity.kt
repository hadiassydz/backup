package com.example.warehouseapp


import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    lateinit var btnRegister : Button
    lateinit var etEmail : EditText
    lateinit var etName : EditText
    lateinit var etPassword : EditText
    lateinit var etPasswordConfirmation : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btn_register)
        etEmail = findViewById(R.id.et_register_email)
        etName = findViewById(R.id.et_register_name)
        etPassword = findViewById(R.id.et_register_password)
        etPasswordConfirmation = findViewById(R.id.et_register_password_confirmation)

        btnRegister.setOnClickListener {
            var userModel = UserModel(
                Email = etEmail.text.toString(),
                Name = etName.text.toString(),
                Password = PasswordHelper.md5(etPassword.text.toString())
            )

            checkUser(etEmail.text.toString()) { isSuccess, isRegistered ->
                if (!isSuccess) {
                    Toast.makeText(
                        applicationContext,
                        "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkUser
                } else if (isRegistered) {
                    Toast.makeText(
                        applicationContext,
                        "Akun dengan email ${etEmail.text.toString()} sudah terdaftar!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@checkUser
                }

                this.registerUser(userModel)
            }
        }
    }

    private fun checkUser(email: String, checkResult: (isSuccess: Boolean, isRegistered: Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->

                var isSuccess = true
                var isRegistered = false

                if (!documents.isEmpty) {
                    isRegistered = true
                }
                checkResult.invoke(isSuccess, isRegistered)
            }
            .addOnFailureListener { exception ->
                checkResult.invoke(false, false)
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    fun registerUser(userModel: UserModel) {
        val db = Firebase.firestore
        db.collection("users")
            .add(userModel)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    applicationContext,
                    "Berhasil melakukan registrasi!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}