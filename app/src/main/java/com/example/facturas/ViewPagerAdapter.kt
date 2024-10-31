package com.example.facturas

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.facturas.fragments.ClientForm
import com.example.facturas.fragments.ServiceForm
import com.example.facturas.fragments.UserForm

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserForm()
            1 -> ClientForm()
            2 -> ServiceForm()
            else -> UserForm()
        }
    }

    override fun getItemCount(): Int {
        //TODO: Update with new fragments
        return 3
    }

}