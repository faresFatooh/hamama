package com.p1.uberfares.helper

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.p1.uberfares.R
import com.p1.uberfares.fragment.Fragment3
import com.p1.uberfares.fragment.Fragment4

class PageAdapterC(fm: FragmentManager,context: Context) : FragmentPagerAdapter(fm) {
    val context = context
    override fun getCount(): Int {
        return 2;
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return Fragment3()
            }
            1 -> {
                return Fragment4()
            }
            else -> {
                return Fragment3()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return context.getString(R.string.trip_history)
            }
            1 -> {
                return context.getString(R.string.restaurants)
            }
        }
        return super.getPageTitle(position)
    }
}