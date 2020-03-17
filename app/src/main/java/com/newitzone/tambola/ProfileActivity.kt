package com.newitzone.tambola

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
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.contentcapture.ContentCaptureSessionId
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.vvalidator.form
import com.google.android.material.textfield.TextInputLayout
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.newitzone.tambola.utils.UtilMethods
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import retrofit.TambolaApiService
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private val GALLERY = 1
    private val CAMERA = 2
    private var IMG = ""
    //
    @BindView(R.id.text_update) lateinit var txtUpdate: TextView
    @BindView(R.id.text_add_image) lateinit var txtAddImg: TextView
    @BindView(R.id.image_profile) lateinit var imgProfile: ImageView

    @BindView(R.id.text_input_fname) lateinit var tInputfName: TextInputLayout
    @BindView(R.id.text_input_lname) lateinit var tInputlName: TextInputLayout
    @BindView(R.id.text_input_email) lateinit var tInputEmail: TextInputLayout
    @BindView(R.id.text_input_mobile) lateinit var tInputMobile: TextInputLayout
    @BindView(R.id.text_input_password) lateinit var tInputPassword: TextInputLayout
    @BindView(R.id.text_input_confirm_password) lateinit var tInputlConfirmPassword: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()
        this.context = this@ProfileActivity
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
        txtAddImg.setOnClickListener { view ->
            showPictureDialog()
        }
        // btn update
        txtUpdate.setOnClickListener { view ->
            val fName = text_input_fname.editText!!.text.toString().trim()
            val lName = text_input_lname.editText!!.text.toString().trim()
//            val email = text_input_email.editText!!.text.toString().trim()
            val mobile = text_input_mobile.editText!!.text.toString().trim()
            val dob = ""
            val password = text_input_password.editText!!.text.toString().trim()
            val confirmPassword = text_input_confirm_password.editText!!.text.toString().trim()
            val img = ""
            val userid = login.id
            val sessionId = login.sid
            form {
                inputLayout(R.id.text_input_fname , name = "First Name") {
                    isNotEmpty()
                }
                inputLayout(R.id.text_input_lname , name = "Last Name") {
                    isNotEmpty()
                }

                submitWith(R.id.text_update) { result ->
                    // this block is only called if form is valid.
                    if (password.equals(confirmPassword)) {
                        // do something with a valid form state.
                        context?.let {
                            updateProfileApi(
                                it,
                                fName,
                                lName,
                                mobile,
                                password,
                                userid,
                                sessionId,
                                dob,
                                img
                            )
                        }
                    }else{
                        input(R.id.input_confirm_password , name = "Confirm Password") {
                            context?.let { UtilMethods.ToastLong(it,"Password is not match") }
                        }
                    }
                }
            }
        }
    }
    private fun displayDetails(result: Result) {
        tInputfName.editText?.setText(result.fname)
        tInputlName.editText?.setText(result.lname)
        tInputEmail.editText?.setText(result.emailId)
        tInputMobile.editText?.setText(result.mobileNo)
        if (result.img.isNotEmpty()) {
            // load the image with Picasso
            Picasso.get().load(result.img).into(imgProfile) // select the ImageView to load it into
        }
    }

    private fun updateProfileApi(context: Context,fname: String,lname: String
                                 ,mobileNo: String,passkey: String,userid: String
                                 ,sessionId: String,dob: String,img: String){
        val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.updateProfile(fname,lname,mobileNo,passkey,userid,sessionId,dob,img)
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        UtilMethods.ToastLong(context,"${response.body()?.msg}")
                    } else {
                        UtilMethods.ToastLong(context,"${response.body()?.msg}")
                    }
                } catch (e: Exception) {
                    UtilMethods.ToastLong(context,"Exception ${e.message}")
                } catch (e: Throwable) {
                    UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message)
                }
            }
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
                    imgProfile!!.setImageBitmap(bitmap)
                    IMG = UtilMethods.convert(bitmap).toString()
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ProfileActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            var bitmap = data!!.extras!!.get("data") as Bitmap
            bitmap = UtilMethods.resizeBitmap(bitmap,200)
            imgProfile!!.setImageBitmap(bitmap)
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
