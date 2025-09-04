package br.edu.utfpr.projeto1_avancado

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {
    lateinit var dbHelper: DBHelper
    var pontoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        dbHelper = DBHelper(this)
        val edtNome: EditText = findViewById(R.id.edtNome)
        val edtDesc: EditText = findViewById(R.id.edtDesc)
        val edtLat: EditText = findViewById(R.id.edtLat)
        val edtLng: EditText = findViewById(R.id.edtLng)
        val btnSalvar: Button = findViewById(R.id.btnSalvar)

        pontoId = intent.getIntExtra("id", 0)
        if (pontoId != 0) {
            // Carregar dados do ponto para editar
            val cursor: Cursor = dbHelper.readableDatabase.rawQuery(
                "SELECT * FROM pontos WHERE id=?",
                arrayOf(pontoId.toString())
            )
            if (cursor.moveToFirst()) {
                edtNome.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome")))
                edtDesc.setText(cursor.getString(cursor.getColumnIndexOrThrow("descricao")))
                edtLat.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")).toString())
                edtLng.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")).toString())
            }
            cursor.close()
        }

        btnSalvar.setOnClickListener {
            val values = ContentValues()
            values.put("nome", edtNome.text.toString())
            values.put("descricao", edtDesc.text.toString())
            values.put("latitude", edtLat.text.toString().toDouble())
            values.put("longitude", edtLng.text.toString().toDouble())
            values.put("endereco", "")
            values.put("imagemPath", "")

            if (pontoId == 0) {
                dbHelper.writableDatabase.insert("pontos", null, values)
                Toast.makeText(this, "Ponto cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.writableDatabase.update("pontos", values, "id=?", arrayOf(pontoId.toString()))
                Toast.makeText(this, "Ponto atualizado", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}