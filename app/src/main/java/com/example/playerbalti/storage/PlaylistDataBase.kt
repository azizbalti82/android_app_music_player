package com.example.playerbalti.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlaylistDataBase(context: Context):SQLiteOpenHelper(context, DBNAME,null,VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        //if i'm creating the database for the first time
        createRecentlyPlayedTable(db)
        createFavoritesTable(db)
        createMostPlayedTable(db)
        createQueueTable(db)
        createUserPlaylistsTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    private fun  createFavoritesTable(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE ${PlaylistAttributes.Table1} (" +
                "${PlaylistAttributes.ID} LONG PRIMARY KEY," +
                "${PlaylistAttributes.timeAdded} TIMESTAMP DEFAULT CURRENT_TIMESTAMP )"
        db?.execSQL(sql)
    }

    private fun createRecentlyPlayedTable(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE ${PlaylistAttributes.Table2} (" +
                "${PlaylistAttributes.ID} LONG PRIMARY KEY," +
                "${PlaylistAttributes.timeAdded} TIMESTAMP DEFAULT CURRENT_TIMESTAMP )"
        db?.execSQL(sql)
    }

    private fun createMostPlayedTable(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE ${PlaylistAttributes.Table3} (" +
                "${PlaylistAttributes.ID} LONG PRIMARY KEY," +
                "${PlaylistAttributes.playing_count} INT DEFAULT 0 )"
        db?.execSQL(sql)
    }

    private fun createQueueTable(db: SQLiteDatabase?) {
        //each row hold ID of the song
        //used when the app is closed it saves the queue
        val sql = "CREATE TABLE ${PlaylistAttributes.Table4} (${PlaylistAttributes.ID} LONG PRIMARY KEY)"
        db?.execSQL(sql)
    }

    private fun createUserPlaylistsTable(db: SQLiteDatabase?) {
        val sql = "CREATE TABLE ${PlaylistAttributes.Table5} (" +
                "${PlaylistAttributes.ID} LONG," +
                "${PlaylistAttributes.playlistID} TEXT, "+
                "PRIMARY KEY ( ${PlaylistAttributes.ID}, ${PlaylistAttributes.playlistID}))"
        db?.execSQL(sql)
    }

    companion object{
        const val DBNAME = "Playlist"
        const val VERSION = 1 //DATA BASE VERSION
    }

}

object PlaylistAttributes{
    const val Table1 = "favorites"
    const val Table2 = "recentlyPlayed"
    const val Table3 = "mostPlayed"
    const val Table4 = "queue"
    const val Table5 = "userPlaylists"
    const val ID = "id" //the media store id for each song
    const val playlistID = "playlistId" //the media store id for each song
    const val timeAdded = "timeAdded"
    const val playing_count = "playing_count"
}