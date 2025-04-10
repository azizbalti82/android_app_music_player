package com.example.playerbalti.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class shared {
    companion object{
        val TAG = "storage"

        //values
        var repeat = "always"
        var shuffle = false
        var open_count = 0 //the number of times the app opened
        var lastSongIndex = 0 //the queue index of last played song

        var genres_sort_type="name-asc" //ask:croissant | desc:decroissant
        var folders_sort_type="name-asc"
        var artists_sort_type="name-asc"
        var albums_sort_type="name-asc"
        var songs_sort_type="name-asc"

        //audioSettings
        var allow_30_sec = false //allow short songs (lower than 30sec)
        var pause_when_mute = true //when the sound volume is 0 stop the song
        var lower_volume = true //lower volume on notification

        //generalSettings
        var lastAddedInterval = 1 //1: 1 month
        var rememberLastTab = false //remember last tab
        var hiddenTabs = "" // A LIST of hidden tabs splitted buy '|' exemple: album|folder|playlist
        var tabsOrder = "songs|albums|artists|playlists|folders|genres" //a list of tabs names ordered
        var lastTab = 0 //index of last opened tab
        var language = "english" //app language




        //set  values in "SharedPreferences" -------------------------------------------------------
        fun set(c:Context?,parent:String,key:String,value:String){
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val editor = share.edit()
            editor.putString(key,value)
            editor.apply()

            //update variable's value:
            when(key){
                "repeat"  -> repeat = value
                "genres_sort_type" -> genres_sort_type=value
                "folders_sort_type" -> folders_sort_type=value
                "artists_sort_type" -> artists_sort_type=value
                "albums_sort_type" -> albums_sort_type=value
                "songs_sort_type" -> songs_sort_type=value
                "language" -> language = value
                "hiddenTabs" -> hiddenTabs = value
                "tabsOrder" -> tabsOrder = value
            }

            Log.d(TAG, "added to shared preferences: parent:$parent | key:$key | value:$value")
        }
        //boolean version
        fun set(c:Context?,parent:String,key:String,value:Boolean){
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val editor = share.edit()
            editor.putBoolean(key,value)
            editor.apply()

            //update variable's value:
            when(key){
                "shuffle" -> shuffle = value
                "allow_30_sec" -> allow_30_sec = value
                "pause_when_mute" -> pause_when_mute = value
                "lower_volume" -> lower_volume = value
                "rememberLastTab" -> rememberLastTab = value
            }

            Log.d(TAG, "added to shared preferences: parent:$parent | key:$key | value:$value")
        }
        //int version
        fun set(c:Context?,parent:String,key:String,value:Int){
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val editor = share.edit()
            editor.putInt(key,value)
            editor.apply()

            //update variable's value:
            when(key){
                "open_count" -> open_count = value
                "lastSongIndex"-> lastSongIndex=value
                "lastAddedInterval" -> lastAddedInterval = value
                "lastTab" -> lastTab = value
            }

            Log.d(TAG, "added to shared preferences: parent:$parent | key:$key | value:$value")
        }

        //get values in "SharedPreferences" -------------------------------------------------------
        fun getString(c:Context?,parent:String,key:String,default:String) : String{
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val value = share.getString(key,default).toString()
            Log.d(TAG, "got from shared preferences: parent:$parent | key:$key | value:$value")
            return value
        }
        //boolean version
        fun getBoolean(c:Context?,parent:String,key:String,default:Boolean) : Boolean{
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val value = share.getBoolean(key,default)
            Log.d(TAG, "got from shared preferences: parent:$parent | key:$key | value:$value")
            return value
        }
        //int version
        fun getInt(c:Context?,parent:String,key:String,default:Int) : Int{
            val share: SharedPreferences = c!!.getSharedPreferences(parent,Context.MODE_PRIVATE)
            val value = share.getInt(key,default)
            Log.d(TAG, "got from shared preferences: parent:$parent | key:$key | value:$value")

            return value
        }
    }
}