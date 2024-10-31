package com.example.facturas

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.facturas.fragments.ClientForm
import com.example.facturas.fragments.ServiceForm

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ClientForm()
            1 -> ServiceForm()
            else -> ClientForm()
        }
    }

    override fun getItemCount(): Int {
        //TODO: Update with new fragments
        return 2
    }

}