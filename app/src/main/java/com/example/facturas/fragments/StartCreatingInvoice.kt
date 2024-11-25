package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartCreatingInvoice : Fragment() {

    val db = MainApplication.database

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_creating_invoice, container, false)

        val buttonCreateInvoice: Button = view.findViewById(R.id.button_create_invoice)
        buttonCreateInvoice.setOnClickListener {
            checkIfMinimumQueriesExist()
        }

        return view
    }

    private fun checkIfMinimumQueriesExist() {
        Toast.makeText(
            requireContext(),
            "Comprobando si existen consultas mínimas",
            Toast.LENGTH_SHORT
        ).show()
        CoroutineScope(Dispatchers.IO).launch {
            val user = db.personDao().getUser()
            Log.d("StartCreatingInvoice", "User: ${user}")
            val client = db.personDao().getLastClientId()
            Log.d("StartCreatingInvoice", "Clients: ${client}")
            val service = db.serviceDao().getLastServiceId()
            Log.d("StartCreatingInvoice", "Services: ${service}")

            if (user == null || client == 0 || service == 0) {
                Log.d("StartCreatingInvoice", "No existen consultas mínimas")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "No existen consultas mínimas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("StartCreatingInvoice", "Existen consultas mínimas")
                    showDisclaimerDialog()
                }

            }
        }
    }

    private fun showDisclaimerDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aviso")
            .setMessage("No me hago responsable de ningún mal uso que se haga con la app.")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                findNavController().navigate(R.id.action_startCreatingInvoice_to_invoiceSelectClient)
            }
            .show()
    }
}