package com.florizt.base_mvvm_lib.media.camera

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.florizt.base_mvvm_lib.ext.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
class SystemCameraManager {
    private var context: Context? = null
    private var iCameraCallBack: ICameraCallBack? = null

    private val REQUESTCODE_ALBUM = 100
    private val REQUESTCODE_IMAGE = 101
    private val REQUESTCODE_VIDEO = 102

    private var file: File? = null

    fun SystemCameraManager(
        context: Context?,
        iCameraCallBack: ICameraCallBack?
    ) {
        this.context = context
        this.iCameraCallBack = iCameraCallBack
    }

    fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        context?.also {
            if (it is Activity) {
                it.startActivityForResult(intent, REQUESTCODE_ALBUM)
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    fun startOpenCameraPicture() {
        var fileName: String =
            SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA).format(Date())
        if (!fileName.endsWith(".jpeg")) {
            fileName = "$fileName.jpeg"
        }
        context?.also {
            file = File(it.createDir(TYPE_IMAGE, ""), fileName)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (intent.resolveActivity(it.getPackageManager()) != null) {
                val imageUri: Uri? = it.fileToUri(file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                if (it is Activity) {
                    it.startActivityForResult(intent, REQUESTCODE_IMAGE)
                }
            }
        }
    }

    /**
     * start to camera、video
     */
    fun startOpenCameraVideo(videoQuality: Int) {
        startOpenCameraVideo(0, videoQuality)
    }

    /**
     * start to camera、video
     */
    fun startOpenCameraVideo(recordVideoSecond: Int, videoQuality: Int) {
        var fileName: String =
            SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA).format(Date())
        if (!fileName.endsWith(".mp4")) {
            fileName = "$fileName.mp4"
        }
        context?.also {
            file = File(it.createDir(TYPE_VIDEO, ""), fileName)
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (intent.resolveActivity(it.getPackageManager()) != null) {
                if (recordVideoSecond > 0) {
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordVideoSecond)
                }
                val imageUri: Uri? = it.fileToUri(file)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality)
                if (it is Activity) {
                    it.startActivityForResult(intent, REQUESTCODE_VIDEO)
                }
            }
        }
    }

    fun startCrop(
        file: File?, aspectX: Int, aspectY: Int,
        outputX: Int, outputY: Int, requestCode: Int
    ) {
        val intent = Intent("com.android.camera.action.CROP")
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context?.also {
            intent.setDataAndType(it.fileToUri(file), "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, it.fileToUri(file))
            intent.putExtra("aspectX", aspectX)
            intent.putExtra("aspectY", aspectY)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG)
            intent.putExtra("outputX", outputX)
            intent.putExtra("outputY", outputY)
            intent.putExtra("scale", true)
            intent.putExtra("scaleUpIfNeeded", true)
            intent.putExtra("return-data", false)
            if (it is Activity) {
                it.startActivityForResult(intent, requestCode)
            }
        }
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int, intent: Intent?
    ) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_ALBUM) {
                intent?.let {
                    intent.data
                }?.let {
                    context?.uriToPath(it)
                }?.let {
                    File(it)
                }?.let {
                    iCameraCallBack?.onSuccess(it)
                }
            } else if (requestCode == REQUESTCODE_IMAGE) {
                file?.also {
                    iCameraCallBack?.onSuccess(it)
                }
            } else if (requestCode == REQUESTCODE_VIDEO) {
                file?.also {
                    iCameraCallBack?.onSuccess(it)
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            file?.also {
                if (it.exists() && it.length() == 0L) {
                    it.delete()
                }
            }
        }
    }

    interface ICameraCallBack {
        fun onSuccess(file: File)
    }
}