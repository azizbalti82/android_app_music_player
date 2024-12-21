package com.example.playerbalti.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.playerbalti.R
import com.example.playerbalti.data

class shuffle_widget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        try {
            // Check if the intent is not null and has an action (action: a text like an id for broadcasts
            if (intent?.action != null) {
                val action = intent.action
                // Handle button clicks based on the action
                if (action == "shuffle") {
                    //get shuffled new list
                    var songsList = data.shuffle(data.songsList)
                    Log.d("widget", "songs shuffled")
                    data.playAll(songsList)
                    Toast.makeText(context, "${data.songsCount} songs shuffled", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }catch (e:Exception){
            Log.e("widget", "onReceive: shuffle widget: $e")
        }
    }

    private fun setButtonClickListener(context: Context, views: RemoteViews, buttonId: Int, action: String) {
        val intent = Intent(context, shuffle_widget::class.java)
        intent.action = action

        // Add the appropriate flags based on Android version
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }

        // Creating pendingIntent from an intent: we created a broadcast.
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)
        views.setOnClickPendingIntent(buttonId, pendingIntent) //associate this broadcast with clicking the button: buttonId
    }
    internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.shuffle_widget)

        setButtonClickListener(context, views, R.id.widget_shuffle_container, "shuffle")

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}


