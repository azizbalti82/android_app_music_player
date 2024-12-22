package com.example.playerbalti

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.PorterDuff
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RemoteViews
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playerbalti.otherActivities.playerActivity
import com.example.playerbalti.storage.db_manager
import com.example.playerbalti.storage.shared
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import kotlin.random.Random



class data {
    companion object {
        var lastRenamedPlaylist: String = ""
        var playlist_deleted = false
        var context: Context? = null
        var main:MainActivity? = null
        var taskbar: LinearLayout? = null
        var player_activity: playerActivity? = null
        var remainingTime: TextView? = null

        //the views holder of widgets
        var RemoteViews: RemoteViews? = null

        //songs lists
        var dataChanged = false
        var tabsChanged = false
        var songsCount = 0
        var songsList: MutableList<Song> = ArrayList()
        var songsDir: MutableList<folder> = ArrayList()
        var songsAlbum: MutableList<album> = ArrayList()
        var songsArtist: MutableList<artist> = ArrayList()
        var songsGenres: MutableList<genre> = ArrayList()
        var songsSelectionMode: MutableList<Song> = ArrayList() //list of songs used by selection mode

        //playlists
        var songsPlaylists: MutableList<playlist> = ArrayList()
        var systemPlaylists: MutableList<playlist> = ArrayList() //like favorites...
        var playingCount:MutableList<Int> = ArrayList() //holds all the playing counts for the playlist most_played

        //queue variables
        var queue: MutableList<Song> = ArrayList()
        var queueSelectedSong: Int = -1
        var queueSelectedIsPlaying = false
        var mediaPlayer: MediaPlayer? = MediaPlayer()

        //miniplayer views
        var miniplayer: LinearLayout? = null
        var miniplayer_img: ImageView? = null
        var miniplayer_song: TextView? = null
        var miniplayer_playpause: ImageView? = null
        var miniplayer_next: ImageView? = null
        var progress: ProgressBar? = null

        //player activity views
        var player_seekbar: SeekBar? = null
        var player_played_time: TextView? = null
        var player_duration: TextView? = null
        var player_title: TextView? = null
        var player_album: TextView? = null
        var player_artist: TextView? = null
        var player_playpause: ImageView? = null

        val handler = Handler() //for song position
        var countdownTimer: CountDownTimer? = null

        var song: Song? = null //just a container u can use to share song from activity to another

        //-------------------------------------------------------------------------------------------
        fun get_hidden_tabs():MutableList<String>{
            return shared.hiddenTabs.split('|').toMutableList()
        }
        fun get_tabs():MutableList<String>{
            //get tabs in order
            val result = shared.tabsOrder.split('|').toMutableList()
            if(result.isEmpty()){
                return arrayListOf<String>("songs","albums","artists","playlists","folders","genres").toMutableList()
            }else{
                return result
            }
        }

        fun restart(){
            main?.finish()
            val intent = Intent(main, MainActivity::class.java)
            main?.startActivity(intent)
        }


        fun startSleepTimer(min: Long) {
            val totalMillis = min * 60 * 1000 // Convert minutes to milliseconds

            countdownTimer?.cancel()
            countdownTimer = object : CountDownTimer(totalMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Update UI with remaining time if needed
                    if(remainingTime!=null){
                        val time = getDurationString(millisUntilFinished)
                        remainingTime?.text = time
                    }
                }


                override fun onFinish() {
                    // Stop the current song when the timer finishes
                    if (mediaPlayer?.isPlaying == true) {
                        miniPlayerStatue("pause_play")
                    }
                }
            }
            countdownTimer?.start()
        }
        //------------------------------------------------------------------------------------------------------------
        fun disableContainer(c: Context,i:View?,customMsg:String="this feature is not ready yet.."){
            i?.setOnClickListener{
                Toast.makeText(c, customMsg, Toast.LENGTH_SHORT).show()
            }
        }

        fun getAllSongs(context: Context,success_msg:String = "", error_msg:String = "") {
            try {
                songsCount = 0
                songsList.clear()
                // Set the URI for the audio files in the MediaStore
                val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                // Define the columns you want to retrieve
                val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ARTIST,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.GENRE,
                    MediaStore.Audio.Media.CD_TRACK_NUMBER,
                    MediaStore.Audio.Media.BITRATE,
                    MediaStore.Audio.Media.DATE_MODIFIED ,
                    MediaStore.Audio.Media.DATE_ADDED
                    )

                // Get the content resolver
                val contentResolver = context.contentResolver

                //if allow_30_sec option is true: allow short songs (by default not allowed)
                var cursor: Cursor?
                if(!shared.allow_30_sec) {
                    //method1
                    val selection = MediaStore.MediaColumns.DURATION + " > ?"
                    val selectionArgs = arrayOf("30000") //30 seconds
                    cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)

                    //method2
                    //cursor = contentResolver.query(uri, projection, "duration>30000", null, sortOrder)
                }else{
                    cursor = contentResolver.query(uri, projection, null, null, null)
                }
                if (cursor != null) {
                    Log.d("mainActivitymsg", "Cursor is not null")
                    while (cursor.moveToNext()) {
                        // Retrieve the data from the cursor
                        var id =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        var title =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                                ?: "unknown"
                        var artist =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                                ?: "unknown"
                        var path =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                                ?: ""
                        var duration =
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                        var album =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                                ?: "unknown"
                        var albumArtist =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST))
                                ?: artist
                        var year =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
                        var genre =
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE))
                                ?: "unknown"
                        var tracknumber =
                            cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.CD_TRACK_NUMBER))

                        val bitrate = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE))
                        val dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED))
                        val dateModified = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED))
/*
                        var lyrics =""
                        try {
                            lyrics = cursor.getString(cursor.getColumnIndexOrThrow("LYRICS"))
                        }catch (e:Exception){
                            Log.e("error", e.toString())
                        }
 */

                        // Create a Song object and add it to the list
                        val song = Song(
                            id,
                            title,
                            artist,
                            path,
                            duration,
                            album,
                            albumArtist,
                            year,
                            genre,
                            tracknumber,
                            bitrate,
                            dateAdded,
                            dateModified
                        )
                        songsList.add(song)
                    }
                    // Close the cursor when you're done with it
                    cursor.close()
                } else {
                    Log.e("mainActivitymsg", "Cursor is null")
                }
                songsCount = songsList.count()
                Log.d("mainActivitymsg", "songs updated")
                if(success_msg.isNotEmpty()){
                    Toast.makeText(context, success_msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if(error_msg.isNotEmpty()){
                    Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show()
                }
                Log.d("mainActivitymsg", "error while updating songs")
                e.printStackTrace()
            }
        }


        fun getLastAdded(): MutableList<Song> {
            //get the last 100 added songs
            var songs = songsList.sortedByDescending { it.dateAdded }
            if(songs.size>=100){
                return songs.subList(0,100).toMutableList()
            }else{
                return songs.toMutableList()
            }

        }

        fun getDirectories() {
            /*
            this function is supposed to extract directories with their metadata:
            name/path/number of tracks
             */
            if (songsList.isNotEmpty()) {
                var result = mutableListOf<String>()
                songsDir.clear()
                for (song: Song in songsList) {
                    result.add(song.path.substringBeforeLast('/'))
                }
                result = result.distinct().toMutableList()

                //save folders:
                for (i in result) {
                    var songs = getFolderSongs(i)
                    songsDir.add(folder(i.substringAfterLast('/'), i,songs.size,songs))
                }
            }
        }

        fun getAlbums() {
            //get all albums
            var result: MutableList<album> = ArrayList()
            for (song in songsList) {
                var name = song.album
                var artist = song.albumArtist
                var year = song.year

                //check if this album already added
                var exist = false
                for (i in result) {
                    if (i.name == name) {
                        i.songs.add(song)
                        i.trackCount++
                        exist = true
                        break
                    }
                }
                if (!exist) {
                    result.add(album(name, artist, year, 1,arrayListOf(song)))
                }
            }
            songsAlbum = result
        }

        fun getGenres() {
            //update songs
            //getAllSongs(context,MediaStore.Audio.Media.GENRE + " ASC")

            //get all genres
            var result: MutableList<genre> = ArrayList()
            for (i in songsList) {
                var name = i.genre

                //if gerne exist add 1 to song count
                var exist = false
                for (i in result) {
                    if (i.name == name) {
                        i.count++
                        exist = true
                        break
                    }
                }

                //else add it with count=1
                if (!exist) {
                    result.add(genre(name, 1))
                }
            }
            songsGenres = result
        }
        fun getArtists() {
            //get all artists (only name and songCount)
            var result: MutableList<artist> = ArrayList()
            for (song in songsList) {
                var artist = song.artist

                //if artist exist add 1 to song count
                var exist = false
                for (i in result) {
                    if (i.name == artist) {
                        i.songs.add(song)
                        i.countSongs++
                        exist = true
                        break
                    }
                }

                //calculate albums of this artist if its not calculated yet (=-1):
                //add songs count to 1
                //now we have one songCount recorded and the total albumsCount

                //if this is the first time we add this artist
                if (!exist) {
                    //get all of his albums
                    var albums = getArtistAlbums(artist)
                    //save
                    result.add(artist(artist, 1,albums.size,arrayListOf(song),albums))
                }
            }

            songsArtist = result
        }
        fun updateLists(c: Context) {
            getAllSongs(c)
            getDirectories()
            getAlbums()
            getGenres()
            getArtists()
        }

        fun count(i: String, array: MutableList<String>): Int {
            var n = 0
            for (item in array) {
                if (item == i) {
                    n++
                }
            }
            return n
        }

        fun extractAlbumArt(filePath: String): ByteArray? {
            return try {
                MediaMetadataRetriever().use { retriever ->
                    retriever.setDataSource(filePath)
                    retriever.embeddedPicture
                }
            } catch (e: Exception) {
                // Log the error or handle it accordingly
                null
            }
        }


        fun miniPlayerStatue(playing: String,updateArt:Boolean = true) {
            if (queue.isEmpty()) {
                miniplayer?.visibility = View.INVISIBLE
            } else {
                //set mini_player visible
                miniplayer?.visibility = View.VISIBLE

                //if current Song reached the end: back to song n°0 (a loop)
                if (queueSelectedSong == queue.count()) {
                    queueSelectedSong = 0
                }

                //set the new selected song in the mini_player
                val song = queue[queueSelectedSong] //get current song
                miniplayer_song?.text = song.title  //set title

                /*
                // ------------- initializing ui of widget_player -------------------------------------
                if (player1.views != null) {
                    Log.d("widget", "RemoteViews is not null")
                    //set title
                    player1.views?.setTextViewText(R.id.widget_title,song.title)
                    //set image
                    player1.views?.setImageViewResource(R.id.widget_art,R.drawable.album)
                }else{
                    Log.d("widget", "RemoteViews is null")
                }

                 */

                //play the selected song:
                if (playing == "stop_play") {
                    stop_and_play(song.path,updateArt)
                }
                else if (playing == "pause_play") {
                    play_or_pause(song.path)
                }
                else if (playing == "initialize") {
                    // ----------- this will initialize only mini_player

                    //if image not set, we extract it and set it
                    val art = extractAlbumArt(song.path)
                    if(miniplayer_img?.drawable == null) {
                        loadImage(context!!, art, miniplayer_img!!, R.drawable.default_song_image)
                    }
                    //setup progress bar
                    progress?.max = mediaPlayer?.duration ?: 0

                    //set icon (play or stop)

                    if (mediaPlayer?.isPlaying == true) {
                        miniplayer_playpause?.setImageResource(R.drawable.pause)
                    }

                    // ------------- this is for player --------------------------------------------


                    // ------------- this is for notification_player -------------------------------

                }
            }
        }

        fun stop_and_play(path: String,updateArt:Boolean = true) {
            //set song art:
            Log.d("data", "stop_and_play: executed")

            //update mini_player art
            try {
                //i added try & catch because it crushes the playing in service mode (app closed)
                if(miniplayer_img != null && context!=null) {
                    val url = extractAlbumArt(path)
                    loadImage(context!!, url, miniplayer_img!!, R.drawable.default_song_image)
                }
            }catch (_:Exception){}

            // Release the MediaPlayer (stop)
            stop()


            //initialize mediaPlayer again
            mediaPlayer = MediaPlayer()
            updateSong(path)

            //play
            play()

        }

        fun play_or_pause(path: String) {
            updateSong(path)

            if (queueSelectedIsPlaying) {
                // If playing, pause playback
                pause()
            } else {
                // If paused, resume playback
                play()
            }
        }

        fun setup_players_data(){
            //this function is used to set the current seekbars values,duration,...
            //it works for: main progressbar - player - foreground service - widget
            progress?.max = mediaPlayer?.duration ?: 0

            //set listeners for mediaPlayer
            mediaPlayer?.setOnCompletionListener { mp ->
                try {
                    if(shared.repeat == "once") {
                        mp.start()
                    } else if(shared.repeat == "always") {
                        queueSelectedSong += 1
                        miniPlayerStatue("stop_play")


                    } else{
                        miniPlayerStatue("pause_play")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            //initialize player_activity
            player_seekbar?.min =  0
            player_seekbar?.max = mediaPlayer?.duration ?: 0
            player_duration?.text = mediaPlayer?.duration?.let { getDurationString(it.toLong()) }
            player_title?.text = queue[queueSelectedSong].title
            player_artist?.text = queue[queueSelectedSong].artist
            player_album?.text = queue[queueSelectedSong].album


            if(playerActivity.activityCreated) {
                val song = queue[queueSelectedSong]
                playerActivity.updateUi(song)
            }

            // Update SeekBar progress based on MediaPlayer position
            handler.postDelayed(updateSeekBar,1000) // Delayed to update every second
        }

        fun updateSong(path: String) {
            // Initialize MediaPlayer with the sound file path
            try {
                mediaPlayer?.setDataSource(path)
                mediaPlayer?.prepare()

            } catch (e: Exception) {
                e.printStackTrace()
            }

            setup_players_data()
        }

        fun stop() {
            mediaPlayer?.release()
            mediaPlayer = null

            if (mediaPlayer?.isPlaying == false) {
                miniplayer_playpause?.setImageResource(R.drawable.play)
                player_playpause?.setImageResource(R.drawable.play_circle)
            }
        }

        fun play() {
            mediaPlayer?.start()
            queueSelectedIsPlaying = true

            if (mediaPlayer?.isPlaying == true) {
                miniplayer_playpause?.setImageResource(R.drawable.pause)
                player_playpause?.setImageResource(R.drawable.pause_circle)

                //song played, lets add it to recently played playlist: ----------------------------
                if(db_manager.exist_Recent(context!!,queue[queueSelectedSong].id)){
                    //remove from recent (to put it in the top later)
                    db_manager.remove_Recent(context!!,queue[queueSelectedSong].id)
                }
                //put it in the top in recent
                db_manager.add_Recent(context!!,queue[queueSelectedSong].id)

                //lets add it to most played (if it deserves to): ----------------------------------
                if(db_manager.exist_mostPlayed(context!!,queue[queueSelectedSong].id)){
                    //update it: increment playing count
                    db_manager.update_mostPlayed(context!!,queue[queueSelectedSong].id)
                }else{
                    //add it with playing count: 1
                    db_manager.add_mostPlayed(context!!,queue[queueSelectedSong].id)
                }
            }
        }

        fun pause() {
            mediaPlayer?.pause()
            queueSelectedIsPlaying = false

            if (mediaPlayer?.isPlaying == false) {
                miniplayer_playpause?.setImageResource(R.drawable.play)
                player_playpause?.setImageResource(R.drawable.play_circle)
            }
        }

        fun playAll(songs: MutableList<Song> = ArrayList()) {
            //whenever you want to playAll just send your playlist then:
            //the queue cleared & new songs added && start fresh

            //clear playing queue
            queue.clear()

            //add songs to queue
            for (i in songs) {
                queue.add(i)
            }

            //playing
            queueSelectedSong = 0
            queueSelectedIsPlaying = false
            miniPlayerStatue("stop_play")
        }

        fun playAll(songs: MutableList<Song> = ArrayList(), pos: Int) {
            //whenever you want to playAll just send your playlist then:
            //the queue cleared & new songs added && start fresh

            //clear playing queue
            queue.clear()

            //add songs to queue
            for (i in songs) {
                queue.add(i)
            }
            //playing
            queueSelectedSong = pos
            miniPlayerStatue("stop_play")
        }

        fun addAll(c: Context, songs: MutableList<Song> = ArrayList()) {
            val empty = queue.isEmpty()
            //add "songs" to the playing queue
            for (i in songs) {
                queue.add(i)
            }
            Toast.makeText(
                c,
                "${songs.size} ${if (songs.size == 1) "song" else "songs"} added to playing queue",
                Toast.LENGTH_LONG
            ).show()
            if (empty) {
                //playing
                queueSelectedSong = 0
                miniPlayerStatue("stop_play")
            }
        }

        fun add(c: Context, song: Song) {
            val empty = queue.isEmpty()
            //add "song" to the playing queue
            queue.add(song)

            Toast.makeText(c, "1 song added to playing queue", Toast.LENGTH_SHORT).show()
            if (empty) {
                //playing
                queueSelectedSong = 0
                miniPlayerStatue("stop_play")
            }
        }

        fun addNextAll(c: Context, songs: MutableList<Song> = ArrayList()) {
            val empty = queue.isEmpty()

            //index of begining the inserting
            var p = queueSelectedSong + 1

            //add "songs" to the playing queue
            for (i in songs) {
                queue.add(p,i)
                p++
            }
            Toast.makeText(c, "will play after current song", Toast.LENGTH_SHORT).show()
            if (empty) {
                //playing
                queueSelectedSong = 0
                miniPlayerStatue("stop_play")
            }
        }

        fun addNext(c: Context, song: Song) {
            val empty = queue.isEmpty()

            //index of begining the inserting
            var p = queueSelectedSong + 1

            //add "song" to the playing queue
            queue.add(queueSelectedSong + 1,song)
            Toast.makeText(c, "will play after current song", Toast.LENGTH_LONG).show()
            if (empty) {
                //playing
                queueSelectedSong = 0
                miniPlayerStatue("stop_play")
            }
        }


        fun getCurrentSongPlayedTime():Int?{
            return mediaPlayer?.currentPosition
        }

        private val updateSeekBar = object : Runnable {
            // Runnable to update SeekBar progress
            override fun run() {
                progress?.progress = mediaPlayer?.currentPosition ?: 0
                player_seekbar?.progress = mediaPlayer?.currentPosition ?: 0
                player_played_time?.text = mediaPlayer?.currentPosition?.let { getDurationString(it.toLong()) }
                handler.postDelayed(this, 1000) // Schedule the next update after 1 second
            }
        }

        fun loadImage(c: Context, url: ByteArray?, view: ImageView, drawable: Int) {
            val default = getDrawable(c, drawable)
            if (default != null) {
                DrawableCompat.setTint(default, ContextCompat.getColor(c, R.color.text3))
            }

            Glide.with(c)
                .load(url)
                .error(default)
                .placeholder(default)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache the image efficiently
                //.transition(DrawableTransitionOptions.withCrossFade()) // Smooth crossfade transition
                .into(view)
        }

        //----------------------------------------------------------------------------- get single data ---
        fun getFolder(path: String): folder? {
            for (i in songsDir) {
                if (i.path == path) {
                    return i
                }
            }
            return null
        }
        fun getAlbum(name: String,artist:String): album? {
            for (i in songsAlbum) {
                if (i.name == name && i.artist == artist) {
                    return i
                }
            }
            return null
        }
        fun getArtist(name: String): artist? {
            for (i in songsArtist) {
                if (i.name == name) {
                    return i
                }
            }
            return null
        }

        //----------------------------------------------------------------------------- get songs ------
        private fun getFolderSongs(path: String): MutableList<Song> {
            var songs: MutableList<Song> = ArrayList()
            for (i in songsList) {
                if (i.path.startsWith(path)) {
                    songs.add(i)
                }
            }
            return songs
        }
        private fun getAlbumSongs(name: String?): MutableList<Song> {
            var songs: MutableList<Song> = ArrayList()
            for (i in songsList) {
                if (i.album == name) {
                    songs.add(i)
                }
            }
            return songs
        }

        fun getGenreSongs(name: String?): MutableList<Song> {
            //getAllSongs(c, defaultSort)
            var songs: MutableList<Song> = ArrayList()
            for (i in songsList) {
                if (i.genre == name) {
                    songs.add(i)
                }
            }
            return songs
        }
        private fun getArtistSongs(name: String?): MutableList<Song> {
            //getAllSongs(c, defaultSort)
            var songs: MutableList<Song> = ArrayList()
            for (i in songsList) {
                if (i.artist == name) {
                    songs.add(i)
                }
            }
            return songs
        }
        fun getPlaylistSongs(name: String?): MutableList<Song> {
            var songs: MutableList<Song> = ArrayList()
            for (playlist in songsPlaylists) {
                if (playlist.name == name) {
                    return playlist.songs
                }
            }
            return songs
        }

        fun getSystemPlaylistSongs(name: String?): MutableList<Song> {
            var songs: MutableList<Song> = ArrayList()
            for (playlist in systemPlaylists) {
                if (playlist.name == name) {
                    return playlist.songs
                }
            }
            return songs
        }

        fun getArtistAlbums(name: String?): MutableList<album> {
            //getAllSongs(c, defaultSort)
            var albums: MutableList<album> = ArrayList()
            for (song in songsList) {
                if (song.artist == name) {
                    var name = song.album
                    var artist = song.albumArtist
                    var year = song.year

                    //check if this album already added
                    var exist = false
                    for (i in albums) {
                        if (i.name == name) {
                            i.songs.add(song)
                            i.trackCount++
                            exist = true
                            break
                        }
                    }
                    if (!exist) {
                        albums.add(album(name, artist, year, 1,arrayListOf(song)))
                    }
                }
            }
            return albums.distinct().toMutableList()
        }





        //files delete -----------------------------------------------------------------------------
        /*
        fun deleteFile(c: Context, songs: MutableList<Song>) {
            try {
                if (songs.size == 1) {
                    deleteFile(c, songs[0])
                } else {
                    for (i in songs) {
                        val file = File(i.path)
                        val uri: Uri = Uri.fromFile(file)
                        //delete(c,uri)
                    }
                }
            }catch (e:Exception){
                Log.e("data", "deleteFile: ",e)
            }
        }
        fun deleteFile(c: Context, song: Song) {
            try{
                val file = File(song.path)
                val uri: Uri = Uri.fromFile(file)
                //delete(c,uri)
            }catch (e:Exception){
                Log.e("data", "deleteFile: ",e)
            }
        }

         */

















        fun shuffle(list: MutableList<Song>): MutableList<Song> {
            var result: MutableList<Song> = ArrayList()

            try {
                if (list.size <= 1) {
                    return list
                } else {
                    //the list must at least have 1 element
                    var firstSong = list[0]
                    var p = 1
                    for (i in list.subList(1, list.size)) {
                        //random number between 0 and n-1
                        var random = Random.nextInt(p++)
                        result.add(random, i)
                    }
                    //now lets shuffle the first element we put earlier
                    result.add(Random.nextInt(p), list[0])

                    /*
                //now lets enable shuffle: variable shuffle=true
                shuffle = true
 */

                    //return list
                    return result
                }
            } catch (e: Exception) {
                return ArrayList()
            }
        }

        fun existNamePlaylist(name: String): Boolean {
            for (i in songsPlaylists) {
                if (i.name == name) {
                    return true
                }
            }
            return false
        }


        fun getDurationString(ms:Long):String{
            //get duration in ms, return duration in string:
            //exemple: return "3:05" or "0:57" or "1:20:04
            val s = ms/1000 //duration in seconds
            var ch = ""

            if(s>=3600){
                //have hours
                var hours = (s/3600)
                var minutes = (s%3600)/60
                var seconds = s % 60
                ch = "${hours}:${if (minutes<10) "0$minutes" else minutes}:${if (seconds<10) "0$seconds" else seconds}"
            }else{
                //do not have hours
                var minutes = s/60
                var seconds = s%60
                ch = "${minutes}:${if (seconds<10) "0$seconds" else seconds}"
            }
            return ch
        }

        fun changeButtonIconColor(c:Context,button: ImageButton,d:Int, color: Int) {
            val drawable = DrawableCompat.wrap(getDrawable(c,d)!!).mutate()
            button.setImageDrawable(drawable)
            DrawableCompat.setTint(drawable, ContextCompat.getColor(button.context, color))
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
            button.setImageDrawable(drawable)
        }


        fun getLyrics(filePath: String): String? {
            return try {
                val audioFile = AudioFileIO.read(File(filePath))
                val tag = audioFile.tag
                tag.getFirst(FieldKey.LYRICS)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun updateLyrics(context: Context, fileUri: Uri, newLyrics: String) {
            try {
                val file = File(getRealPathFromURI(context, fileUri))
                val audioFile = AudioFileIO.read(file)
                val tag = audioFile.tagOrCreateDefault
                tag.setField(FieldKey.LYRICS, newLyrics)
                audioFile.commit()
                Log.d("lyrics", "Lyrics updated successfully.")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("lyrics", "Failed to update lyrics: ${e.message}")
            }
        }

        fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
            var cursor = context.contentResolver.query(contentUri, null, null, null, null)
            cursor?.moveToFirst()
            val idx = cursor?.getColumnIndex(android.provider.MediaStore.Images.Media.DATA)
            val filePath = cursor?.getString(idx ?: -1)
            cursor?.close()
            return filePath
        }
    }
}


//Song class is a simple data class to represent a song
class Song (
    val id: Long,
    val title: String,
    val artist: String,
    val path: String,
    val duration: Long,
    val album:String,
    val albumArtist:String,
    val year:Int,
    val genre:String,
    val tracknumber:Int,
    val bitrate:Int,
    val dateAdded:String,
    val dateModified:String
)
class folder (
    val name: String,
    val path: String,
    val trackCount:Int,
    val songs:MutableList<Song>,
)
class album (
    val name: String,
    val artist: String,
    val year: Int,
    var trackCount:Int,
    val songs:MutableList<Song>,
)
class genre (
    val name: String,
    var count: Int,
)
class artist (
    val name: String,
    var countSongs:Int,
    var countAlbum:Int,
    val songs:MutableList<Song>,
    val albums:MutableList<album>,
)
class playlist (
    val name: String, //the name is the id in the database
    var count:Int,
    var songs: MutableList<Song>,
)

