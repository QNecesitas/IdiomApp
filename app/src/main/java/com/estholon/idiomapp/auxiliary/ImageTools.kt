package com.estholon.idiomapp.auxiliary

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Environment
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageTools {

    companion object {
        val ANCHO_DE_FOTO_A_SUBIR = 900
        val ALTO_DE_FOTO_A_SUBIR = 900

        fun convertImageString(bitmap: Bitmap?): String? {
            val BitRec =
                Bitmap.createScaledBitmap(bitmap!!, ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            BitRec.compress(Bitmap.CompressFormat.JPEG, 28, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        fun getHoraActual(SumaDeFormatos: String): String {
            return SimpleDateFormat(
                SumaDeFormatos,
                Locale.getDefault()
            ).format(Date())
        }

        @Throws(IOException::class)
        fun createTempImageFile(context: Context, name: String): File {
            val storageDir = context.filesDir
            return File.createTempFile(name, ".png", storageDir)
        }

        @Throws(IOException::class)
        fun obtenerTempImageFile(context: Context , nombre: String): File {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File(storageDir, nombre)
        }

        fun saveImageToInternalStorage(context: Context , bitmap: Bitmap , name: String): String {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val file = File(directory, "$name.jpg")
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return file.absolutePath
        }
    }

}