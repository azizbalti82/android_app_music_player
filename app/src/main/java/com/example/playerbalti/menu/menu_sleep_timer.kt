package com.example.playerbalti.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuSleepTimerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class menu_sleep_timer : BottomSheetDialogFragment() {
    lateinit var b:MenuSleepTimerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSleepTimerBinding.inflate(inflater,container,false)

        //set values:
        data.remainingTime = b.remainTime

        //set listeners
        b.off.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.countdownTimer?.cancel()
                b.remainTime.text = ""
            }
        }
        b.min5.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(5)
            }
        }
        b.min10.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(10)
            }
        }
        b.min20.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(20)
            }
        }
        b.min30.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(30)
            }
        }
        b.min40.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(40)
            }
        }
        b.min60.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(60)
            }
        }
        b.min120.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                data.startSleepTimer(120)
            }
        }

        return b.root
    }
}