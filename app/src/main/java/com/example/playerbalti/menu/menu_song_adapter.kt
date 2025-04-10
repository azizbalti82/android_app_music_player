package com.example.playerbalti.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.playerbalti.R
import com.example.playerbalti.Song
import com.example.playerbalti.album
import com.example.playerbalti.artist
import com.example.playerbalti.browser.webviewActivity
import com.example.playerbalti.content.albumContent
import com.example.playerbalti.content.artistContent
import com.example.playerbalti.content.folderContent
import com.example.playerbalti.data
import com.example.playerbalti.databinding.MenuSongBinding
import com.example.playerbalti.folder
import com.example.playerbalti.otherActivities.songDetails
import com.example.playerbalti.storage.db_manager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch


class menu_song_adapter(var song:Song) : BottomSheetDialogFragment() {
    lateinit var b:MenuSongBinding

    // Initialize ActivityResultLauncher for permission request
    private val launcher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("mainActivitymsg", "Permission granted")
            } else {
                // Permission denied, handle accordingly
                Log.d("mainActivi tymsg", "Permission denied")
                Toast.makeText(requireContext(), "We need permission to delete", Toast.LENGTH_LONG).show()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = MenuSongBinding.inflate(inflater,container,false)

        //set values:
        b.name.text = song.title
        b.artist.text = song.artist

        //set image:
        lifecycleScope.launch {
            var url = data.extractAlbumArt(song.path)
            data.loadImage(requireContext(), url, b.image, R.drawable.default_song_image)
        }

        //initialize favorites button's color
        if(db_manager.exist_Favorites(requireContext(),song.id)){
            data.changeButtonIconColor(requireContext(), b.like,R.drawable.heart,R.color.text_red)
        }else{
            data.changeButtonIconColor(requireContext(), b.like,R.drawable.heart,R.color.text2)
        }



        //set listeners
        b.containerYoutube.setOnClickListener {
            try{
                val intent = Intent(requireContext(), webviewActivity::class.java)
                intent.putExtra("title","${song.title} by ${song.artist}")
                intent.putExtra("url","https://www.youtube.com/results?search_query=")
                requireContext().startActivity(intent)
            }catch(e:Exception){
                Log.e("mainActivitymsg",e.toString())
            }
        }

        b.containerSpotify.setOnClickListener {
            try{
                val intent = Intent(requireContext(), webviewActivity::class.java)
                intent.putExtra("title","${song.artist} ${song.title}")
                intent.putExtra("url","https://open.spotify.com/search/song/")
                requireContext().startActivity(intent)
            }catch(e:Exception){
                Log.e("mainActivitymsg",e.toString())
            }
        }

        b.containerPlaynext.setOnClickListener{
            data.addNext(requireContext(),song)
            //hide menu
            this.dismiss()
        }
        b.containerAddQueue.setOnClickListener{
            data.add(requireContext(),song)
            //hide menu
            this.dismiss()
        }

        b.containerAddPlaylist.setOnClickListener{
            val adapter = menu_playlist_add_adapter(arrayListOf(song))
            adapter.show(requireFragmentManager(), adapter.tag)
            this.dismiss()
        }

        b.info.setOnClickListener {
            //open details page
            try {
                data.song = song
                val intent = Intent(requireContext(), songDetails::class.java)
                startActivity(intent)
            } catch (e: Exception) {
            }
        }

        b.like.setOnClickListener {
            if(db_manager.exist_Favorites(requireContext(),song.id)){
                //if exist remove
                val check = db_manager.remove_Favorites(requireContext(),song.id)
                if(check){
                    data.changeButtonIconColor(requireContext(), b.like,R.drawable.heart,R.color.text2)
                }
            }else{
                //add to favorites
                val check = db_manager.add_Favorites(requireContext(),song.id)
                if(check){
                    data.changeButtonIconColor(requireContext(), b.like,R.drawable.heart,R.color.text_red)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }


        b.containerDelete.setOnClickListener{
            //data.deleteFile(requireContext(),song)
            try {
                //delete(requireContext(), File(song.path).toUri())
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        b.containerAlbum.setOnClickListener {
            //search
            var result:album? = data.getAlbum(song.album, song.albumArtist)

            if(result != null) {
                //open album page
                try {
                    val intent = Intent(requireContext(), albumContent::class.java)
                    intent.putExtra("name",result.name)
                    intent.putExtra("artist", result.artist)
                    intent.putExtra("year", result.year)

                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
                }
            }
        }
        b.containerArtist.setOnClickListener {
            //search
            var result:artist? = data.getArtist(song.artist)

            if(result != null) {
                //open artist page
                try {
                    val intent = Intent(requireContext(), artistContent::class.java)
                    intent.putExtra("artist",result.name)
                    intent.putExtra("songCount",result.countSongs)
                    intent.putExtra("albumCount",result.countAlbum)

                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
                }
            }
        }
        b.containerFolder.setOnClickListener {
            //search
            var result:folder? = data.getFolder(song.path.substringBeforeLast('/'))

            if(result != null) {
                //open folder page
                try {
                    val intent = Intent(requireContext(), folderContent::class.java)
                    intent.putExtra("path",result.path)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
                }
            }
        }
        b.containerShare.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, Uri.parse(song.path))
                type = "audio/*"
            }

            val shareIntent = Intent.createChooser(sendIntent,null)
            startActivity(shareIntent)
        }
        b.containerDetails.setOnClickListener {
            //open details page
            try {
                data.song = song
                val intent = Intent(requireContext(), songDetails::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("mainActivitymsg", "error in onclick in folder adapter: \n$e")
            }
        }

        //disable features until they are ready
        data.disableContainer(requireContext(),b.containerTag)
        data.disableContainer(requireContext(),b.containerDelete)
        data.disableContainer(requireContext(),b.containerAudioCutter)
        data.disableContainer(requireContext(),b.containerRingtone)
        data.disableContainer(requireContext(),b.containerShare)
        return b.root
    }
}