package com.shajib.workmanagerandroidpractice

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = Data.Builder()
            .putString("url", "https://www.gstatic.com/webp/gallery/1.webp")
            .build()

        val workManager = WorkManager.getInstance(this)
        //one time work request
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .addTag("download")
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "download" + System.currentTimeMillis().toString(),
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )

        //periodic work request
        /*val periodicWorkRequest = PeriodicWorkRequestBuilder<DownloadWorker>(
            15,
            TimeUnit.MINUTES
        ) //auto repeat every 15 minutes
            .addTag("periodicDownload")
            .setInputData(data)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodicDownload" + System.currentTimeMillis().,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )*/


        //Taking the WorkInfo from DownloadWorker
        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this, object : Observer<WorkInfo> {
                override fun onChanged(value: WorkInfo) {
                    if (value.state == WorkInfo.State.SUCCEEDED) {
                        val filePath =
                            value.outputData.getString("filePath") //get filePath from inputData using key "filePath" from DownloadWorker
                        val bitmap = BitmapFactory.decodeFile(filePath) //get bitmap from filePath

                        //show image using bitmap
                        findViewById<ImageView>(R.id.ivImage).setImageBitmap(bitmap)
                    }
                }
            })
    }
}