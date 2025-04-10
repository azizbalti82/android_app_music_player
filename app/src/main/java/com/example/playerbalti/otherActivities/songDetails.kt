package com.example.playerbalti.otherActivities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySongDetailsBinding
import com.example.playerbalti.storage.db_manager

class songDetails : AppCompatActivity() {
    lateinit var b:ActivitySongDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySongDetailsBinding.inflate(layoutInflater)
        setContentView(b.root)


            b.cancelButton.setOnClickListener {
            this.finish()
        }

        if(data.song != null){
            b.count.text = "played ${db_manager.read_mostPlayed(this, data.song!!.id)} times"

            b.title.text = data.song?.title
            b.artist.text = data.song?.artist
            b.genre.text = data.song?.genre
            b.year.text = data.song?.year.toString()
            b.duration.text = data.getDurationString(data.song?.duration!!)

            b.album.text = data.song?.album
            b.albumArtist.text = data.song?.albumArtist
            b.trackNumber.text = data.song?.tracknumber.toString()

            b.path.text = data.song?.path

            val bitrate = data.song?.bitrate.toString()
            b.bitrates.text = bitrate.substring(0,bitrate.length-3)+"K"

            //return data.song to null
            data.song = null
        }



    }
}