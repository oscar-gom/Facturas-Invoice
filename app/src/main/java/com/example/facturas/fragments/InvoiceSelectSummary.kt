package com.example.facturas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceSelectSummary : Fragment() {
    private val args: InvoiceSelectSummaryArgs by navArgs()
    private val db = MainApplication.database
    private var client: Person? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice_select_summary, container, false)

        val clientName = view.findViewById<TextView>(R.id.client_name)
        val clientNif = view.findViewById<TextView>(R.id.client_nif)

        getClient(clientName, clientNif)

        return view
    }

    private fun getClient(clientName: TextView, clientNif: TextView) {
        val clientId = args.clientId

        CoroutineScope(Dispatchers.IO).launch {
            client = db.personDao().getPersonById(clientId)
            withContext(Dispatchers.Main) {
                clientName.text = client?.name
                clientNif.text = client?.fiscalNumber
            }
        }
    }
}