package com.example.facturas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facturas.models.Invoice
import com.example.facturas.models.Service

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FAKE DATA
        val service1 = Service("Camera", 399.99, 0.0, 1, 0.21)
        val service2 = Service("Tripod", 49.99, 0.25, 1, 0.21)

        val invoice = Invoice(
            "2024-0001",
            "2024-10-28",
            "2024-11-29",
            listOf(service1, service2),
            "ES6621000418401234567891"
        )
    }
}