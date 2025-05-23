package com.newitzone.desitambola

import android.Manifest
import android.app.Activity
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
import com.newitzone.desitambola.utils.UtilMethods
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
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

class ProfileActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private val GALLERY = 1
    private val CAMERA = 2
    private var IMG = ""
    //
    @BindView(R.id.text_bank_details_update) lateinit var txtBankDetailsUpdate: TextView
    @BindView(R.id.text_update) lateinit var txtUpdate: TextView
    @BindView(R.id.text_add_image) lateinit var txtAddImg: TextView
    @BindView(R.id.image_profile) lateinit var imgProfile: ImageView

    @BindView(R.id.text_input_fname) lateinit var tInputfName: TextInputLayout
    @BindView(R.id.text_input_lname) lateinit var tInputlName: TextInputLayout
    @BindView(R.id.text_input_email) lateinit var tInputEmail: TextInputLayout
    @BindView(R.id.text_input_mobile) lateinit var tInputMobile: TextInputLayout
    @BindView(R.id.text_input_password) lateinit var tInputPassword: TextInputLayout
    @BindView(R.id.text_input_confirm_password) lateinit var tInputlConfirmPassword: TextInputLayout

    @BindView(R.id.text_input_bank_name) lateinit var tInputBankName: TextInputLayout
    @BindView(R.id.text_input_ac_holder_name) lateinit var tInputAcHoldName: TextInputLayout
    @BindView(R.id.text_input_account_no) lateinit var tInputAcNo: TextInputLayout
    @BindView(R.id.text_input_confirm_account_no) lateinit var tInputConfirmAcNo: TextInputLayout
    @BindView(R.id.text_input_ifsc) lateinit var tInputIfsc: TextInputLayout

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
        txtUpdate.setOnClickListener {
            onUpdateProfile()
        }
        txtBankDetailsUpdate.setOnClickListener {
            onUpdateBankDetails()
        }
    }

    override fun onResume() {
        super.onResume()
        if (login != null) {
            // display details of profile
            context?.let { getBankDetailsApi(it, login.id, login.sid) }
        }
    }
    private fun displayDetails(result: Result) {
        tInputfName.editText?.setText(result.fname)
        tInputlName.editText?.setText(result.lname)
        tInputEmail.editText?.setText(result.emailId)
        tInputMobile.editText?.setText(result.mobileNo)
        if (result.userType.toInt() == 0) {
            if (result.img.isNotEmpty()) {
                // load the image with Picasso
                Picasso.get().load(result.img).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgProfile) // select the ImageView to load it into
            }
        }else if (result.userType.toInt() == 1) {
            //val user = context?.let { LoginActivity.getUserInfo(it, login.id) }
            if (result.fbImg.isNotEmpty()) {
                // load the image with Picasso
                Picasso.get().load(result.fbImg).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgProfile) // select the ImageView to load it into
            }
        }
    }
    private fun displayBankDetails(result: List<model.bankdetails.Result>?) {
        if (result != null) {
            tInputBankName.editText?.setText(result.first().bankName)
            tInputAcHoldName.editText?.setText(result.first().accountHolder)
            tInputAcNo.editText?.setText(result.first().accountNo)
            tInputConfirmAcNo.editText?.setText(result.first().accountNo)
            tInputIfsc.editText?.setText(result.first().ifcsCode)
//            val getRow: Any = result.first()
//            val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>
//            tInputBankName.editText?.setText(t["bank_name"] as CharSequence)
//            tInputAcHoldName.editText?.setText(t["account_holder"] as CharSequence)
//            tInputAcNo.editText?.setText(t["account_no"] as CharSequence)
//            tInputConfirmAcNo.editText?.setText(t["account_no"] as CharSequence)
//            tInputIfsc.editText?.setText(t["ifcs_code"] as CharSequence)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onUpdateProfile(){
        val fName = text_input_fname.editText!!.text.toString().trim()
        val lName = text_input_lname.editText!!.text.toString().trim()
//            val email = text_input_email.editText!!.text.toString().trim()
        val mobile = text_input_mobile.editText!!.text.toString().trim()
        val dob = ""
        val password = text_input_password.editText!!.text.toString().trim()
        val confirmPassword = text_input_confirm_password.editText!!.text.toString().trim()
        val img = IMG
        val userid = login.id
        val sessionId = login.sid

        text_input_fname.error = null
        text_input_lname.error = null
        text_input_password.error = null
        text_input_confirm_password.error = null

        var cancel = false
        var focusable: View? = null

        if(fName.isEmpty()){
            text_input_fname.error = "First name cannot be blank"
            focusable = text_input_fname
            cancel = true
        }
        if(lName.isEmpty()){
            text_input_lname.error = "Last name cannot be blank"
            focusable = text_input_lname
            cancel = true
        }
        if(password != confirmPassword){
            text_input_confirm_password.error = "Password is not match"
            focusable = text_input_confirm_password
            cancel = true
        }
        if (cancel){
            focusable!!.requestFocus()
        }else{
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
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onUpdateBankDetails(){
        val bankName = text_input_bank_name.editText!!.text.toString().trim()
        val acHoldName = text_input_ac_holder_name.editText!!.text.toString().trim()
        val acNo = text_input_account_no.editText!!.text.toString().trim()
        val confirmAcNo = text_input_confirm_account_no.editText!!.text.toString().trim()
        val ifsc = text_input_ifsc.editText!!.text.toString().trim()

        text_input_bank_name.error = null
        text_input_ac_holder_name.error = null
        text_input_account_no.error = null
        text_input_confirm_account_no.error = null
        text_input_ifsc.error = null

        var cancel = false
        var focusable: View? = null

        if(bankName.isEmpty()){
            text_input_bank_name.error = "Bank name cannot be blank"
            focusable = text_input_bank_name
            cancel = true
        }
        if(acHoldName.isEmpty()){
            text_input_ac_holder_name.error = "Account holder name cannot be blank"
            focusable = text_input_ac_holder_name
            cancel = true
        }
        if(acNo.isEmpty()){
            text_input_account_no.error = "Account no cannot be blank"
            focusable = text_input_account_no
            cancel = true
        }
        if(confirmAcNo.isEmpty()){
            text_input_confirm_account_no.error = "Confirm account no cannot be blank"
            focusable = text_input_confirm_account_no
            cancel = true
        }
        if(acNo != confirmAcNo){
            text_input_confirm_account_no.error = "Account no is not match"
            focusable = text_input_confirm_account_no
            cancel = true
        }
        if(ifsc.isEmpty()){
            text_input_ifsc.error = "IFSC code cannot be blank"
            focusable = text_input_ifsc
            cancel = true
        }
        if (UtilMethods.validateByRegex(ifsc, UtilMethods.IFSC).isNotEmpty()){
            text_input_ifsc.error = "IFSC code is not valid"
            focusable = text_input_ifsc
            cancel = true
        }

        if (cancel){
            focusable!!.requestFocus()
        }else {
            // TODO: To update bank details
            // this block is only called if form is valid.
            // do something with a valid form state.
            val context = this@ProfileActivity
            updateBankDetailsApi(context, bankName, ifsc, acNo, acHoldName, login.id, login.sid)
        }

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updateProfileApi(context: Context, fname: String, lname: String
                                 , mobileNo: String, passkey: String, userid: String
                                 , sessionId: String, dob: String, img: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context,true)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.updateProfile(
                        fname,
                        lname,
                        mobileNo,
                        passkey,
                        userid,
                        sessionId,
                        dob,
                        img
                    )
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message
                            )
                        }
                        UtilMethods.hideLoading()
                    }
                } catch (e: Throwable) {
                    runOnUiThread { UtilMethods.ToastLong(context, "Server or Internet error : ${e.message}") }
                    Log.e("TAG", "Throwable : $e")
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun updateBankDetailsApi(context: Context, bankName: String, ifsc: String
                                     , acNo: String, acHoldName: String, userid: String
                                     , sessionId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.updateBankDetails(
                        bankName,
                        ifsc,
                        acNo,
                        acHoldName,
                        userid,
                        sessionId
                    )
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
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
    private fun getBankDetailsApi(context: Context, userid: String, sessionId: String){
        val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.getBankDetails(userid,sessionId)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()?.status == 1){
                                 displayBankDetails(response.body()?.result?.first())
                            }else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } else {
                            UtilMethods.ToastLong(context,"${response.body()?.msg}")
                        }
                    } catch (e: Exception) {
                        UtilMethods.ToastLong(context,"Exception ${e.message}")
                    } catch (e: Throwable) {
                        UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message)
                    }
                }
            }catch (e: Throwable) {
                runOnUiThread {
                    UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}")
                }
                Log.e("TAG","Throwable : $e")
                UtilMethods.hideLoading()
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
        if (requestCode == GALLERY){
            if (data != null){
                val contentURI = data!!.data
                try{
                    var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    if (bitmap != null) {
                        bitmap = UtilMethods.resizeBitmap(bitmap, 200)
                        //val path = saveImage(bitmap)
                        //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                        imgProfile!!.setImageBitmap(bitmap)
                        IMG = UtilMethods.convert(bitmap).toString()
                    }
                }catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ProfileActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK){
            if (data != null) {
                try {
                    var bitmap = data!!.extras!!.get("data") as Bitmap
                    if (bitmap != null) {
                        bitmap = UtilMethods.resizeBitmap(bitmap, 200)
                        imgProfile!!.setImageBitmap(bitmap)
                        //saveImage(bitmap)
                        IMG = UtilMethods.convert(bitmap).toString()
                    }
                    //Toast.makeText(this@ProfileActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileActivity, "Something went wrong", Toast.LENGTH_SHORT)
                        .show()
                }
            }
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
        //Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
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
