package com.example.facturas

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.facturas.fragments.ClientForm
import com.example.facturas.fragments.InvoiceSelectClient
import com.example.facturas.fragments.ServiceForm
import com.example.facturas.fragments.StartCreatingInvoice
import com.example.facturas.fragments.UserForm

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NavHostFragment.create(R.navigation.nav_user)
            1 -> NavHostFragment.create(R.navigation.nav_client)
            2 -> NavHostFragment.create(R.navigation.nav_service)
            else -> NavHostFragment.create(R.navigation.nav_invoice)
        }
    }

}