package com.newitzone.desitambola

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.vvalidator.form
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.internal.LinkedTreeMap
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.UtilMethods
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.text_input_email
import kotlinx.android.synthetic.main.activity_profile.text_input_mobile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.TambolaApiService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import model.login.Result
import java.util.*

class ContactUsActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private val GALLERY = 1
    private val CAMERA = 2
    private var IMG = ""
    //
    @BindView(R.id.text_submit) lateinit var txtSubmit: TextView
    @BindView(R.id.text_add_attachment) lateinit var txtAddAttachment: TextView
    @BindView(R.id.image_attachment) lateinit var imgAttachment: ImageView

    @BindView(R.id.text_input_name) lateinit var tInputName: TextInputLayout
    @BindView(R.id.text_input_email) lateinit var tInputEmail: TextInputLayout
    @BindView(R.id.text_input_mobile) lateinit var tInputMobile: TextInputLayout
    @BindView(R.id.text_input_query) lateinit var tInputQuery: TextInputLayout

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_contact_us)
        supportActionBar?.hide()
        this.context = this@ContactUsActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        // Intent
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        // display details of profile
        displayDetails(login)
        // permission
        methodRequiresPermissions()
        // add image
        txtAddAttachment.setOnClickListener { view ->
            showPictureDialog()
        }
        // btn update
        txtSubmit.setOnClickListener {
            onSubmitQuery()
        }
    }
    
    private fun displayDetails(result: Result) {
        tInputName.editText?.setText(result.fname + " "+ result.lname)
        tInputEmail.editText?.setText(result.emailId)
        tInputMobile.editText?.setText(result.mobileNo)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onSubmitQuery(){
        val name = text_input_name.editText!!.text.toString().trim()
        val email = text_input_email.editText!!.text.toString().trim()
        val mobile = text_input_mobile.editText!!.text.toString().trim()
        val query = text_input_query.editText!!.text.toString().trim()
        val img = ""
        val userid = login.id
        val sessionId = login.sid

        text_input_name.error = null
        text_input_email.error = null
        text_input_mobile.error = null
        text_input_query.error = null

        var cancel = false
        var focusable: View? = null

        if(name.isEmpty()){
            text_input_bank_name.error = "Name cannot be blank"
            focusable = text_input_name
            cancel = true
        }
        if(email.isEmpty()){
            text_input_email.error = "Email cannot be blank"
            focusable = text_input_email
            cancel = true
        }
        if(mobile.isEmpty()){
            text_input_mobile.error = "Mobile no cannot be blank"
            focusable = text_input_mobile
            cancel = true
        }
        if(query.isEmpty()){
            text_input_query.error = "Query cannot be blank"
            focusable = text_input_query
            cancel = true
        }
        if (cancel){
            focusable!!.requestFocus()
        }else {
            // TODO: To update bank details
            // this block is only called if form is valid.
            // do something with a valid form state.
            val context = this@ContactUsActivity
            querySubmitApi(context, name, email, mobile, query, img, userid, sessionId)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun querySubmitApi(context: Context, name: String, email: String, mobile: String
                               , query: String, img: String, userid: String, sessionId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.querySubmit(name,email,mobile,query,img,userid,sessionId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                MessageDialog(context, "","${response.body()?.msg}").show()
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Ooops: Something else went wrong : " + e.message
                            )
                        }
                        UtilMethods.hideLoading()
                    }
                } catch (e: Throwable) {
                    runOnUiThread {
                        UtilMethods.ToastLong(context, "Server or Internet error : ${e.message}")
                    }
                    Log.e("TAG", "Throwable : $e")
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    bitmap = UtilMethods.resizeBitmap(bitmap,200)
                    //val path = saveImage(bitmap)
                    //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imgAttachment!!.setImageBitmap(bitmap)
                    IMG = UtilMethods.convert(bitmap).toString()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ContactUsActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            var bitmap = data!!.extras!!.get("data") as Bitmap
            bitmap = UtilMethods.resizeBitmap(bitmap,200)
            imgAttachment!!.setImageBitmap(bitmap)
            //saveImage(bitmap)
            IMG = UtilMethods.convert(bitmap).toString()
            //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {

            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/dt"
    }

    private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = false,
        rationaleMessage = "Custom rational message",
        permanentlyDeniedMessage = "Custom permanently denied message",
        rationaleMethod = { req -> rationaleCallback(req) },
        permanentDeniedMethod = { req -> permissionsPermanentlyDenied(req) }
    )

    private fun methodRequiresPermissions() = runWithPermissions(Manifest.permission.CAMERA, options = quickPermissionsOption) {
        Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
    }
    private fun rationaleCallback(req: QuickPermissionsRequest) {
        // this will be called when permission is denied once or more time. Handle it your way
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom rationale dialog. Please allow us to proceed " + "asking for permissions again, or cancel to end the permission flow.")
            .setPositiveButton("Go Ahead") { dialog, which -> req.proceed() }
            .setNegativeButton("cancel") { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        // this will be called when some/all permissions required by the method are permanently
        // denied. Handle it your way.
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom permissions permanently denied dialog. " +
                    "Please open app settings to open app settings for allowing permissions, " +
                    "or cancel to end the permission flow.")
            .setPositiveButton("App Settings") { dialog, which -> req.openAppSettings() }
            .setNegativeButton("Cancel") { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    private fun whenPermAreDenied(req: QuickPermissionsRequest) {
        // handle something when permissions are not granted and the request method cannot be called
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom permissions denied dialog. \n${req.deniedPermissions.size}/${req.permissions.size} permissions denied")
            .setPositiveButton("OKAY") { _, _ -> }
            .setCancelable(false)
            .show()
//        val toast = Toast.makeText(this, req.deniedPermissions.size.toString() + " permission(s) denied. This feature will not work.", Toast.LENGTH_LONG)
//        toast.setGravity(Gravity.CENTER, 0, 0)
//        toast.show()
    }

}
