package com.example.mirastroenelmapa

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mirastroenelmapa.Constantes.INTERVAL_TIME
import com.example.mirastroenelmapa.Constantes.lapaz

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.mirastroenelmapa.databinding.ActivityGoogleMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener{

    companion object {
        val REQUIERED_PERMISSION_GPS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private var isGPSEnabled = false
    private val PERMISSION_ID = 42
    //Variable que vamos a usar para gestionar el GPS con google play services
    //FusedLocation: fusionar los datos respectivos a GPS en un objeto
    private lateinit var fusedLocation : FusedLocationProviderClient

    private var contador: Int = 0
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var misRutas: MutableList<LatLng>
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

        binding.btnHabilitarGPS.setOnClickListener {
            enableGPSServices()
        }
        binding.btnHabilitarCoor.setOnClickListener {
            manageLocation()
        }
        binding.btnRecorrido.setOnClickListener {
            setupPolyline(misRutas)
        }
    }

    //Función para cuando el mapa este listo
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))

        mMap.apply {
            setMinZoomPreference(13f)
            setMaxZoomPreference(20f)
        }

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lapaz,10f))

        mMap.setPadding(0,0,0,Utils.dp(64))// densidad de pixeles en pantalla

        /**
         * Estilo personalizado de mapa
         */

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.my_map_style))

        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener {
            //it es la posición donde haces click con tu dedo
            mMap.addMarker(MarkerOptions()
                .title("Nueva ubicación Random")
                .snippet("${it.latitude},\n${it.longitude}")
                .position(it)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
            )
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        //marker es el marcador al que le estas haciendo click
        Toast.makeText(this, "${marker.position.latitude}, ${marker.position.longitude}", Toast.LENGTH_LONG).show()
        return false
    }

    //HABILITAR PERMISOS DE GPS Y COORDENADAS

    /**
     *
     *
     * GPS
     *
     *
     */

    private fun enableGPSServices() {
        if(!hasGPSEnabed()){
            AlertDialog.Builder(this)
                .setTitle(R.string.alert_dialog_title)
                .setMessage(R.string.alert_dialog_description)
                .setPositiveButton(
                    R.string.alert_dialog_button_accept,
                    DialogInterface.OnClickListener{
                            dialog, wich -> goToEnableGPS()
                    })
                .setNegativeButton(R.string.alert_dialog_button_denny) {
                        dialog, wich -> isGPSEnabled = false
                }
                .setCancelable(true)
                .show()
        } else
            Toast.makeText(this,"Ya tienes el GPS habilitado", Toast.LENGTH_SHORT).show()
    }

    private fun hasGPSEnabed(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun goToEnableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    //Seccion: tratamiento de permisos para el uso del GPS
    private fun allPermissionsGrantedGPS(): Boolean {
        return REQUIERED_PERMISSION_GPS.all {
            ContextCompat.checkSelfPermission( baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     *
     *
     * Coordenadas
     *
     *
     * */

    @SuppressLint("MissingPermission")
    private fun manageLocation() {
        if (hasGPSEnabed()){
            if (allPermissionsGrantedGPS()) {
                //solo puede ser tratado si el usuario dio permisos
                fusedLocation = LocationServices.getFusedLocationProviderClient(this)
                //Estan configurando un evento que escuche cuando
                // del sensor GPS se captura datos correctamente
                fusedLocation.lastLocation.addOnSuccessListener {
                        location -> requestNewLocationData()
                }
            }else{
                requestPermissionsLocation()
            }
        }else{
            goToEnableGPS()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var myLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME//10 segundos = 10000 milisegundos
        ).setMaxUpdates(50).build()
        fusedLocation.requestLocationUpdates(myLocationRequest, myLocationCallback, Looper.myLooper())
    }

    private fun requestPermissionsLocation() {
        ActivityCompat.requestPermissions(this, REQUIERED_PERMISSION_GPS, PERMISSION_ID)
    }

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var myLastLocation: Location? = locationResult.lastLocation
            if(myLastLocation != null) {
                var lastLatitude = myLastLocation.latitude
                var lastLongitude = myLastLocation.longitude

                if(contador > 0) {
                    binding.apply {
                        mMap.addMarker(MarkerOptions()
                            .title("Ubicación ${contador}")
                            .snippet("${lastLatitude},${lastLongitude}")
                            .position(LatLng(lastLatitude,lastLongitude))
                        )
                    }
                    misRutas.add(LatLng(lastLatitude,lastLongitude))
                }

                latitud = myLastLocation.latitude
                longitud = myLastLocation.longitude
                contador++
            }
        }
    }

    //Polylines

    private fun setupPolyline(ruta: MutableList<LatLng>) {
        //las líneas Polyline dependen de un arreglo o lista de coordenadas
        val polyline = mMap.addPolyline(
            PolylineOptions()
            .color(Color.BLUE)
            .width(10f) //ancho de la línea
            .clickable(true) //la línea debe ser clickeada
            .geodesic(true) //curvatura con respecto al radio de la tierra
        )
        polyline.points = ruta

        lifecycleScope.launch{
            val misRutasEnTiempoReal = mutableListOf<LatLng>()
            for (punto in ruta){
                misRutasEnTiempoReal.add(punto)
                polyline.points = misRutasEnTiempoReal
                delay(2_000)
            }
        }
    }
}