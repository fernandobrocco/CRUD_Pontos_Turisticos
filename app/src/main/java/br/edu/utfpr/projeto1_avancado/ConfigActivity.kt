package br.edu.utfpr.projeto1_avancado

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val edtZoom: EditText = findViewById(R.id.edtZoom)
        val spMapType: Spinner = findViewById(R.id.spMapType)

        val prefs = getSharedPreferences("config", Context.MODE_PRIVATE)
        // Lista de tipos de mapa
        val mapTypes = listOf("Normal", "Satélite", "Híbrido", "Terreno")

        // Configurar o Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mapTypes)
        spMapType.adapter = adapter

        // Carregar zoom salvo
        edtZoom.setText(prefs.getFloat("zoom", 15f).toString())

        // Restaurar tipo de mapa salvo
        when (prefs.getInt("mapType", 1)) { // 1 = Normal
            1 -> spMapType.setSelection(0) // Normal
            2 -> spMapType.setSelection(1) // Satélite
            4 -> spMapType.setSelection(2) // Híbrido
            3 -> spMapType.setSelection(3) // Terreno
        }

        // Botão salvar
        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            val zoom = edtZoom.text.toString().toFloatOrNull() ?: 15f

            // Descobre qual foi selecionado
            val mapType = when (spMapType.selectedItemPosition) {
                0 -> 1 // Normal
                1 -> 2 // Satélite
                2 -> 4 // Híbrido
                3 -> 3 // Terreno
                else -> 1
            }
            // Salva zoom e tipo de mapa
            prefs.edit()
                .putFloat("zoom", zoom)
                .putInt("mapType", mapType) // GoogleMap.MAP_TYPE_*
                .apply()

            finish()
        }
    }
}
