package com.example.mirastroenelmapa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mirastroenelmapa.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient

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
}