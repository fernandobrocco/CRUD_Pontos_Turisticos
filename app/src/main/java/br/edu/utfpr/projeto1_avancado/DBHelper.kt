package br.edu.utfpr.projeto1_avancado

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "pontos.db", null, 1) {

    // Cria o banco de dados
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE pontos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT," +
                    "descricao TEXT," +
                    "latitude REAL," +
                    "longitude REAL," +
                    "endereco TEXT," +
                    "imagemPath TEXT)"
        )
    }

    // Atualiza o banco de dados
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pontos")
        onCreate(db)
    }
}