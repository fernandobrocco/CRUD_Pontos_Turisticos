package br.edu.utfpr.projeto1_avancado

import android.database.Cursor
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

// atividade relacionada ao mapeamento dos pontos cadastrados
class MapaActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var dbHelper: DBHelper
    lateinit var mMap: GoogleMap

    //criação da atividade
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        dbHelper = DBHelper(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //carregamento do mapa
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val prefs = getSharedPreferences("config", MODE_PRIVATE) // pega preferencias na configActivity
        val zoomConfig = prefs.getFloat("zoom", 15f) // pega o zoom do mapa na configActivity
        val mapType = prefs.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL) // define o tipo de mapa escolhido na configActivity
        mMap.mapType = mapType

        val idSelecionado = intent.getIntExtra("id", -1)

        val db = dbHelper.readableDatabase
        val cursor: Cursor = if (idSelecionado != -1) {
            //seleciona um ponto especifico
            db.rawQuery(
                "SELECT nome, descricao, latitude, longitude FROM pontos WHERE id = ?",
                arrayOf(idSelecionado.toString())
            )
        } else {
            //seleciona todos os pontos
            db.rawQuery(
                "SELECT nome, descricao, latitude, longitude FROM pontos",
                null
            )
        }

        val geocoder = Geocoder(this, Locale.getDefault())

        if (cursor.moveToFirst()) {
            do {
                val nome = cursor.getString(0)
                var desc = cursor.getString(1)
                val lat = cursor.getDouble(2)
                val lng = cursor.getDouble(3)
                val ponto = LatLng(lat, lng)

                // Tenta converter latitude e longitude para endereço textual
                try {
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        desc = address.getAddressLine(0) // endereço completo
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // adiciona marcador no mapa
                mMap.addMarker(
                    MarkerOptions()
                        .position(ponto)
                        .title(nome)
                        .snippet(desc)
                )

                // move a câmera para o ponto do mapa
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ponto, zoomConfig))

            } while (cursor.moveToNext())
        }

        cursor.close()
    }
}