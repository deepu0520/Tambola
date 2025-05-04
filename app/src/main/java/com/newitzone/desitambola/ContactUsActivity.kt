package com.newitzone.desitambola

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.newitzone.desitambola.databinding.ActivityContactUsBinding
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.PermissionUtil
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import retrofit.TambolaApiService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class ContactUsActivity : AppCompatActivity(), PermissionUtil.PermissionCallback {
    private var context: Context? = null
    private lateinit var login: Result
    private val GALLERY = 1
    private val CAMERA = 2
    private var IMG = ""
    private lateinit var binding: ActivityContactUsBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
       // setContentView(R.layout.activity_contact_us)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        this.context = this@ContactUsActivity
//        ButterKnife.bind(this)
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
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            PermissionUtil.handlePermissionResult(this, Manifest.permission.CAMERA, isGranted, this)
        }

        PermissionUtil.requestPermission(
            activity = this,
            permission = Manifest.permission.CAMERA,
            launcher = permissionLauncher,
            callback = this
        )

        // add image
        binding.textAddAttachment.setOnClickListener { view ->
            showPictureDialog()
        }
        // btn update
        binding.textSubmit.setOnClickListener {
            onSubmitQuery()
        }
    }

    private fun displayDetails(result: Result) {
        binding.textInputEmail.editText?.setText(result.fname + " " + result.lname)
        binding.textInputEmail.editText?.setText(result.emailId)
        binding.textInputMobile.editText?.setText(result.mobileNo)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onSubmitQuery() {
        val name = binding.textInputName.editText!!.text.toString().trim()
        val email = binding.textInputEmail.editText!!.text.toString().trim()
        val mobile = binding.textInputMobile.editText!!.text.toString().trim()
        val query = binding.textInputQuery.editText!!.text.toString().trim()
        val img = ""
        val userid = login.id
        val sessionId = login.sid

        binding.textInputName.error = null
        binding.textInputEmail.error = null
        binding.textInputMobile.error = null
        binding.textInputQuery.error = null

        var cancel = false
        var focusable: View? = null

        if (name.isEmpty()) {
            binding.textInputName.error = "Name cannot be blank"
            focusable = binding.textInputName
            cancel = true
        }
        if (email.isEmpty()) {
            binding.textInputEmail.error = "Email cannot be blank"
            focusable = binding.textInputEmail
            cancel = true
        }
        if (mobile.isEmpty()) {
            binding.textInputMobile.error = "Mobile no cannot be blank"
            focusable = binding.textInputMobile
            cancel = true
        }
        if (query.isEmpty()) {
            binding.textInputQuery.error = "Query cannot be blank"
            focusable = binding.textInputQuery
            cancel = true
        }
        if (cancel) {
            focusable!!.requestFocus()
        } else {
            // TODO: To update bank details
            // this block is only called if form is valid.
            // do something with a valid form state.
            val context = this@ContactUsActivity
            querySubmitApi(context, name, email, mobile, query, img, userid, sessionId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun querySubmitApi(
        context: Context, name: String, email: String, mobile: String,
        query: String, img: String, userid: String, sessionId: String,
    ) {
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        service.querySubmit(name, email, mobile, query, img, userid, sessionId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                MessageDialog(context, "", "${response.body()?.msg}").show()
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
        } else {
            UtilMethods.ToastLong(context, "No Internet Connection")
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    bitmap = UtilMethods.resizeBitmap(bitmap, 200)
                    //val path = saveImage(bitmap)
                    //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    binding.imageAttachment.setImageBitmap(bitmap)
                    IMG = UtilMethods.convert(bitmap).toString()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ContactUsActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        } else if (requestCode == CAMERA) {
            var bitmap = data!!.extras!!.get("data") as Bitmap
            bitmap = UtilMethods.resizeBitmap(bitmap, 200)
            binding.imageAttachment!!.setImageBitmap(bitmap)
            //saveImage(bitmap)
            IMG = UtilMethods.convert(bitmap).toString()
            //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY
        )
        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/dt"
    }

    override fun onPermissionGranted() {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

    }

    override fun onPermissionDenied() {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionPermanentlyDenied() {
        AlertDialog.Builder(this)
            .setTitle("Permission Permanently Denied")
            .setMessage("Please open app settings to allow permission.")
            .setPositiveButton("Open Settings") { _, _ ->
                PermissionUtil.openAppSettings(this)
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    /*private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = false,
        rationaleMessage = "Custom rational message",
        permanentlyDeniedMessage = "Custom permanently denied message",
        rationaleMethod = { req -> rationaleCallback(req) },
        permanentDeniedMethod = { req -> permissionsPermanentlyDenied(req) }
    )

    private fun methodRequiresPermissions() =
        runWithPermissions(Manifest.permission.CAMERA, options = quickPermissionsOption) {
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
            .setMessage(
                "This is the custom permissions permanently denied dialog. " +
                        "Please open app settings to open app settings for allowing permissions, " +
                        "or cancel to end the permission flow."
            )
            .setPositiveButton("App Settings") { dialog, which -> req.openAppSettings() }
            .setNegativeButton("Cancel") { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }*/

    /*private fun whenPermAreDenied(req: QuickPermissionsRequest) {
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
    }*/

}
