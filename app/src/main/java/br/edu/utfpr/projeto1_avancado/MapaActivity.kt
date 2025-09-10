package br.edu.utfpr.projeto1_avancado

import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var dbHelper: DBHelper
    lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        dbHelper = DBHelper(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //Recupera configurações do SharedPreferences
        val prefs = getSharedPreferences("config", MODE_PRIVATE)
        val zoomConfig = prefs.getFloat("zoom", 15f)
        val mapType = prefs.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL)
        mMap.mapType = mapType

        // Lê pontos do banco
        val cursor: Cursor = dbHelper.readableDatabase.rawQuery(
            "SELECT nome, descricao, latitude, longitude FROM pontos", null
        )

        if (cursor.moveToFirst()) {
            do {
                val nome = cursor.getString(0)
                val desc = cursor.getString(1)
                val lat = cursor.getDouble(2)
                val lng = cursor.getDouble(3)
                val ponto = LatLng(lat, lng)

                mMap.addMarker(
                    MarkerOptions()
                        .position(ponto)
                        .title(nome)
                        .snippet(desc)
                )

                // 🔹 Centraliza no último ponto lido (mantendo seu comportamento atual)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ponto, zoomConfig))

            } while (cursor.moveToNext())
        }

        cursor.close()
    }
}