package com.newitzone.desitambola.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.newitzone.desitambola.R
import java.io.ByteArrayOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


/**
 * Created by imran on 27/Dec/2017.
 */
object UtilMethods {

    private lateinit var progressDialogBuilder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private val TAG: String = "---UtilMethods"

    /**
     * @param context
     * @action show progress loader
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showLoading(context: Context){
        progressDialogBuilder = AlertDialog.Builder(context)
        progressDialogBuilder.setCancelable(false) // if you want user to wait for some process to finish,
        progressDialogBuilder.setView(R.layout.layout_loading_dialog)

        progressDialog = progressDialogBuilder.create()
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT));
        try {
            progressDialog.show()
        }catch (ex: WindowManager.BadTokenException){
            Log.e(TAG, ex.toString())
        }
    }

    /**
     * @action hide progress loader
     */
    fun hideLoading(){
        try {
            progressDialog.dismiss()
        }catch (ex: WindowManager.BadTokenException){
            Log.e(TAG, ex.toString())
        }

    }


    /**
     * @param context
     * @action show Long toast message
     */
    fun ToastLong(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    fun ToastSort(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    /**
     * @param context
     * @return true or false mentioning the device is connected or not
     * @brief checking the internet connection on run time
     */
    fun isConnectedToInternet(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val allNetworks = manager?.allNetworks?.let { it } ?: return false
            allNetworks.forEach { network ->
                val info = manager.getNetworkInfo(network)
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        } else {
            val allNetworkInfo = manager?.allNetworkInfo?.let { it } ?: return false
            allNetworkInfo.forEach { info ->
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        }
        return false
    }

    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap? {
        val decodedBytes: ByteArray = Base64.decode(
            base64Str.substring(base64Str.indexOf(",") + 1),
            Base64.DEFAULT
        )
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convert(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    fun resizeBitmap(source: Bitmap, maxLength: Int): Bitmap {
        try {
            if (source.height >= source.width) {
                if (source.height <= maxLength) { // if image height already smaller than the required height
                    return source
                }

                val aspectRatio = source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                val result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                return result
            } else {
                if (source.width <= maxLength) { // if image width already smaller than the required width
                    return source
                }

                val aspectRatio = source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()

                val result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                return result
            }
        } catch (e: Exception) {
            return source
        }
    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
    fun roundOffDecimal(number: Float): Float? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toFloat()
    }
    fun getTime(inTime: String): String{
        try {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
                println("get time: "+inTime.format(dateTimeFormatter))
                inTime.format(dateTimeFormatter)
            }else{
                val formatDate = SimpleDateFormat("hh:mm a")
                println("get time: "+inTime.format(formatDate))
                inTime.format(formatDate)
            }
        }catch (e: Exception){
            Log.e(TAG, "Time exception : $e")
        }
        return inTime
    }
    fun getDateInMS(inputDate: String?): Long? {
        var inputMilliseconds = 0L
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date: Date = sdf.parse(inputDate)
            inputMilliseconds = date.time
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Get Date Exception : $e")
        }
        return inputMilliseconds
    }
    fun getDatetimeDDMMYYYY_HHMMSS(inputDate: String): String {
        return try {
            var sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = sdf.parse(inputDate)

            sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
            sdf.format(date)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Get Date Exception : $e")
            inputDate
        }
    }
    fun getDateYYYYMMDD(inputDate: String): String {
        return try {
            var sdf = SimpleDateFormat("dd-MM-yyyy")
            val date = sdf.parse(inputDate)

            sdf = SimpleDateFormat("yyyy-MM-dd")
            sdf.format(date)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Get Date Exception : $e")
            inputDate
        }
    }
    fun getTimeAMPM(inputDatetime: String): String? {
        var outputTime = inputDatetime
        try {
            var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val date = sdf.parse(inputDatetime)
            sdf = SimpleDateFormat("hh:mm a")
            outputTime = sdf.format(date)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Get time exception : $e")
        }
        return outputTime.toUpperCase()
    }
    fun getTimeDifferenceInMinute(fromTime: Long, toTime: Long, type: Int): Long {
        var different = 0L
        try {
            if (type == 0) { // type 0 is fromTime is greater than toTime
                if (fromTime > toTime!!) {
                    different = fromTime.minus(toTime)
                }
            }else if (type == 1) { // type 1 is greater value of fromTime and toTime
                if (fromTime > toTime!!) {
                    different = fromTime.minus(toTime)
                }else if (toTime!! > fromTime) {
                    different = toTime?.minus(fromTime)
                }
            }else if (type == 2) { // type 2 is toTime is greater than fromTime
                if (toTime!! > fromTime) {
                    different = toTime?.minus(fromTime)
                }
            }
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60

            val elapsedMinutes: Long = different / minutesInMilli
            different %= elapsedMinutes
            // TODO: Final value
            different = elapsedMinutes
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Get time exception : $e")
        }
        return different
    }
    fun isAutoTime(context: Context): Int{
        return android.provider.Settings.Global.getInt(context.contentResolver, android.provider.Settings.Global.AUTO_TIME, 0)
    }

    const val MOBILE = 0
    const val EMAIL = 1
    const val PAN_NO = 2
    const val GSTIN = 3
    const val PINCODE = 4
    const val CHEQUE_NO = 5
    const val DATE = 6
    const val IFSC = 7

    // TODO: Mobile No etc. Validate
    fun validateByRegex(str: String, RegexType: Int): String {
        var result = ""
        when (RegexType) {
            MOBILE -> { // Mobile No
                val regex = Regex("^[6-9][0-9]{9}\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "Mobile number is not valid"
                }
            }
            EMAIL -> { // Email
                val regex = Regex("^([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "Email is not valid"
                }
            }
            PAN_NO -> { // Pan No
                val regex = Regex("^[A-Za-z]{5}\\d{4}[A-Za-z]{1}\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "PAN number is not valid"
                }
            }
            GSTIN -> { // GSTIN
                val regex = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "GST number is not valid"
                }
            }
            PINCODE -> { // PinCode
                val regex = Regex("^[1-9][0-9]{5}$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "Pin code is not valid"
                }
            }
            CHEQUE_NO -> { // Cheque
                val regex = Regex("^[1-9][0-9]{5}$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "Cheque number is not valid"
                }
            }
            DATE -> { // Date
                val regex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "Date is not valid"
                }
            }
            IFSC -> { // Date
                val regex = Regex("[A-Z|a-z]{4}[0][\\d]{6}\$")
                result = if (str.matches(regex)) {
                    ""
                } else {
                    "IFSC code is not valid"
                }
            }
        }
        return result
    }

}