package com.example.app

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.menuprincipal.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_history -> {
                    // Ação ao clicar em Histórico
                }
                R.id.nav_map -> {
                    // Ação ao clicar em Mapa
                }
                R.id.nav_camera -> {
                    // Ação ao clicar em Câmera
                }
            }
            true
        }

        // Configurar botão de menu lateral
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            // Lógica para abrir menu lateral
        }
    }
}
