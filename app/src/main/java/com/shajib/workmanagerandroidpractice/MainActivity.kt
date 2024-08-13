package com.shajib.workmanagerandroidpractice

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = Data.Builder()
            .putString("url", "https://www.gstatic.com/webp/gallery/1.webp")
            .build()

        val workManager = WorkManager.getInstance(this)
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .addTag("download")
            .setInputData(Data.Builder().putString("url", "https://www.gstatic.com/webp/gallery/1.webp").build())
            .build()

        workManager.enqueueUniqueWork(
            "download" + System.currentTimeMillis().toString(),
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )

        //Taking the WorkInfo from DownloadWorker
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this, object : Observer<WorkInfo> {
            override fun onChanged(value: WorkInfo) {
                if(value.state == WorkInfo.State.SUCCEEDED) {
                    val filePath = value.outputData.getString("filePath") //get filePath from inputData using key "filePath" from DownloadWorker
                    val bitmap = BitmapFactory.decodeFile(filePath) //get bitmap from filePath

                    //show image using bitmap
                    findViewById<ImageView>(R.id.ivImage).setImageBitmap(bitmap)
                }
            }
        })
    }
}