package com.example.mirastroenelmapa

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import com.example.mirastroenelmapa.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {
    companion object {
        val REQUIERED_PERMISSION_GPS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    }

    private lateinit var binding: ActivityMainBinding
    private var isGPSEnabled = false
    private val PERMISSION_ID = 42
    //Variable que vamos a usar para gestionar el GPS con google play services
    //FusedLocation: fusionar los datos respectivos a GPS en un objeto
    private lateinit var fusedLocation : FusedLocationProviderClient

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var distance: Double = 0.0
    private var acumulateDistance: Double = 0.0
    private var velocity: Double = 0.0
    private var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /*@SuppressLint("MissingPermission")
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

    //OJO: solo usar si estas completamente seguro de que en este punto
    //garantizas al 100% que el usuario ha dado permisos
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        //configurar las caracteristicas de nuestra peticion de localizacion
        //Version 21 y su nueva manera de configurar un request
        var myLocationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            INTERVAL_TIME
        ).setMaxUpdates(50).build()
        fusedLocation.requestLocationUpdates(myLocationRequest, myLocationCallback, Looper.myLooper())
    }*/
}