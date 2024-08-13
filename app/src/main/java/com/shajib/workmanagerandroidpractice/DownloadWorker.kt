package com.shajib.workmanagerandroidpractice

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author Shajib
 * @since Aug 12, 2024
 **/
class DownloadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var context: Context? = null

    init {
        this.context = context
    }

    override fun doWork(): Result {
        try {
            //take imageUrl using "url" key from inputData and connect with HttpURLConnection
            val imageUrl = URL(inputData.getString("url"))
            val conn: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
            conn.setDoInput(true)
            conn.connect()

            //convert imageUrl to Bitmap using BitmapFactory using BitmapFactory.Options()
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmapImage = BitmapFactory.decodeStream(conn.inputStream, null, options)

            //create a local storage
            val folder = context?.getExternalFilesDir(null)
            val file = File(folder, "testFile1")
            file.createNewFile()

            //convert Bitmap to ByteArray using ByteArrayOutputStream()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmapImage?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val bitmapData = byteArrayOutputStream.toByteArray()

            //create a FileOutputStream to write (save) ByteArray to local storage
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(bitmapData)
            fileOutputStream.flush()
            fileOutputStream.close()
            conn.disconnect()

            return Result.success(Data.Builder().putString("filePath", file.absolutePath).build())

        } catch (e: Exception) {
            e.printStackTrace()

            val errorMessage = Data.Builder()
                .putString("ERROR", e.message)
                .build()
            return Result.failure(errorMessage)
        }
    }
}