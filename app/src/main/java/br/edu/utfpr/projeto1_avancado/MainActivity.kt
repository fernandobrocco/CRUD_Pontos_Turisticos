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

        listView.setOnItemClickListener { _, _, position, _ ->
            val idSelecionado = ids[position]
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Escolha uma ação")
            builder.setItems(arrayOf("Ver no mapa", "Editar", "Deletar")) { _, which ->
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
                }
            }
            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarLista()
    }

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