package br.edu.utfpr.projeto1_avancado

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val edtZoom: EditText = findViewById(R.id.edtZoom)
        val rgMapType: RadioGroup = findViewById(R.id.rgMapType)

        val prefs = getSharedPreferences("config", Context.MODE_PRIVATE)

        // Carregar zoom salvo
        edtZoom.setText(prefs.getFloat("zoom", 15f).toString())

        // Restaurar tipo de mapa salvo
        when (prefs.getInt("mapType", 1)) { // 1 = Normal
            1 -> rgMapType.check(R.id.rbNormal)
            2 -> rgMapType.check(R.id.rbSatelite)
            4 -> rgMapType.check(R.id.rbHibrido)
            3 -> rgMapType.check(R.id.rbTerreno)
        }

        // Botão salvar
        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            val zoom = edtZoom.text.toString().toFloatOrNull() ?: 15f

            // Descobre qual foi selecionado
            val mapType = when (rgMapType.checkedRadioButtonId) {
                R.id.rbNormal -> 1
                R.id.rbSatelite -> 2
                R.id.rbHibrido -> 4
                R.id.rbTerreno -> 3
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
