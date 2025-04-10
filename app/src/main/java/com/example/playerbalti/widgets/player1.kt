package com.example.playerbalti.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.playerbalti.MainActivity
import com.example.playerbalti.R
import com.example.playerbalti.data
import com.example.playerbalti.storage.shared

class player1 : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val views = update(context, appWidgetManager, appWidgetId)
            /*
            val song = data.queue[data.queueSelectedSong]
            Log.d("widget", "onUpdate: views is null: ${views==null}")
            views.setTextViewText(R.id.widget_title,song.title)

             */
        }
    }

    override fun onEnabled(context: Context) {
        // the first widget is created (you just added your first widget)
    }

    override fun onDisabled(context: Context) {
        // when the last widget is removed (no more widgets enabled)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        try {
            Log.w("widget", "onReceive: ${intent?.action}")
            // Check if the intent is not null and has an action
            if (intent?.action != null) {
                val action = intent.action
                // Handle button clicks based on the action
                if(action == "BUTTON_CLICKED_SHUFFLE"){
                    if(shared.shuffle == false){
                        if(data.queue.isNotEmpty()) {
                            var selected = data.queue[data.queueSelectedSong]
                            //remove current song from list
                            data.queue.drop(data.queueSelectedSong)
                            //shuffle the list
                            var songs = data.shuffle(data.queue)
                            //add selected song to the start of list
                            songs.add(0, selected)
                            //play:
                            data.playAll(songs.toMutableList())
                        }

                        //lets change the shuffle button's icon to blue
                    }else{
                        //lets change the shuffle button's color to normal
                    }
                    shared.set(context,"player","shuffle",!shared.shuffle)

                }
                else if(action == "BUTTON_CLICKED_PREVIOUS"){
                    val duration = data.getCurrentSongPlayedTime()
                    if (duration != null && ((duration / 1000) < 5 && data.queueSelectedSong > 0)) {
                        data.queueSelectedSong--
                        data.miniPlayerStatue("stop_play")
                    } else {
                        //restart song
                        data.miniPlayerStatue("stop_play")
                    }
                }
                else if(action == "BUTTON_CLICKED_PLAY"){
                    data.miniPlayerStatue("pause_play")
                }
                else if(action == "BUTTON_CLICKED_NEXT"){
                    //next song button
                    data.queueSelectedSong++
                    data.miniPlayerStatue("stop_play")
                }
                else if(action == "BUTTON_CLICKED_FAVORITE"){
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                else if(action == "OPEN_APP_ACTION") {
                    // Handle the click action to open the app
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context?.startActivity(intent)
                }
            }
        } catch (e:Exception){
            Log.e("widget", "onReceive: $e", )
        }

    }

    private fun setButtonClickListener(context: Context, views: RemoteViews, buttonId: Int, action: String) {
        val intent = Intent(context, javaClass)
        intent.action = action

        // Add the appropriate flags based on Android version
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
        views.setOnClickPendingIntent(buttonId, pendingIntent)
    }


    internal fun update(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int):RemoteViews  {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.player1)

        // Set click listeners for each button

        setButtonClickListener(context, views, R.id.widget_container, "OPEN_APP_ACTION")
        setButtonClickListener(context, views, R.id.widget_art, "OPEN_APP_ACTION")

        setButtonClickListener(context, views, R.id.shuffle, "BUTTON_CLICKED_SHUFFLE")
        setButtonClickListener(context, views, R.id.previous, "BUTTON_CLICKED_PREVIOUS")
        setButtonClickListener(context, views, R.id.play, "BUTTON_CLICKED_PLAY")
        setButtonClickListener(context, views, R.id.next, "BUTTON_CLICKED_NEXT")
        setButtonClickListener(context, views, R.id.favorite, "BUTTON_CLICKED_FAVORITE")

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

        return views
    }
}
