package br.edu.utfpr.projeto1_avancado

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class CadastroActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var pontoId: Int = 0
    private var imagemUri: Uri? = null
    private var imagemPathInterna: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        dbHelper = DBHelper(this)

        val edtNome: EditText = findViewById(R.id.edtNome)
        val edtDesc: EditText = findViewById(R.id.edtDesc)
        val edtLat: EditText = findViewById(R.id.edtLat)
        val edtLng: EditText = findViewById(R.id.edtLng)
        val btnImportar: Button = findViewById(R.id.btnImportar)
        val btnSalvar: Button = findViewById(R.id.btnSalvar)
        val img: ImageView = findViewById(R.id.img)

        pontoId = intent.getIntExtra("id", 0)

        // Restaurar ponto existente
        if (pontoId != 0) {
            val cursor: Cursor = dbHelper.readableDatabase.rawQuery(
                "SELECT * FROM pontos WHERE id=?",
                arrayOf(pontoId.toString())
            )
            // Verifica se o ponto existe
            if (cursor.moveToFirst()) {
                edtNome.setText(cursor.getString(cursor.getColumnIndexOrThrow("nome")))
                edtDesc.setText(cursor.getString(cursor.getColumnIndexOrThrow("descricao")))
                edtLat.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")).toString())
                edtLng.setText(cursor.getDouble(cursor.getColumnIndexOrThrow("longitude")).toString())
                imagemPathInterna = cursor.getString(cursor.getColumnIndexOrThrow("imagemPath"))

                // Carrega imagem do armazenamento interno
                if (!imagemPathInterna.isNullOrEmpty()) {
                    val file = File(imagemPathInterna!!)
                    if (file.exists()) {
                        img.setImageURI(Uri.fromFile(file))
                    }
                }
            }
            cursor.close()
        }

        // Launcher para escolher imagem da galeria
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    // Copia a imagem para armazenamento interno
                    val nomeArquivo = "imagem_ponto_${System.currentTimeMillis()}.jpg"
                    val file = File(filesDir, nomeArquivo)
                    contentResolver.openInputStream(selectedImageUri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    imagemPathInterna = file.absolutePath
                    img.setImageURI(Uri.fromFile(file))
                }
            }
        }

        /* // Valida se o campo esta vazio e para a função
        fun validaCampoVazio(campo: EditText, mensagem: String) {
            if (campo.text.toString().isEmpty()) {
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                return
            }
        }*/

        // Botão importar abre galeria
        btnImportar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // Botão salvar
        btnSalvar.setOnClickListener {

            //converte para string e retira os espaços
            val nome = edtNome.text.toString().trim()
            val desc = edtDesc.text.toString().trim()
            val latStr = edtLat.text.toString().trim()
            val lngStr = edtLng.text.toString().trim()

            //valida se os campos estao vazios e para a função
            if (nome.isEmpty() || desc.isEmpty() || latStr.isEmpty() || lngStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //converte para double e se nao for possivel retorna 0.0
            val lat = latStr.toDoubleOrNull() ?: 0.0
            val lng = lngStr.toDoubleOrNull() ?: 0.0

            // Salva ponto no banco de dados
            val values = ContentValues().apply {
                put("nome", nome)
                put("descricao", desc)
                put("latitude", lat)
                put("longitude", lng)
                put("endereco", "")
                put("imagemPath", imagemPathInterna ?: "")
            }
            // Verifica se é um novo ponto(cadastra) ou um ponto existente(atualiza)
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
