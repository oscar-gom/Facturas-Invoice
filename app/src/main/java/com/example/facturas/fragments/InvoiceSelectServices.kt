package com.example.facturas.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.facturas.R


class InvoiceSelectServices : Fragment() {
    private val args: InvoiceSelectServicesArgs by this.navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val clientId = args.clientId
        Log.d("InvoiceSelectServices", "Client ID: $clientId")

        return inflater.inflate(R.layout.fragment_invoice_select_services, container, false)
    }

}