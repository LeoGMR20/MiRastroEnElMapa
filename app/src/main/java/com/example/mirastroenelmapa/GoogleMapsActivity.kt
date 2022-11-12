package com.example.mirastroenelmapa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mirastroenelmapa.databinding.ActivityGoogleMapsBinding

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGoogleMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //Función para cuando el mapa este listo
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        //Marcador
        //Tachuela roja que se posiciona en el mapa donde quieren ubicarse
        /*mMap.addMarker(MarkerOptions()
            .title("Salar de Uyuni")
            .snippet("${salarUyuni.latitude},${salarUyuni.longitude}") //Contenido extra
            .position(salarUyuni)
        )*/

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true // Botones + - zoom in zoom out
            isCompassEnabled = true // la brújula de orientación del mapa
            isMapToolbarEnabled = true // habilito para un marcador la opción de ir a ver una ruta a verlo en la app Mapa Google
            isRotateGesturesEnabled = false // deshabilitar la opción de rotación del mapa
            isTiltGesturesEnabled = false // deshabilitar la opción de rotación de la cámara
            isZoomControlsEnabled = false // deshabilita las opciones de zoom con los dedos del mapa
        }
    }
}