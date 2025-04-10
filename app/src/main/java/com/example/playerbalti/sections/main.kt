package com.example.playerbalti.sections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playerbalti.databinding.FragmentMainBinding

class main : Fragment() {
    lateinit var b: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        b = FragmentMainBinding.inflate(inflater,container,false)
        return b.root
    }
}