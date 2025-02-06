package edu.victorlamas.fandom.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import edu.victorlamas.fandom.R
import edu.victorlamas.fandom.models.Fandom
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

var fandomMutableList = mutableListOf<Fandom>()

fun readRawFile(context: Context): MutableList<Fandom> {
    // Lee los archivos y crea dos listas de favoritos y eliminados
    val listOfFandoms = mutableListOf<Fandom>()
    val listOfFavs = readFandomOptions(context, R.string.filenameFavs)
    val listOfDeletes = readFandomOptions(context, R.string.filenameDeleted)

    try {
        val input = InputStreamReader(context.resources.openRawResource(R.raw.data))
        val br = BufferedReader(input)
        var linea = br.readLine()

        while (!linea.isNullOrEmpty()) {
            val data = linea.split(";")

            val id = data[0].toInt()
            val name = data[1]
            val universe = data[2]
            val description = data[3]
            val image = data[4]
            val info = data[5]

            val fav: Boolean = listOfFavs.contains(id)
            val visible: Boolean = !listOfDeletes.contains(id)

            listOfFandoms.add(Fandom(id, name, universe, description, image, info, fav, visible))

            linea = br.readLine()
        }
        br.close()
        input.close()

        listOfFandoms.sortBy { fandom -> fandom.name }

        return listOfFandoms.filter { fandom -> fandom.visible }.toMutableList()

    } catch (e: Exception) {
        Log.e("ERROR IO", e.message.toString())
        return mutableListOf()
    }
}

// Actualiza las listas de favs y deletes y sus correspondientes archivos
fun updateFilesOptions(context: Context, file: Int) {
    if (file == R.string.filenameFavs) {
        val listOfFavs = mutableListOf<Int>()
        listOfFavs.addAll(fandomMutableList.filter { fandom -> fandom.fav }
            .map { fandom -> fandom.id })
        listOfFavs.sort()
        writeFandomOptions(context, file, listOfFavs)
    } else if (file == R.string.filenameDeleted) {
        val listOfDeletes = readFandomOptions(context, R.string.filenameDeleted)
        listOfDeletes.addAll(fandomMutableList.filter { fandom -> !fandom.visible }
            .map { fandom -> fandom.id })
        listOfDeletes.sort()
        writeFandomOptions(context, file, listOfDeletes)
    }
}

fun deleteFilesOptions(context: Context) {
    context.deleteFile(context.getString(R.string.filenameFavs))
    context.deleteFile(context.getString(R.string.filenameDeleted))
}

// Lee el archivo de favoritos o de eliminados para devolver una lista de IDs
private fun readFandomOptions(context: Context, file: Int): MutableList<Int> {
    val listOfFandomIds: MutableList<Int> = mutableListOf()

    if (context.fileList().contains(context.getString(file))) {
        try {
            val input = InputStreamReader(context.openFileInput(context.getString(file)))
            val br = BufferedReader(input)
            var linea = br.readLine()

            // Si la línea no está vacía, se añade el ID a la lista
            while(!linea.isNullOrEmpty()) {
                listOfFandomIds.add(linea.toInt())
                linea = br.readLine()
            }

            br.close()
            input.close()
        } catch (e: IOException) {
            Log.e("ERROR IO", e.message.toString())
            listOfFandomIds.clear()
            return listOfFandomIds
        }
    }

    return listOfFandomIds
}

// Escribe en el archivo de favoritos o de eliminados
private fun writeFandomOptions(
    context: Context,
    file: Int,
    listOfFandomIds: MutableList<Int>
) {
    Log.d("WRITE", listOfFandomIds.toString())
    try {
        // Si el fichero no existe se crea; si existe se sobrescribe
        val output = OutputStreamWriter(context.openFileOutput(
            context.getString(file),
            Activity.MODE_PRIVATE
        ))

        // Se escribe en el fichero línea a línea
        for (id in listOfFandomIds) {
            output.write("$id\n")
        }

        // Se confirma la escritura.
        output.flush()
        output.close()

    } catch (e: IOException) {
        Log.e("ERROR IO", e.message.toString())
    }
}