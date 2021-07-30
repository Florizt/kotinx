package com.florizt.base_mvvm_lib.media.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.*
import android.hardware.Camera.*
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.florizt.base_mvvm_lib.ext.TYPE_VIDEO
import com.florizt.base_mvvm_lib.ext.createDir
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.Comparator
import kotlin.collections.ArrayList


/**
 * 自定义相机，可拍照、录制视频
 * 支持暂停录像、视频分片与视频合并功能
 * Created by wuwei
 * 2021/7/28
 * 佛祖保佑       永无BUG
 */
class CustomCameraManager {
    private val TAG = "CustomCameraManager"
    private var context: Context? = null
    private var cameraCallBack: ICameraCallBack? = null

    //录音状态,默认未开始
    private var status: VideoStatus = VideoStatus.STATUS_NO_READY

    private var tempDirPath: String? = null
    private var mergeDirPath: String? = null
    //合成文件
    private var originFileName: String? = null

    //录音分片文件集合
    private val tempFilePaths = ArrayList<String>()

    private val cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()

    //相机
    private var camera: Camera? = null
    private var parameters: Camera.Parameters? = null

    //角度
    private var sm: SensorManager? = null
    private var sensorAngle = 0

    //预览
    private var isPreviewing = false
    private var holder: SurfaceHolder? = null
    private var viewWidth = 0
    private var viewHeight = 0

    //录像
    private var mediaRecorder: MediaRecorder? = null

    //摄像头
    private var SELECTED_CAMERA = -1
    private var CAMERA_POST_POSITION = -1
    private var CAMERA_FRONT_POSITION = -1


    fun CustomCameraManager(context: Context?, cameraCallBack: ICameraCallBack?) {
        this.context = context
        this.cameraCallBack = cameraCallBack
        findAvailableCameras()
        SELECTED_CAMERA = CAMERA_POST_POSITION
        tempDirPath =
            context?.createDir(TYPE_VIDEO, "video-temp")?.getAbsolutePath()
        mergeDirPath =
            context?.createDir(TYPE_VIDEO, "video-merge")?.getAbsolutePath()
        originFileName = SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA).format(Date())
    }

    private fun findAvailableCameras() {
        val info = CameraInfo()
        val cameraNum: Int = Camera.getNumberOfCameras()
        for (i in 0 until cameraNum) {
            Camera.getCameraInfo(i, info)
            when (info.facing) {
                Camera.CameraInfo.CAMERA_FACING_FRONT -> CAMERA_FRONT_POSITION = info.facing
                Camera.CameraInfo.CAMERA_FACING_BACK -> CAMERA_POST_POSITION = info.facing
            }
        }
    }

    fun doOpenCamera() {
        if (camera == null) {
            openCamera(SELECTED_CAMERA)
        }
        status = VideoStatus.STATUS_READY
        Log.i(TAG, "doOpenCamera----------: cameraOpened")
        cameraCallBack!!.cameraOpened()
    }

    @Synchronized
    private fun openCamera(id: Int) {
        try {
            camera = Camera.open(id)
        } catch (var3: Exception) {
            var3.printStackTrace()
        }
        if (Build.VERSION.SDK_INT > 17) {
            try {
                camera?.also { it.enableShutterSound(false) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun doStartPreview(holder: SurfaceHolder?, viewWidth: Int, viewHeight: Int) {
        if (isPreviewing) {
            Log.i(TAG, "doStartPreview---------: isPreviewing")
        }
        if (holder == null) {
            return
        }
        this.holder = holder
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
        try {
            parameters = camera?.getParameters()
            val previewSize: Camera.Size =
                getSize(parameters?.getSupportedPreviewSizes(), viewWidth, viewHeight)
            val pictureSize: Camera.Size =
                getSize(parameters?.getSupportedPictureSizes(), viewWidth, viewHeight)
            parameters?.setPreviewSize(previewSize.width, previewSize.height)
            parameters?.setPictureSize(pictureSize.width, pictureSize.height)
            if (isSupportedFocusMode(
                    parameters?.getSupportedFocusModes() as List<String>,
                    Camera.Parameters.FOCUS_MODE_AUTO
                )
            ) {
                parameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
            }
            if (isSupportedPictureFormats(
                    parameters?.getSupportedPictureFormats() as List<Int>,
                    ImageFormat.JPEG
                )
            ) {
                parameters?.setPictureFormat(ImageFormat.JPEG)
                parameters?.setJpegQuality(100)
            }
            camera?.setParameters(parameters)
            parameters = camera?.getParameters()
            camera?.setPreviewDisplay(holder) //SurfaceView
            val cameraAngle = getCameraDisplayOrientation(context, SELECTED_CAMERA)
            camera?.setDisplayOrientation(cameraAngle) //浏览角度
            camera?.setPreviewCallback(PreviewCallback { data, camera -> }) //每一帧回调
            camera?.startPreview() //启动浏览
            isPreviewing = true
            Log.i(TAG, " Start Preview ")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun takePicture() {
        if (camera == null) {
            return
        }
        val cameraAngle = getCameraDisplayOrientation(context, SELECTED_CAMERA)
        var tempCurrentAngle = 0
        when (cameraAngle) {
            90 -> tempCurrentAngle = Math.abs(sensorAngle + cameraAngle) % 360
            270 -> tempCurrentAngle = Math.abs(cameraAngle - sensorAngle)
        }
        val currentAngle = tempCurrentAngle
        camera?.takePicture(null, null, PictureCallback { data, camera ->
            var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            val matrix = Matrix()
            if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                matrix.setRotate(currentAngle.toFloat())
            } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                matrix.setRotate((360 - currentAngle).toFloat())
                matrix.postScale(-1f, 1f)
            }
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            if (currentAngle == 90 || currentAngle == 270) {
                cameraCallBack?.takePictureSuccess(bitmap, true)
            } else {
                cameraCallBack?.takePictureSuccess(bitmap, false)
            }
        })
    }

    fun startRecord(surface: Surface?) {
        camera?.setPreviewCallback(null)
        val nowAngle = (sensorAngle + 90) % 360
        if (status == VideoStatus.STATUS_START) {
            return
        }
        if (camera == null) {
            openCamera(SELECTED_CAMERA)
        }
        if (parameters == null) {
            parameters = camera?.getParameters()
        }
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }
        val focusModes: List<String> = parameters?.getSupportedFocusModes() as List<String>
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters?.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
        }
        camera?.setParameters(parameters)
        camera?.unlock()
        mediaRecorder!!.reset()
        mediaRecorder!!.setCamera(camera)
        val mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P)
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder!!.setOutputFormat(mProfile.fileFormat)
        mediaRecorder!!.setVideoEncoder(mProfile.videoCodec)
        mediaRecorder!!.setAudioEncoder(mProfile.audioCodec)
        val videoSize: Camera.Size =
            getSize(parameters?.getSupportedVideoSizes(), viewWidth, viewHeight)
        mediaRecorder!!.setVideoSize(videoSize.width, videoSize.height)
        Log.i(TAG, "startRecord: " + videoSize.width.toString() + "  " + videoSize.height)
        mediaRecorder!!.setVideoFrameRate(mProfile.videoFrameRate)
        mediaRecorder!!.setVideoEncodingBitRate(mProfile.videoBitRate)
        mediaRecorder!!.setAudioEncodingBitRate(mProfile.audioBitRate)
        mediaRecorder!!.setAudioChannels(mProfile.audioChannels)
        mediaRecorder!!.setAudioSamplingRate(mProfile.audioSampleRate)
        val cameraAngle = getCameraDisplayOrientation(context, SELECTED_CAMERA)
        if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) { //手机预览倒立的处理
            if (cameraAngle == 270) { //横屏
                if (nowAngle == 0) {
                    mediaRecorder!!.setOrientationHint(180)
                } else if (nowAngle == 270) {
                    mediaRecorder!!.setOrientationHint(270)
                } else {
                    mediaRecorder!!.setOrientationHint(90)
                }
            } else {
                if (nowAngle == 90) {
                    mediaRecorder!!.setOrientationHint(270)
                } else if (nowAngle == 270) {
                    mediaRecorder!!.setOrientationHint(90)
                } else {
                    mediaRecorder!!.setOrientationHint(nowAngle)
                }
            }
        } else {
            mediaRecorder!!.setOrientationHint(nowAngle)
        }
        mediaRecorder!!.setPreviewDisplay(surface)
        var currentFileName = originFileName
        if (status == VideoStatus.STATUS_PAUSE) { //假如是暂停录像 将文件名后面加个数字,防止重名文件内容被覆盖
            currentFileName = currentFileName + "_" + tempFilePaths.size
        }
        if (!currentFileName!!.endsWith(".mp4")) {
            currentFileName = "$currentFileName.mp4"
        }
        val file = File(tempDirPath, currentFileName)
        if (file.exists()) {
            file.delete()
        }
        tempFilePaths.add(file.getAbsolutePath())
        mediaRecorder?.setOutputFile(file.getAbsolutePath())
        try {
            mediaRecorder!!.prepare()
            mediaRecorder!!.start()
            status = VideoStatus.STATUS_START
        } catch (e: Exception) {
            e.printStackTrace()
            if (file != null && file.exists() && file.length() == 0L) {
                file.delete()
            }
            Log.i(TAG, "startRecord Exception")
        }
    }

    fun clearAllTemp() {
        for (i in tempFilePaths.indices) {
            val file = File(tempFilePaths[i])
            if (file.exists()) {
                file.delete()
            }
        }
    }

    /**
     * 暂停录音
     */
    fun pauseRecord() {
        check(!(status !== VideoStatus.STATUS_START)) { "没有在录像" }
        status = VideoStatus.STATUS_PAUSE
        doStopRecord()
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        check(!(status == VideoStatus.STATUS_NO_READY || status == VideoStatus.STATUS_READY)) { "录像尚未开始" }
        status = VideoStatus.STATUS_STOP
        doStopRecord()
        status = VideoStatus.STATUS_NO_READY
        merge()
    }

    fun merge() {
        if (tempFilePaths.size > 0) {
            var mergeFileName = originFileName
            if (!mergeFileName!!.endsWith(".mp4")) {
                mergeFileName = "$mergeFileName.mp4"
            }
            val mergeFilePath: String = File(mergeDirPath, mergeFileName).getAbsolutePath()
            cachedThreadPool.execute({
                Mp4ParserMerge.mergeVideos(tempFilePaths, mergeFilePath)
                clearAllTemp()
                cameraCallBack?.recordSuccess(File(mergeFilePath))
            })
        }
    }

    private fun doStopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder!!.setOnErrorListener(null)
            mediaRecorder!!.setOnInfoListener(null)
            mediaRecorder!!.setPreviewDisplay(null)
            try {
                mediaRecorder!!.stop()
            } catch (e: Exception) {
                e.printStackTrace()
                mediaRecorder = null
                mediaRecorder = MediaRecorder()
            } finally {
                if (mediaRecorder != null) {
                    mediaRecorder!!.release()
                }
                mediaRecorder = null
            }
            doStopPreview()
        }
    }

    /**
     * 停止预览
     */
    fun doStopPreview() {
        if (null != camera) {
            try {
                camera?.setPreviewCallback(null)
                camera?.stopPreview()
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                camera?.setPreviewDisplay(null)
                isPreviewing = false
                Log.i(TAG, " Stop Preview ")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 销毁Camera
     */
    fun doDestroyCamera() {
        if (null != camera) {
            try {
                camera?.setPreviewCallback(null)
                camera?.stopPreview()
                //这句要在stopPreview后执行，不然会卡顿或者花屏
                camera?.setPreviewDisplay(null)
                holder = null
                isPreviewing = false
                camera?.release()
                camera = null
                Log.i(TAG, " Destroy Camera ")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.i(TAG, " Camera  Null")
        }
    }

    fun registerSensorManager(context: Context) {
        if (sm == null) {
            sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sm!!.registerListener(
            sensorEventListener,
            sm!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unregisterSensorManager(context: Context) {
        if (sm == null) {
            sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        sm!!.unregisterListener(sensorEventListener)
    }

    private val sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (Sensor.TYPE_ACCELEROMETER !== event.sensor.type) {
                return
            }
            val values = event.values
            sensorAngle = getSensorAngle(values[0], values[1])
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun getSensorAngle(x: Float, y: Float): Int {
        return if (Math.abs(x) > Math.abs(y)) {
            /**
             * 横屏倾斜角度比较大
             */
            if (x > 4) {
                /**
                 * 左边倾斜
                 */
                270
            } else if (x < -4) {
                /**
                 * 右边倾斜
                 */
                90
            } else {
                /**
                 * 倾斜角度不够大
                 */
                0
            }
        } else {
            if (y > 7) {
                /**
                 * 左边倾斜
                 */
                0
            } else if (y < -7) {
                /**
                 * 右边倾斜
                 */
                180
            } else {
                /**
                 * 倾斜角度不够大
                 */
                0
            }
        }
    }

    fun getSize(
        data: List<Camera.Size>?,
        viewWidth: Int,
        viewHeight: Int
    ): Camera.Size {
        val list: MutableList<Camera.Size> = ArrayList()
        data?.forEach {
            Log.i(TAG, "doStasasartPreview111: " + it.width.toString() + "  " + it.height)
            if (equalRate(it, 1f * viewHeight / viewWidth)) {
                list.add(it)
            }
        }
        val list2: MutableList<Camera.Size> = ArrayList()
        for (i in list.indices) {
            if (list[i].width <= viewHeight) {
                list2.add(list[i])
            }
        }
        Collections.sort(list2, comp)
        for (i in list2.indices) {
            Log.i(
                TAG,
                "doStasasartPreview222: " + list2[i].width.toString() + "  " + list2[i].height
            )
        }
        Log.i(
            TAG,
            "doStasasartPreview333: " + list2[0].width.toString() + "  " + list2[0].height
        )
        return list2[0]
    }

    private fun equalRate(s: Camera.Size, rate: Float): Boolean {
        val r = s.width as Float / s.height as Float
        return Math.abs(r - rate) <= 0.2
    }

    private val comp: Comparator<Camera.Size> =
        Comparator<Camera.Size> { o1, o2 -> o2.width - o1.width }

    fun isSupportedFocusMode(
        focusList: List<String>,
        focusMode: String
    ): Boolean {
        for (i in focusList.indices) {
            if (focusMode == focusList[i]) {
                Log.i(TAG, "FocusMode supported $focusMode")
                return true
            }
        }
        Log.i(TAG, "FocusMode not supported $focusMode")
        return false
    }

    fun isSupportedPictureFormats(
        supportedPictureFormats: List<Int>,
        jpeg: Int
    ): Boolean {
        for (i in supportedPictureFormats.indices) {
            if (jpeg == supportedPictureFormats[i]) {
                Log.i(TAG, "Formats supported $jpeg")
                return true
            }
        }
        Log.i(TAG, "Formats not supported $jpeg")
        return false
    }

    fun getCameraDisplayOrientation(context: Context?, cameraId: Int): Int {
        val info = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = wm.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }

    interface ICameraCallBack {
        fun cameraOpened()
        fun takePictureSuccess(bitmap: Bitmap, isVertical: Boolean)
        fun recordSuccess(file: File)
    }
}