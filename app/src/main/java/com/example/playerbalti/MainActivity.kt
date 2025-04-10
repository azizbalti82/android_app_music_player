package com.example.playerbalti

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.playerbalti.adapters.myAdapter
import com.example.playerbalti.databinding.ActivityMainBinding
import com.example.playerbalti.menu.menu_tabs_selector_adapter
import com.example.playerbalti.more.MoreAdapter
import com.example.playerbalti.otherActivities.permissions
import com.example.playerbalti.otherActivities.playerActivity
import com.example.playerbalti.otherActivities.search
import com.example.playerbalti.sections.albums
import com.example.playerbalti.sections.artists
import com.example.playerbalti.sections.folders
import com.example.playerbalti.sections.genres
import com.example.playerbalti.sections.playlists
import com.example.playerbalti.sections.songs
import com.example.playerbalti.storage.db_manager
import com.example.playerbalti.storage.shared
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
    lateinit var b: ActivityMainBinding
    private lateinit var mediaStoreObserver: MediaStoreObserver
    var startTime:Long = 0

    override fun onDestroy() {
        super.onDestroy()
        mediaStoreObserver.stopObserving()
        /*
        //save queue -------------------------------------------------------------------------------
        db_manager.clear_queue(this)
        db_manager.addAll_queue(this)
        //save last played song index
        shared.set(this,"player","lastSongIndex",data.queueSelectedSong)
         */
    }





    override fun onResume() {
        super.onResume()
        data.context = this
        Log.d(TAG, "app started: ${System.currentTimeMillis()-startTime} ms")
        data.miniPlayerStatue("initialize")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        data.context = this
        data.main = this

        startTime = System.currentTimeMillis()

        //load settings from SharedPreferences -----------------------------------------------------
        shared.shuffle = shared.getBoolean(this,"player","shuffle",false)
        shared.repeat = shared.getString(this,"player","repeat","always")
        //sort settings
        shared.genres_sort_type =shared.getString(this,"sort","genres_sort_type","name-asc")
        shared.folders_sort_type=shared.getString(this,"sort","folders_sort_type","name-asc")
        shared.artists_sort_type=shared.getString(this,"sort","artists_sort_type","name-asc")
        shared.albums_sort_type =shared.getString(this,"sort","albums_sort_type","name-asc")
        shared.songs_sort_type  =shared.getString(this,"sort","songs_sort_type","name-asc")
        //settings (audio):
        shared.allow_30_sec  =shared.getBoolean(this,"settingsAudio","allow_30_sec",false)
        shared.pause_when_mute  =shared.getBoolean(this,"settingsAudio","pause_when_mute",true)
        shared.lower_volume  =shared.getBoolean(this,"settingsAudio","lower_volume",true)
        //settings (general):
        shared.lastAddedInterval  =shared.getInt(this,"settingsGeneral","lastAddedInterval",1)
        shared.rememberLastTab  =shared.getBoolean(this,"settingsGeneral","rememberLastTab",false)
        shared.language  =shared.getString(this,"settingsGeneral","language","english")
        shared.lastTab  =shared.getInt(this,"settingsGeneral","lastTab",0)
        shared.hiddenTabs  =shared.getString(this,"settingsGeneral","hiddenTabs","")
        shared.tabsOrder  =shared.getString(this,"settingsGeneral","tabsOrder","songs|albums|artists|playlists|folders|genres")
        //update starting times
        shared.set(this,"statistics","open_count",shared.open_count+1)


        //start foreground service
        val intent = Intent(this,RunningService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }else{
            startService(intent)
        }

        // choose theme
        data.context = applicationContext
        setTheme(R.style.mainTheme)
        setContentView(b.root) //-------------------------------------------------------------------


        //observe for media changing
        mediaStoreObserver = MediaStoreObserver(this, Handler())
        mediaStoreObserver.startObserving()

        //save mini payer views
        data.miniplayer = b.miniPlayer
        data.miniplayer_img = b.miniplayerImg
        data.miniplayer_song = b.miniplayerSong
        data.miniplayer_playpause = b.miniPlayerPlayPause
        data.miniplayer_next = b.miniplayerNext
        data.progress = b.progress

        //update mini player statue (set it invisible)
        data.miniPlayerStatue("")

        //test for permissions
        testPermisson()

        //save task bar for later use
        data.taskbar = b.taskbar

        //You have the permission to write to audio media

        initialize_tabs()

        //user playlists
        val names = db_manager.getPlaylistsNames(this) //playlists names
        for (name in names){
            val songs = db_manager.read(this,name)
            data.songsPlaylists.add(playlist(name, songs.size, songs))
        }



        //set listeners: -------------------------------------***---------**-----------------**-----
        b.moreButton.setOnClickListener {
            val MoreAdapter = MoreAdapter()
            MoreAdapter.show(supportFragmentManager, MoreAdapter.tag)
        }

        b.searchButton.setOnClickListener{
            val intent = Intent(this, search::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.little_scale,0)
        }

        //------------------------------------------------------------------------------ mini player
        b.miniplayerNext.setOnClickListener{
            //next song button
            data.queueSelectedSong++
            data.miniPlayerStatue("stop_play")
        }

        b.miniPlayerPlayPause.setOnClickListener{
            data.miniPlayerStatue("pause_play")
        }
        data.mediaPlayer?.setOnCompletionListener {
            // This code will be executed when the song completes
            data.queueSelectedSong++
            data.miniPlayerStatue("stop_play")
        }
        b.miniPlayerContainer.setOnClickListener{
            try{
                val intent = Intent(this, playerActivity::class.java)
                startActivity(intent)
                //Apply the animatio
                overridePendingTransition(R.anim.slide_up_activity, R.anim.no_animation)
            }catch(e:Exception){
                Log.e("more click", "error in onclick in main activity: \n$e")
            }
        }


    }

    private fun initialize_tabs() {
        // Initialize TabLayout and ViewPager from the binding
        var tabLayout = b.tabLayout
        var viewPager = b.viewPager

        // Add tabs to the TabLayout
        val tabs = data.get_tabs() //get tabs in order
        val tabs_classes = ArrayList<Fragment>()
        tabs.removeAll(data.get_hidden_tabs().toSet())
        for(i in tabs){
            tabLayout.addTab(tabLayout.newTab().setText(i))
            //also get the class of that tab
            if(i == "songs"){
                tabs_classes.add(songs())
            }else if(i == "albums"){
                tabs_classes.add(albums())
            }else if(i == "artists"){
                tabs_classes.add(artists())
            }else if(i == "playlists"){
                tabs_classes.add(playlists())
            }else if(i == "folders"){
                tabs_classes.add(folders())
            }else if(i == "genres"){
                tabs_classes.add(genres())
            }
        }
        // Set up the adapter for the ViewPager
        val adapter = myAdapter(this,supportFragmentManager, tabLayout.tabCount,tabs_classes)
        viewPager.adapter = adapter

        // Attach a listener to the ViewPager to sync with TabLayout changes
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        //if "remember_last_tab" option is true:
        if(shared.rememberLastTab){
            viewPager.currentItem = shared.lastTab
        }
        // Set a listener for tab selection events
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @SuppressLint("ResourceType", "MissingInflatedId")
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
                shared.set(adapter.context,"settingsGeneral","lastTab", tab.position)
            }
            @SuppressLint("MissingInflatedId")
            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //when the tab is already selected and you select it again: open tabs selector
                val adapter = menu_tabs_selector_adapter()
                adapter.show(supportFragmentManager, adapter.tag)

            }
        })
    }

    private fun testPermisson() {
        //1st thing we gonna see if the permission is granted:
        Log.d(TAG, "test for permissions")
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Log.d(TAG, "permission not ok: storage: sdk<10")
            //for sdk < 10
            if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //(permission not granted)
                permissionsActivity()
            }
        } else if(ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //for android >= 10 (storage permission)
            permissionsActivity()
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(applicationContext,android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            //for android >= 13 (notification permission)
            Log.d(TAG, "permission not ok: foreground")
            permissionsActivity()
        }else{
            //permission granted
            Log.d(TAG, "permission OK")

            //data.getAllSongs(this,data.defaultSort)

            lifecycleScope.launch {
                val time = measureTimeMillis {
                    // data.updateLists(applicationContext)
                    data.getAllSongs(applicationContext)

                    val a2 = async { data.getDirectories() }
                    val a3 = async { data.getAlbums() }
                    val a4 = async { data.getGenres() }
                    val a5 = async { data.getArtists() }

                    // Await all coroutines to complete
                    a2.await()
                    a3.await()
                    a4.await()
                    a5.await()
                }
                Log.d(TAG, "songs scanned in: $time ms")
            }
        }
    }
    private fun permissionsActivity() {
        overridePendingTransition(0, 0)// Disable transition animations
        val intent = Intent(this, permissions::class.java)
        startActivity(intent)
    }

    companion object{
        val TAG = "mainActivity"
    }
}


