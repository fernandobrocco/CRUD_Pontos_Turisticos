package br.edu.utfpr.projeto1_avancado

import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var dbHelper: DBHelper
    lateinit var db: SQLiteDatabase
    lateinit var listView: ListView
    val lista = ArrayList<String>()
    val ids = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        db = dbHelper.readableDatabase
        listView = findViewById(R.id.listView)

        findViewById<android.widget.Button>(R.id.btnAdd).setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }

        findViewById<android.widget.Button>(R.id.btnMapa).setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }

        findViewById<android.widget.Button>(R.id.btnConfig).setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }

        // modal de seleção de ação quando clica em cima de um ponto
        listView.setOnItemClickListener { _, _, position, _ ->
            val idSelecionado = ids[position]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Escolha uma ação")
            builder.setItems(
                arrayOf(
                    "Ver no mapa",
                    "Editar",
                    "Deletar",
                    "Converter para endereço"
                )
            ) { _, which ->
                when (which) {
                    0 -> { // Ver no mapa
                        val intent = Intent(this, MapaActivity::class.java)
                        intent.putExtra("id", idSelecionado)
                        startActivity(intent)
                    }
                    1 -> { // Editar
                        val intent = Intent(this, CadastroActivity::class.java)
                        intent.putExtra("id", idSelecionado)
                        startActivity(intent)
                    }
                    2 -> { // Deletar
                        db.delete("pontos", "id=?", arrayOf(idSelecionado.toString()))
                        Toast.makeText(this, "Ponto deletado", Toast.LENGTH_SHORT).show()
                        carregarLista()
                    }
                    3 -> { // Converter geocódigo(lat e lng) para endereço textual
                        val cursor = db.rawQuery(
                            "SELECT latitude, longitude FROM pontos WHERE id = ?",
                            arrayOf(idSelecionado.toString())
                        )
                        if (cursor.moveToFirst()) {
                            val lat = cursor.getDouble(0)
                            val lng = cursor.getDouble(1)

                            val geocoder =
                                android.location.Geocoder(this, java.util.Locale.getDefault())
                            try {
                                val addresses = geocoder.getFromLocation(lat, lng, 1)
                                // verifica se o endereço foi encontrado
                                if (!addresses.isNullOrEmpty()) {
                                    val endereco = addresses[0].getAddressLine(0)

                                    AlertDialog.Builder(this)
                                        .setTitle("Endereço encontrado")
                                        .setMessage(endereco)
                                        .setPositiveButton("OK", null)
                                        .show()
                                } else {
                                    // se o endereço não foi encontrado, mostra uma mensagem de erro
                                    Toast.makeText(
                                        this,
                                        "Endereço não encontrado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                // se ocorrer um erro ao buscar o endereço, mostra uma outra mensagem de erro
                                e.printStackTrace()
                                Toast.makeText(this, "Erro ao buscar endereço", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        cursor.close()
                    }
                }
            }
            builder.show()
        }
    }

    // atualiza a lista quando a atividade é reaberta
    override fun onResume() {
        super.onResume()
        carregarLista()
    }

    // carrega a lista de pontos cadastrados
    private fun carregarLista() {
        lista.clear()
        ids.clear()
        val cursor: Cursor = db.rawQuery("SELECT id, nome FROM pontos", null)
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0))
                lista.add(cursor.getString(1))
            } while (cursor.moveToNext())
        }
        cursor.close()
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista)
    }
}