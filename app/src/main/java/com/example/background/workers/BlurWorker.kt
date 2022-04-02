package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.IllegalArgumentException


private const val TAG = "BlurWorker"
class BlurWorker(ctx : Context, params : WorkerParameters) : Worker(ctx, params) {

    val err = "Invalid input uri"

    override fun doWork(): Result {




        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Image being blurred...", appContext)

        //slow down the worker
        sleep()


        return try {

//            val picture = BitmapFactory.decodeResource(
//                appContext.resources,
//                R.drawable.android_cupcake
//            )

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, err)
                throw IllegalArgumentException(err)
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(
                    Uri.parse(resourceUri)))

            val blur = blurBitmap(picture, appContext)
            val uri = writeBitmapToFile(appContext, blur)

            makeStatusNotification("wrote to $uri", appContext)


            val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())
            Result.success(outputData)

        } catch (e:Throwable) {
            Log.e("TAG", "Error applying blur")
            Result.failure()
        }
    }

}