package br.edu.utfpr.projeto1_avancado

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val edtZoom: EditText = findViewById(R.id.edtZoom)
        val prefs = getSharedPreferences("config", Context.MODE_PRIVATE)
        edtZoom.setText(prefs.getFloat("zoom", 15f).toString())

        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            val zoom = edtZoom.text.toString().toFloat()
            prefs.edit().putFloat("zoom", zoom).apply()
            finish()
        }
    }
}