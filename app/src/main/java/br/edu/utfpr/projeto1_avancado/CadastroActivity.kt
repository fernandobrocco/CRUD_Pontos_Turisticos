package br.edu.utfpr.projeto1_avancado

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

        val img1: ImageView = findViewById(R.id.img1)
        val img2: ImageView = findViewById(R.id.img2)
        val img3: ImageView = findViewById(R.id.img3)
        val img4: ImageView = findViewById(R.id.img4)

        val imageView: ImageView = findViewById(R.id.imageViewPonto)
        var imagemResId: Int = 0



        pontoId = intent.getIntExtra("id", 0)
        if (pontoId != 0) {
            // Carrega dados do ponto para edição
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

        // Listener para selecionar a imagem
        val clickListener = View.OnClickListener { v ->
            imagemResId = when(v.id) {
                R.id.img1 -> R.drawable.cristo
                R.id.img2 -> R.drawable.torre
                R.id.img3 -> R.drawable.machu_picchu
                R.id.img4 -> R.drawable.coliseu
                else -> 0
            }
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show()
        }

        //carregar imagem do banco de dados
        val cursor = dbHelper.readableDatabase.rawQuery("SELECT imagemPath FROM pontos WHERE id=?", arrayOf(pontoId.toString()))
        if (cursor.moveToFirst()) {
            val resId = cursor.getInt(0) // ID do drawable
            imageView.setImageResource(resId)
        }
        cursor.close()
        // configurar os listeners de clique para as imagens
        img1.setOnClickListener(clickListener)
        img2.setOnClickListener(clickListener)
        img3.setOnClickListener(clickListener)
        img4.setOnClickListener(clickListener)

        // listener para salvar o ponto selecionado
        btnSalvar.setOnClickListener {
            val values = ContentValues()
            values.put("nome", edtNome.text.toString())
            values.put("descricao", edtDesc.text.toString())
            values.put("latitude", edtLat.text.toString().toDouble())
            values.put("longitude", edtLng.text.toString().toDouble())
            values.put("endereco", "")
            values.put("imagemPath", imagemResId.toString())

            // salvar o ponto no banco de dados
            if (pontoId == 0) {
                dbHelper.writableDatabase.insert("pontos", null, values)
                Toast.makeText(this, "Ponto cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                // atualiza o ponto no banco de dados
                dbHelper.writableDatabase.update("pontos", values, "id=?", arrayOf(pontoId.toString()))
                Toast.makeText(this, "Ponto atualizado", Toast.LENGTH_SHORT).show()
            }
            finish()
        }


    }
}