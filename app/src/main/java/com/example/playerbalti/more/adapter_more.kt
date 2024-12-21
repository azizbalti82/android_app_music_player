package com.example.playerbalti.more

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.databinding.ActivityMoreBinding
import com.example.playerbalti.databinding.OpenPlaystorePageBinding
import com.example.playerbalti.menu.menu_sleep_timer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreAdapter : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val b = ActivityMoreBinding.inflate(inflater,container,false)
        b.containerSettings.setOnClickListener {
            val intent = Intent(requireContext(), settingsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerAbout.setOnClickListener{
            val intent = Intent(requireContext(), aboutActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerEqualizer.setOnClickListener{
            val intent = Intent(requireContext(), equalizerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            this.startActivity(intent)
        }
        b.containerSleep.setOnClickListener{
            val adapter = menu_sleep_timer()
            adapter.show(childFragmentManager, adapter.tag)
        }
        b.containerRate.setOnClickListener{
            try {
                val inflater = layoutInflater
                val dialogView: View = inflater.inflate(R.layout.open_playstore_page, null)

                // Build the AlertDialog
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setView(dialogView)

                // Create the AlertDialog
                val customDialog: AlertDialog = builder.create()
                // Set up any interactions or functionalities

                val c: OpenPlaystorePageBinding = OpenPlaystorePageBinding.inflate(layoutInflater)
                c.rate.setOnClickListener {
                    //you can open play store page here later.
                }

                customDialog.show()
            }catch (e: Exception) {
                Log.e("error", "$e")
            }
        }
        b.containerShare.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT, "Balti Player: Share the Music! \uD83C\uDFB6\n" +
                        "Enjoy our new offline music player for android.\nhttps://playstore.test/")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }



        data.disableContainer(requireContext(),b.containerEqualizer)


        return b.root
    }
}