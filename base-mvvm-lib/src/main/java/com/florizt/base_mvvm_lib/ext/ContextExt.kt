package com.florizt.base_mvvm_lib.ext

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.*
import java.util.*


/**
 * Created by wuwei
 * 2021/7/19
 * 佛祖保佑       永无BUG
 */

//---------------------------------------------------软键盘start----------------------------------------------------
fun Context.showSoftKeyboard(v: View) {
    val systemService = getSystemService(Context.INPUT_METHOD_SERVICE)
    if (systemService is InputMethodManager) {
        systemService.showSoftInput(v, InputMethodManager.SHOW_FORCED)
    }
}

fun Context.hiddenSoftKeyboard(v: View) {
    val systemService = getSystemService(Context.INPUT_METHOD_SERVICE)
    if (systemService is InputMethodManager) {
        systemService.hideSoftInputFromWindow(v.windowToken, 0)
    }
}
//---------------------------------------------------软键盘end----------------------------------------------------

//---------------------------------------------------string、color、dimen等资源start----------------------------------------------------
fun Context.getString(id: Int): String = getString(id)

fun Context.getString(id: Int, vararg formatArgs: Any?) = getString(id, formatArgs)

fun Context.getColor(id: Int): Int = resources.getColor(id)

fun Context.getDimen(id: Int): Int = resources.getDimensionPixelOffset(id)

fun Context.getDensity() = resources.displayMetrics.density

fun Context.dp2px(dp: Float): Int = (dp * getDensity() + 0.5f).toInt()

fun Context.px2dp(px: Int): Int = (px / getDensity() + 0.5f).toInt()

fun Context.sp2px(sp: Float): Int = (sp * getDensity() + 0.5f).toInt()

fun Context.px2sp(px: Float): Int = (px / getDensity() + 0.5f).toInt()
//---------------------------------------------------string、color、dimen等资源end----------------------------------------------------

@Synchronized
fun Context.getSharedPreferences(): SharedPreferences =
    getSharedPreferences(packageName, Context.MODE_PRIVATE)


//---------------------------------------------------assets、raw资源start----------------------------------------------------
fun Context.getDrawable(id: Int, mutate: Boolean): Drawable {
    return if (mutate) {
        resources.getDrawable(id).mutate()
    } else {
        resources.getDrawable(id)
    }
}

fun Context.getDrawableUri(id: Int): String {
    return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            resources.getResourcePackageName(id) + "/" +
            resources.getResourceTypeName(id) + "/" +
            resources.getResourceEntryName(id)
}

fun Context.getAsstManager(): AssetManager = assets

fun Context.openAssets(fileName: String): InputStream = getAsstManager().open(fileName)

fun Context.openRaw(id: Int): InputStream = resources.openRawResource(id)

fun Context.readAsset(fileName: String): List<String> {
    val list: ArrayList<String> = arrayListOf()
    val inputStreamReader = InputStreamReader(openAssets(fileName), "UTF-8")
    val bufferedReader = BufferedReader(inputStreamReader)
    var out: String = ""
    while (bufferedReader.readLine()?.also { out = it } != null) {
        list.add(out)
    }
    return list
}

fun Context.readRaw(id: Int): List<String> {
    val list = arrayListOf<String>()
    val inputStreamReader = InputStreamReader(openRaw(id), "UTF-8")
    val bufferedReader = BufferedReader(inputStreamReader)
    var out: String = ""
    while (bufferedReader.readLine()?.also { out = it } != null) {
        list.add(out)
    }
    return list
}

fun Context.assetsToFile(assetName: String): File? {
    val file: File? = createFile(TYPE_DOWNLOAD, assetName, null);
    return file?.also {
        val ins = assets.open(assetName)
        val fos = FileOutputStream(it)
        val buffer = ByteArray(1024)
        var length = 0
        while (ins.read(buffer).let {
                length = it
                length
            } > 0) {
            fos.write(buffer, 0, length)
        }
        fos.flush();
        ins.close();
        fos.close();
    }
}
//---------------------------------------------------assets、raw资源end----------------------------------------------------


//---------------------------------------------------文件系统start----------------------------------------------------
const val TYPE_IMAGE = 1
const val TYPE_VIDEO = 2
const val TYPE_AUDIO = 3
const val TYPE_DOWNLOAD = 4
const val TYPE_DOCUMENT = 5

/**
 *
 * @receiver Context
 * @param type Int
 * @param fileName String 必须是包含后缀的完整文件名
 * @param subFolder String?
 * @return File?
 */
fun Context.createFile(
    type: Int,
    fileName: String,
    subFolder: String?
): File? {
    if (TextUtils.isEmpty(fileName)) {
        throw java.lang.IllegalArgumentException("filename must not be null")
    }
    val tmpFile: File? = File(createDir(type, subFolder), fileName)
    return tmpFile?.also {
        if (!it.exists()) {
            it.createNewFile()
        }
    }
}

fun Context.createDir(type: Int, subFolder: String?): File? {
    var sub = subFolder
    if (sub != null && sub.startsWith("/") && sub.length > 1) {
        sub = sub.substring(1, sub.length)
    } else {
        sub = ""
    }
    val rootDir: File?
    rootDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        getRootDirFile(type)
    } else {
        val state: String = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStorageDirectory() else cacheDir
    }
    return rootDir?.also {
        if (!it.exists()) {
            it.mkdirs()
        }
    }?.let {
        File(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                it.getAbsolutePath().toString() + "/" + sub
            else
                it.getAbsolutePath().toString() + getParentPath(type, sub)
        )
    }?.also {
        if (!it.exists()) {
            it.mkdirs()
        }
    }
}

fun Context.getRootDirFile(type: Int): File? {
    return when (type) {
        TYPE_VIDEO -> getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        TYPE_AUDIO -> getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        TYPE_IMAGE -> getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        TYPE_DOWNLOAD -> getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        TYPE_DOCUMENT -> getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        else -> getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    }
}

fun Context.getParentPath(
    type: Int,
    subFolder: String?
): String {
    var sub = subFolder
    if (sub != null && sub.startsWith("/") && sub.length > 1) {
        sub = sub.substring(1, sub.length)
    } else {
        sub = ""
    }
    return when (type) {
        TYPE_VIDEO -> "/" + packageName + "/video/" + sub
        TYPE_AUDIO -> "/" + packageName + "/audio/" + sub
        TYPE_IMAGE -> "/" + packageName + "/image/" + sub
        TYPE_DOWNLOAD -> "/" + packageName + "/download/" + sub
        TYPE_DOCUMENT -> "/" + packageName + "/document/" + sub
        else -> "/" + packageName + "/download/" + sub
    }
}

fun Context.saveFile(
    content: String,
    file: File?,
    append: Boolean
) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    try {
        val outStream = FileOutputStream(file, append)
        outStream.write(content.toByteArray())
        outStream.write("\r\n".toByteArray())
        outStream.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
//---------------------------------------------------文件系统end----------------------------------------------------


//---------------------------------------------------Uri适配start----------------------------------------------------
@SuppressLint("NewApi")
fun Context.uriToPath(uri: Uri): String? {
    val context = applicationContext
    // DocumentProvider
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        && DocumentsContract.isDocumentUri(context, uri)
    ) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/" + split[1]
                } else {
                    Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }
            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri: Uri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), toLong(id)
            )
            return getDataColumn(contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(contentUri, selection, selectionArgs)
        }
    } else if ("content".equals(uri.getScheme(), ignoreCase = true)) { // Return the remote address
        return if (isGooglePhotosUri(uri)) {
            uri.getLastPathSegment()
        } else getDataColumn(uri, null, null)
    } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
        return uri.getPath()
    }
    return ""
}

fun Context.fileToUri(file: File?): Uri? {
    val uri: Uri
    val authority = packageName + ".fileprovider"
    uri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) { //通过FileProvider创建一个content类型的Uri
        FileProvider.getUriForFile(this, authority, file!!)
    } else {
        Uri.fromFile(file)
    }
    return uri
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 * @author paulburke
 */
private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 * @author paulburke
 */
private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 * @author paulburke
 */
private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.getAuthority()
}

private fun toLong(o: Any?, defaultValue: Long): Long {
    if (o == null) {
        return defaultValue
    }
    var value: Long = 0
    value = try {
        val s = o.toString().trim { it <= ' ' }
        if (s.contains(".")) {
            java.lang.Long.valueOf(s.substring(0, s.lastIndexOf(".")))
        } else {
            java.lang.Long.valueOf(s)
        }
    } catch (e: java.lang.Exception) {
        defaultValue
    }
    return value
}

private fun toLong(o: Any): Long {
    return toLong(o, 0)
}

fun Context.getDataColumn(
    uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = uri?.let {
            contentResolver.query(
                it, projection, selection, selectionArgs,
                null
            )
        }
        if (cursor != null && cursor.moveToFirst()) {
            val column_index: Int = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } catch (ex: IllegalArgumentException) {

    } finally {
        if (cursor != null) {
            cursor.close()
        }
    }
    return ""
}
//---------------------------------------------------Uri适配end----------------------------------------------------