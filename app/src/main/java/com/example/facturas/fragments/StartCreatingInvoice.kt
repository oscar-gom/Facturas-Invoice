package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.facturas.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StartCreatingInvoice : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_creating_invoice, container, false)

        val buttonCreateInvoice: Button = view.findViewById(R.id.button_create_invoice)
        buttonCreateInvoice.setOnClickListener {
            showDisclaimerDialog()
        }

        return view
    }

    private fun showDisclaimerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aviso")
            .setMessage("No me hago responsable de ningún mal uso que se haga con la app.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}