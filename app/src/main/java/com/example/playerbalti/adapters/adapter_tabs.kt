package com.example.playerbalti.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.playerbalti.sections.albums
import com.example.playerbalti.sections.artists
import com.example.playerbalti.sections.folders
import com.example.playerbalti.sections.genres
import com.example.playerbalti.sections.playlists
import com.example.playerbalti.sections.songs

internal class myAdapter( var context:Context, fm:FragmentManager, var totalTabs:Int,val tabs:ArrayList<Fragment>): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return tabs[position]
    }

    override fun getCount(): Int {
        return totalTabs
    }
}