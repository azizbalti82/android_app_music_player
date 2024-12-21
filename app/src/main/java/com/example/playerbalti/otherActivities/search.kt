package com.example.playerbalti.otherActivities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.adapters.AlbumsAdapter
import com.example.playerbalti.adapters.ArtistAdapter
import com.example.playerbalti.adapters.FolderAdapter
import com.example.playerbalti.adapters.SongAdapter
import com.example.playerbalti.adapters.playlistAdapter
import com.example.playerbalti.album
import com.example.playerbalti.artist
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivitySearchBinding
import com.example.playerbalti.folder
import com.example.playerbalti.playlist
import java.util.Locale

class search : AppCompatActivity() {
    lateinit var b:ActivitySearchBinding
    val c = this

    private lateinit var viewSong:RecyclerView
    private lateinit var viewArtist:RecyclerView
    private lateinit var viewAlbum:RecyclerView
    private lateinit var viewDir:RecyclerView
    private lateinit var viewPlaylist:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(b.root)

        //set variables:
        viewSong = b.root.findViewById(R.id.recyclerViewSong)
        viewArtist = b.root.findViewById(R.id.recyclerViewArtist)
        viewAlbum = b.root.findViewById(R.id.recyclerViewAlbum)
        viewDir = b.root.findViewById(R.id.recyclerViewFolder)
        viewPlaylist = b.root.findViewById(R.id.recyclerViewPlaylist)


        //set screen:
        hideAll()
        b.songsChip.isChecked = true
        b.input.requestFocus()


        //set listeners
        b.input.addTextChangedListener {
            if(b.input.text.toString()==""){
                b.toolbutton.setImageResource(R.drawable.vocal)

                //set listeners
                b.toolbutton.setOnClickListener{
                    speechInput()
                }
                setScreen()
            }else{
                b.toolbutton.setImageResource(R.drawable.clear)
                b.toolbutton.setOnClickListener{
                    b.input.text.clear()
                }

                //set list
                setScreen()
            }
        }

        b.cancelButton.setOnClickListener{
            finish()
        }
        b.toolbutton.setOnClickListener{
            speechInput()
        }
        b.songsChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setScreen()
            }
        }
        b.artistChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setScreen()
            }
        }
        b.albumsChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setScreen()
            }
        }
        b.playlistsChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setScreen()
            }
        }
        b.foldersChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                setScreen()
            }
        }
        Log.e("SearchActivity", "activity created")
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 102 && resultCode == Activity.RESULT_OK){
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            b.input.setText(result?.get(0).toString())
            b.input.setSelection(b.input.text.length)
            setScreen()
        }
    }
    private fun speechInput() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this, "speech recognization is not available", Toast.LENGTH_SHORT).show()
        }else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"search for song")
            startActivityForResult(i,102)
        }
    }


    fun setSongs(){
        var word = b.input.text.toString()

        //search
        var result: MutableList<Song> = ArrayList()
        for (i in data.songsList) {
            if (i.title.contains(word, ignoreCase = true)) {
                    result.add(i)
                }
        }

        hideAll()
        //close if result is empty
        if (result.isEmpty()) {
            b.noResultMsg.visibility = View.VISIBLE
        } else {
            //make sure the recyclerview is shown
            b.noResultMsg.visibility = View.INVISIBLE
            b.recyclerViewLayoutSong.visibility = View.VISIBLE

            //set list
            var recyclerView = viewSong
            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            var adapter = SongAdapter(result, supportFragmentManager)
            recyclerView.adapter = adapter

            //set header
            b.songHeader.text = "Songs (${result.size})"
        }
    }
    fun setArtist(){
        var word  = b.input.text.toString()

        //search
        var result:MutableList<artist> = ArrayList()
        for(i in data.songsArtist){
            if(i.name.contains(word, ignoreCase = true)){
                result.add(i)
            }
        }

        hideAll()
        //close if result is empty
        if (result.isEmpty()) {
            b.noResultMsg.visibility = View.VISIBLE
        }else {
            //make sure the recyclerview is shown:
            b.noResultMsg.visibility = View.INVISIBLE
            b.recyclerViewLayoutArtist.visibility = View.VISIBLE

            //set list
            var recyclerView = viewArtist

            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            var adapter = ArtistAdapter(result, supportFragmentManager)
            recyclerView.adapter = adapter

            //set header
            b.artistHeader.text = "Artists (${result.size})"
        }
    }
    fun setAlbum(){
        var word  = b.input.text.toString()

        //search
        var result:MutableList<album> = ArrayList()
        for(i in data.songsAlbum){
            if(i.name.contains(word, ignoreCase = true)){
                result.add(i)
            }
        }

        hideAll()
        //close if result is empty
        if (result.isEmpty()) {
            b.noResultMsg.visibility = View.VISIBLE
        } else {
            //make sure the recyclerview is shown:
            b.noResultMsg.visibility = View.INVISIBLE
            b.recyclerViewLayoutAlbum.visibility = View.VISIBLE

            //set list
            var recyclerView = viewAlbum

            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            var adapter = AlbumsAdapter(result, supportFragmentManager)
            recyclerView.adapter = adapter

            //set header
            b.albumHeader.text = "Albums (${result.size})"
        }
    }
    fun setDir(){
        var word  = b.input.text.toString()

        //search
        var result:MutableList<folder> = ArrayList()
        for(i in data.songsDir){
            if(i.name.contains(word, ignoreCase = true)){
                result.add(i)
            }
        }

        hideAll()
        //close if result is empty
        if (result.isEmpty()) {
            b.noResultMsg.visibility = View.VISIBLE
        }else {
            //make sure the recyclerview is shown:
            b.noResultMsg.visibility = View.INVISIBLE
            b.recyclerViewLayoutFolder.visibility = View.VISIBLE

            //set list
            var recyclerView = viewDir

            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            var adapter = FolderAdapter(result, supportFragmentManager)
            recyclerView.adapter = adapter

            //set header
            b.folderHeader.text = "Folders (${result.size})"
        }
    }
    fun setPlaylist(){
        var word  = b.input.text.toString()

        //search
        var result:MutableList<playlist> = ArrayList()
        for(i in data.songsPlaylists){
            if(i.name.contains(word, ignoreCase = true)){
                result.add(i)
            }
        }

        hideAll()
        //close if result is empty
        if (result.isEmpty()) {
            b.noResultMsg.visibility = View.VISIBLE
        }else {
            //make sure the recyclerview is shown:
            b.noResultMsg.visibility = View.INVISIBLE
            b.recyclerViewLayoutPlaylist.visibility = View.VISIBLE

            //set list
            var recyclerView = viewPlaylist

            val layoutManager = LinearLayoutManager(this)
            recyclerView.layoutManager = layoutManager

            var adapter = playlistAdapter(result, supportFragmentManager)
            recyclerView.adapter = adapter

            //set header
            b.playlistHeader.text = "Playlists (${result.size})"
        }
    }

    fun setScreen(){
        var input  = b.input.text.toString()
        if(input.isBlank()) {
            b.noResultMsg.visibility = View.VISIBLE
            hideAll()
        }else{
            if(b.songsChip.isChecked){
                setSongs()
            }else if(b.artistChip.isChecked){
                setArtist()
            }else if(b.albumsChip.isChecked){
                setAlbum()
            }else if(b.foldersChip.isChecked){
                setDir()
            }else if(b.playlistsChip.isChecked){
                setPlaylist()
            }
        }
    }


    fun hideAll() {
        b.recyclerViewLayoutSong.visibility = View.INVISIBLE
        b.recyclerViewLayoutArtist.visibility = View.INVISIBLE
        b.recyclerViewLayoutAlbum.visibility = View.INVISIBLE
        b.recyclerViewLayoutPlaylist.visibility = View.INVISIBLE
        b.recyclerViewLayoutFolder.visibility = View.INVISIBLE
    }
}