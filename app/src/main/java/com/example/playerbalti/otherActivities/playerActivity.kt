package com.example.playerbalti.otherActivities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.PlayerAdapter
import com.example.playerbalti.album
import com.example.playerbalti.artist
import com.example.playerbalti.content.albumContent
import com.example.playerbalti.content.artistContent
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityPlayerBinding
import com.example.playerbalti.menu.menu_queue_adapter
import com.example.playerbalti.menu.menu_song_adapter
import com.example.playerbalti.storage.db_manager
import com.example.playerbalti.storage.db_manager.Companion.add_Favorites
import com.example.playerbalti.storage.db_manager.Companion.exist_Favorites
import com.example.playerbalti.storage.db_manager.Companion.remove_Favorites
import com.example.playerbalti.storage.shared


class playerActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        updateUi()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set companion variables
        b = ActivityPlayerBinding.inflate(layoutInflater)
        frag_manager = supportFragmentManager
        c = this
        data.player_activity = this
        //set view
        setContentView(b.root)

        song = data.queue[data.queueSelectedSong]
        updateUi()

        //send views to "data":
        data.player_seekbar = b.seekBar //to connect it to the progress bar
        data.player_playpause = b.play
        data.player_played_time = b.playedTime
        data.player_duration = b.duration
        data.player_album = b.album
        data.player_title = b.title
        data.player_artist = b.artist

        data.setup_players_data()

        if (data.mediaPlayer?.isPlaying == true) {
            b.play.setImageDrawable(this.getDrawable(R.drawable.pause_circle))
        }

        //set listeners: ---------------------------------------------------------------------------
        b.cancel.setOnClickListener {
            this.finish()
            //Apply the animation
            overridePendingTransition(0, R.anim.slide_down_activity)
        }

        b.next.setOnClickListener {
            //next song button
            data.queueSelectedSong++
            data.miniPlayerStatue("stop_play")
        }
        b.previous.setOnClickListener {
            val duration = data.getCurrentSongPlayedTime()
            if (duration != null && ((duration / 1000) < 5 && data.queueSelectedSong > 0)) {
                data.queueSelectedSong--
                data.miniPlayerStatue("stop_play")
            } else {
                //restart song
                data.miniPlayerStatue("stop_play")
            }
        }
        b.queue.setOnClickListener {
            val adapter = menu_queue_adapter()
            adapter.show(supportFragmentManager, adapter.tag)
        }
        b.play.setOnClickListener {
            data.miniPlayerStatue("pause_play")
            db_manager.add_Recent(this, song.id)
        }

        b.like.setOnClickListener {
            Log.d("storage", "current id=${song.id}")
            if(exist_Favorites(this, song.id)){
                //if exist remove
                val check = remove_Favorites(this, song.id)
                if(check){
                    data.changeButtonIconColor(this, b.like, R.drawable.heart, R.color.text2)
                }
            }else{
                //add to favorites
                val check = add_Favorites(this, song.id)
                if(check){
                    data.changeButtonIconColor(this, b.like, R.drawable.heart, R.color.text_red)
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }

        }

        // Set a listener to update the media player position when the user interacts with the seek bar
        b.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    data.mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        b.shuffle.setOnClickListener {
            if(shared.shuffle == false){
                var selected = song
                //remove current song from list
                data.queue.drop(data.queueSelectedSong)
                //shuffle the list
                var songs = data.shuffle(data.queue)
                //add selected song to the start of list
                songs.add(0,selected)
                //play:
                data.playAll(songs.toMutableList())

                //lets change the shuffle button's icon to blue
                data.changeButtonIconColor(this, b.shuffle, R.drawable.shuffle, R.color.text)
            }else{
                //lets change the shuffle button's color to normal
                data.changeButtonIconColor(this, b.shuffle, R.drawable.shuffle, R.color.text4)
            }
            shared.set(this,"player","shuffle",!shared.shuffle)
        }
        b.replay.setOnClickListener {
            if (shared.repeat=="always"){
                shared.set(this,"player","repeat","once")
                data.changeButtonIconColor(this, b.replay, R.drawable.repeat_one, R.color.text)
            }else if (shared.repeat=="never"){
                shared.set(this,"player","repeat","always")
                data.changeButtonIconColor(this, b.replay, R.drawable.repeat, R.color.text)
            }else if (shared.repeat=="once"){
                shared.set(this,"player","repeat","never")
                data.changeButtonIconColor(this, b.replay, R.drawable.repeat, R.color.text4)
            }
        }

        activityCreated = true
    }


    //its not working in android 14
    override fun onBackPressed() {
        // Disable exit animation
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_down_activity)
    }




    companion object{ //--------------------------------------------------------------------------------------------------------------------------------------------
        private val TAG: String = "playerActivity"
        lateinit var b:ActivityPlayerBinding
        lateinit var frag_manager: FragmentManager
        lateinit var c:Context
        var activityCreated = false

        lateinit var song: Song

        //this called whenever the song is changed
        fun updateUi(song: Song){
            if(song.path !=  Companion.song.path) {
                b.title.text = song.title
                b.artist.text = song.artist
                b.album.text = song.album
                b.playedTime.text = data.mediaPlayer?.currentPosition?.let {
                    data.getDurationString(
                        it.toLong()
                    )
                }
                b.duration.text = data.getDurationString(song.duration)


                if(exist_Favorites(c,song.id)){
                    data.changeButtonIconColor(c, b.like, R.drawable.heart, R.color.text_red)
                }else{
                    data.changeButtonIconColor(c, b.like, R.drawable.heart, R.color.text2)
                }

                setupRecycler()
                setListeners(song)

                Companion.song = song
            }
        }

        //this is called only when the player activity opened
        fun updateUi(){
                setupRecycler(false)

                b.title.text = song.title
                b.artist.text = song.artist
                b.album.text = song.album
                b.playedTime.text = data.mediaPlayer?.currentPosition?.let {
                    data.getDurationString(
                        it.toLong()
                    )
                }
                b.duration.text = data.getDurationString(song.duration)

                //shuffle button
                if(shared.shuffle){
                    //lets change the shuffle button's icon to blue
                    data.changeButtonIconColor(c, b.shuffle, R.drawable.shuffle, R.color.text)
                }else{
                    data.changeButtonIconColor(c, b.shuffle, R.drawable.shuffle, R.color.text4)
                }

                //repeat button
                if (shared.repeat=="always"){
                    data.changeButtonIconColor(c, b.replay, R.drawable.repeat, R.color.text)
                }else if (shared.repeat=="never"){
                    data.changeButtonIconColor(c, b.replay, R.drawable.repeat, R.color.text4)
                }else if (shared.repeat=="once"){
                    data.changeButtonIconColor(c, b.replay, R.drawable.repeat_one, R.color.text)
                }

                //current song's favorite button(other songs will be initialized with updateUi(song))
                if(exist_Favorites(c, song.id)){
                    data.changeButtonIconColor(c, b.like, R.drawable.heart, R.color.text_red)


                }else{
                    data.changeButtonIconColor(c, b.like, R.drawable.heart, R.color.text2)
                }

                setListeners(song)
        }

        private fun setupRecycler(animation:Boolean = false) {
            // Find the RecyclerView in your layout
            var recyclerView = b.root.findViewById<RecyclerView>(R.id.recyclerViewPlayer)

            // Set up the LinearLayoutManager
            val layoutManager = LinearLayoutManager(c, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = layoutManager


            // Use scrollToPosition to scroll to the desired position
            if(animation){
                recyclerView.smoothScrollToPosition(data.queueSelectedSong)
            }else {
                recyclerView.scrollToPosition(data.queueSelectedSong)
            }

            // Set up the Adapter
            var adapter = PlayerAdapter(data.queue, frag_manager)
            recyclerView.adapter = adapter

            try {
                // Center Snap with PagerSnapHelper
                val pagerSnapHelper = PagerSnapHelper()
                pagerSnapHelper.attachToRecyclerView(recyclerView)

            }catch (e:Exception){
                Log.e(TAG, e.message,null)
            }
        }



        fun setListeners(song: Song) {
            b.artist.setOnClickListener {
                //search
                var result: artist? = data.getArtist(song.artist)

                if (result != null) {
                    //open artist page
                    try {
                        val intent = Intent(c, artistContent::class.java)
                        intent.putExtra("artist", result.name)
                        intent.putExtra("songCount", result.countSongs)
                        intent.putExtra("albumCount", result.countAlbum)

                        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        c.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
                    }
                }
            }
            b.album.setOnClickListener {
                //search
                var result: album? = data.getAlbum(song.album, song.albumArtist)

                if (result != null) {
                    //open album page
                    try {
                        val intent = Intent(c, albumContent::class.java)
                        intent.putExtra("name", result.name)
                        intent.putExtra("artist", result.artist)
                        intent.putExtra("year", result.year)

                        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        c.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
                    }
                }
            }

            b.option.setOnClickListener {
                val adapter = menu_song_adapter(song)
                adapter.show(frag_manager, adapter.tag)
            }
        }
    }
}
