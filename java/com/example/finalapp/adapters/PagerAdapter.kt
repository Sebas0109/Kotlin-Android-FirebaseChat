package com.example.finalapp.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class PagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm)
{
    private val fragmentList = ArrayList<Fragment>()

    override fun getItem(p0: Int): Fragment  = fragmentList[p0]

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment : Fragment)
    {
        fragmentList.add(fragment)
    }
}