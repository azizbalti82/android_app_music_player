package com.example.playerbalti.storage

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.playerbalti.Song
import com.example.playerbalti.data
import com.example.playerbalti.playlist

class db_manager {
    companion object {
        val TAG = "storage"

        // favorites ------------------------------------------------------------------------------------------------------------------
        fun add_Favorites(c: Context, mediaStoreId: Long): Boolean {
            try {
                val newEntry = ContentValues().apply {
                    put(PlaylistAttributes.ID, mediaStoreId)
                }
                val n = PlaylistDataBase(c).writableDatabase.insert(
                    PlaylistAttributes.Table1,
                    null,
                    newEntry
                )
                // Check if the insert was successful
                if (n != -1L) {
                    return true
                } else {
                    return false
                }

            } catch (e: Exception) {
                Log.e(TAG, "add_Favorites: $e",)
                return false
            }
        }


        fun remove_Favorites(c: Context, mediaStoreId: Long): Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Define the WHERE clause to identify the row to be deleted
                val whereClause = "${PlaylistAttributes.ID} = ?"

                // Define the values for the WHERE clause
                val whereArgs = arrayOf(mediaStoreId.toString())

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table1, whereClause, whereArgs)

                db.close()

                //update systemPlaylists:
                read_Favorites(c)

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "add_Favorites: $e",)
                return false
            }
        }

        fun read_Favorites(c: Context) {
            try {
                //read favorites and add them as a new playlist in data.songsPlaylists
                var songs: MutableList<Song> = ArrayList()

                //return a list of all favorites (ordered from newer to older)
                val sql = "SELECT * FROM ${PlaylistAttributes.Table1} order by timeAdded desc"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1
                    val time_added = cursor.getString(1) //because colum index = 2

                    var found = false
                    //search for song by its id:
                    for (i in data.songsList) {
                        if (i.id == song_id) {
                            songs.add(i) //added song to the songList
                            Log.d("storage", "found favorites: ${i.title}")
                            found = true
                            break
                        }
                    }

                    //if a sound in the database don't exist on the device, just remove it from database
                    if (!found) {
                        remove_Favorites(c, song_id)
                    }
                    Log.d("storage", "favorites: $song_id - $time_added")
                }

                //get the index of favorites_playlist to replace it with the new one
                data.systemPlaylists.add(
                    0,
                    playlist("Favorites", songs.size, songs)
                ) //add playlist to songsPlaylists

                cursor.close()

            } catch (e: Exception) {
                Log.e(TAG, "read_Favorites: $e",)
            }
        }

        fun exist_Favorites(c: Context, id: Long): Boolean {
            try {
                //return true if exist / false if not
                val sql = "SELECT * FROM ${PlaylistAttributes.Table1} order by timeAdded desc"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1
                    val time_added = cursor.getString(1) //because colum index = 2

                    if (song_id == id) {
                        return true //song found
                    }
                    Log.d("storage", "read favorites: $song_id - $time_added")
                }
                cursor.close()

            } catch (e: Exception) {
                Log.e(TAG, "read_Favorites: $e",)
            }
            return false //not found
        }

        // recently played --------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun add_Recent(c: Context, mediaStoreId: Long): Boolean {
            try {
                val newEntry = ContentValues().apply {
                    put(PlaylistAttributes.ID, mediaStoreId)
                }
                val n = PlaylistDataBase(c).writableDatabase.insert(
                    PlaylistAttributes.Table2,
                    null,
                    newEntry
                )
                // Check if the insert was successful
                if (n != -1L) {
                    return true
                } else {
                    return false
                }

            } catch (e: Exception) {
                Log.e(TAG, "add_Recents: $e",)
                return false
            }
        }


        fun remove_Recent(c: Context, mediaStoreId: Long): Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Define the WHERE clause to identify the row to be deleted
                val whereClause = "${PlaylistAttributes.ID} = ?"

                // Define the values for the WHERE clause
                val whereArgs = arrayOf(mediaStoreId.toString())

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table2, whereClause, whereArgs)

                db.close()

                //update systemPlaylists:
                read_Recent(c)

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "add_Favorites: $e",)
                return false
            }
        }

        fun read_Recent(c: Context) {
            try {
                //read favorites and add them as a new playlist in data.songsPlaylists
                var songs: MutableList<Song> = ArrayList()

                //return a list of all favorites (ordered from newer to older)
                val sql = "SELECT * FROM ${PlaylistAttributes.Table2} order by timeAdded desc"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1
                    val time_added = cursor.getString(1) //because colum index = 2

                    var found = false
                    //search for song by its id:
                    for (i in data.songsList) {
                        if (i.id == song_id) {
                            songs.add(i) //added song to the songList
                            Log.d("storage", "found recent: ${i.title}")
                            found = true
                            break
                        }
                    }

                    //if a sound in the database don't exist on the device, just remove it from database
                    if (!found) {
                        remove_Recent(c, song_id)
                    }
                    Log.d("storage", "recents: $song_id - $time_added")
                }
                data.systemPlaylists.add(
                    0,
                    playlist("RecentlyPlayed", songs.size, songs)
                ) //add playlist to songsPlaylists }else{
                cursor.close()

            } catch (e: Exception) {
                Log.e(TAG, "read_recents: $e",)
            }
        }

        fun exist_Recent(c: Context, id: Long): Boolean {
            try {
                //return true if exist / false if not
                val sql = "SELECT * FROM ${PlaylistAttributes.Table2} order by timeAdded desc"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1

                    if (song_id == id) {
                        return true //song found
                    }
                }
                cursor.close()
            } catch (e: Exception) {
                Log.e(TAG, "read_recentes: $e",)
            }
            return false //not found
        }

        // MOST played --------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun add_mostPlayed(c: Context, mediaStoreId: Long): Boolean {
            try {
                val newEntry = ContentValues().apply {
                    put(PlaylistAttributes.ID, mediaStoreId)
                    put(PlaylistAttributes.playing_count, 1)
                }
                val n = PlaylistDataBase(c).writableDatabase.insert(
                    PlaylistAttributes.Table3,
                    null,
                    newEntry
                )
                // Check if the insert was successful
                if (n != -1L) {
                    return true
                } else {
                    return false
                }

            } catch (e: Exception) {
                Log.e(TAG, "add_mostPlayed: $e",)
                return false
            }
        }

        fun update_mostPlayed(c: Context, mediaStoreId: Long) {
            //incrementing playing_count
            try {
                val db = PlaylistDataBase(c).writableDatabase
                val values = ContentValues()

                //get old number:
                var count = read_mostPlayed(c, mediaStoreId)


                values.put(PlaylistAttributes.playing_count, count + 1) //adding 1

                val whereClause = "${PlaylistAttributes.ID} = ?"
                val whereArgs = arrayOf(mediaStoreId.toString())

                db.update(PlaylistAttributes.Table3, values, whereClause, whereArgs)
                db.close()
            } catch (e: Exception) {
                Log.e(TAG, "update_mostPlayed: $e",)
            }
        }


        fun remove_mostPlayed(c: Context, mediaStoreId: Long): Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Define the WHERE clause to identify the row to be deleted
                val whereClause = "${PlaylistAttributes.ID} = ?"

                // Define the values for the WHERE clause
                val whereArgs = arrayOf(mediaStoreId.toString())

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table3, whereClause, whereArgs)

                db.close()

                //update systemPlaylists:
                read_Recent(c)

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "remove_mostPlayed: $e",)
                return false
            }
        }

        fun read_mostPlayed(c: Context) {
            try {
                //read favorites and add them as a new playlist in data.songsPlaylists
                var songs: MutableList<Song> = ArrayList()

                //return a list of all favorites (ordered from newer to older)
                val sql = "SELECT * FROM ${PlaylistAttributes.Table3} order by playing_count desc"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1
                    val time_added = cursor.getString(1) //because colum index = 2

                    var found = false
                    //search for song by its id:
                    for (i in data.songsList) {
                        if (i.id == song_id) {
                            songs.add(i) //added song to the songList
                            Log.d("storage", "found MostPlayed: ${i.title}")
                            found = true
                            break
                        }
                    }

                    //if a sound in the database don't exist on the device, just remove it from database
                    if (!found) {
                        remove_mostPlayed(c, song_id)
                    }
                    Log.d("storage", "MostPlayed: $song_id - $time_added")
                }
                data.systemPlaylists.add(
                    0,
                    playlist("MostPlayed", songs.size, songs)
                ) //add playlist to songsPlaylists
                cursor.close()

            } catch (e: Exception) {
                Log.e(TAG, "read MostPlayed: $e",)
            }
        }

        fun read_mostPlayed(c: Context, id: Long): Int {
            try {
                val sql =
                    "SELECT ${PlaylistAttributes.playing_count} FROM ${PlaylistAttributes.Table3} where ${PlaylistAttributes.ID}=${id}"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                cursor.moveToNext()
                val count = cursor.getInt(0)//because colum index = 1
                cursor.close()

                return count
            } catch (e: Exception) {
                return 0
                Log.e(TAG, "read MostPlayed: $e",)
            }
        }

        fun exist_mostPlayed(c: Context, id: Long): Boolean {
            try {
                //return true if exist / false if not
                val sql =
                    "SELECT * FROM ${PlaylistAttributes.Table3} where ${PlaylistAttributes.ID}=${id}"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //id is first (colum index =0)
                    val song_id = cursor.getLong(0)//because colum index = 1
                    if (song_id == id) {
                        return true //song found
                    }
                }
                cursor.close()
            } catch (e: Exception) {
                Log.e(TAG, "exist MostPlayed: $e",)
            }
            return false //not found
        }

        // queue --------------------------------------------------------------------------------------------------------------------------------------------------------------
        fun clear_queue(c: Context): Boolean {
            //remove all
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table4, null, null)
                db.close()

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "clear queue: $e",)
                return false
            }
        }

        fun addAll_queue(c: Context): Boolean {
            try {
                var songs = data.queue
                var n: Long = -1L
                for (i in songs) {
                    val newEntry = ContentValues().apply {
                        put(PlaylistAttributes.ID, i.id)
                    }
                    val db = PlaylistDataBase(c).writableDatabase
                    n = db.insert(PlaylistAttributes.Table4, null, newEntry)
                }

                // Check if the insert was successful
                return n != -1L
            } catch (e: Exception) {
                Log.e(TAG, "addAll queue: $e",)
                return false
            }
        }

        fun getAll_queue(c: Context): Boolean {
            try {
                data.queue.clear()

                val sql = "SELECT * FROM ${PlaylistAttributes.Table4}"
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    //"id" is first (colum index =0)
                    val song_id = cursor.getLong(0)

                    //search for song by its id:
                    for (i in data.songsList) {
                        if (i.id == song_id) {
                            data.queue.add(i) //added song to the songList
                            break
                        }
                    }
                }
                cursor.close()
                return true
            } catch (e: Exception) {
                Log.e(TAG, "addAll queue: $e",)
                return false
            }
        }

        // user playlists --------------------------------------------------------------------------------------------------------------------------------------------------------------
        //this table have song for each row, and for each song we have the playlist that it belongs to

        fun addSong(c: Context, mediaStoreId: Long,playlistId:String): Boolean {
            try {
                val newEntry = ContentValues().apply {
                    put(PlaylistAttributes.ID, mediaStoreId)
                    put(PlaylistAttributes.playlistID, playlistId)
                }
                val n = PlaylistDataBase(c).writableDatabase.insert(
                    PlaylistAttributes.Table5,
                    null,
                    newEntry
                )
                // Check if the insert was successful
                Log.d(TAG, "....................... addSong: $mediaStoreId in $playlistId")
                return n != -1L
            } catch (e: Exception) {
                Log.e(TAG, "add song to playlist: $e",)
                return false
            }
        }


        fun removeSong(c: Context, mediaStoreId: Long,playlistId:String): Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Define the WHERE clause to identify the row to be deleted
                val whereClause = "${PlaylistAttributes.ID} = ? and ${PlaylistAttributes.playlistID} = ?"

                // Define the values for the WHERE clause
                val whereArgs = arrayOf(mediaStoreId.toString(),playlistId)

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table5, whereClause, whereArgs)
                db.close()

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "removesong: $e",)
                return false
            }
        }

        fun removePlaylist(c: Context,playlistId:String): Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase

                // Define the WHERE clause to identify the row to be deleted
                val whereClause = "${PlaylistAttributes.playlistID} = ? "

                // Define the values for the WHERE clause
                val whereArgs = arrayOf(playlistId)

                // Perform the deletion
                val rowsDeleted = db.delete(PlaylistAttributes.Table5, whereClause, whereArgs)
                db.close()

                //true: successful
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e(TAG, "remove playlist: $e",)
                return false
            }
        }

        fun getPlaylistsNames(c: Context):ArrayList<String> {
            try {
                val list = ArrayList<String>()

                val sql = "SELECT ${PlaylistAttributes.playlistID} FROM ${PlaylistAttributes.Table5} group by ${PlaylistAttributes.playlistID} "
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf<String>())
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0))
                }
                cursor.close()
                return list
            } catch (e: Exception) {
                return ArrayList()
            }
        }

        fun rename(c: Context,oldName:String,name:String):Boolean {
            try {
                val db = PlaylistDataBase(c).writableDatabase
                val values = ContentValues()

                values.put(PlaylistAttributes.playlistID, name)

                val whereClause = "${PlaylistAttributes.playlistID} = ?"
                val whereArgs = arrayOf(oldName)

                val n = db.update(PlaylistAttributes.Table5, values, whereClause, whereArgs)
                db.close()

                return n>0
            } catch (e: Exception) {
                return false
            }
        }


        fun read(c: Context,playlistId:String):MutableList<Song> {
            try {
                //read favorites and add them as a new playlist in data.songsPlaylists
                val songs: MutableList<Song> = ArrayList()


                //return a list of songs from a playlist
                val sql = "SELECT * FROM ${PlaylistAttributes.Table5} where ${PlaylistAttributes.playlistID}=? "
                //cursor holds rows
                val cursor = PlaylistDataBase(c).readableDatabase.rawQuery(sql, arrayOf(playlistId))
                while (cursor.moveToNext()) {
                    val song_id = cursor.getLong(0)//because colum index = 1
                    var found = false
                    //search for song by its id:
                    for (i in data.songsList) {
                        if (i.id == song_id) {
                            songs.add(i) //added song to the songList
                            found = true

                            break
                        }
                    }
                    //if a songs don't exist on the device, just remove it from database
                    if (!found) {
                        removeSong(c, song_id, playlistId)
                    }
                }

                cursor.close()
                return songs
            } catch (e: Exception) {
                Log.e(TAG, "read song user playlist: $e",)
                return ArrayList<Song>().toMutableList()
            }
        }

    }
}