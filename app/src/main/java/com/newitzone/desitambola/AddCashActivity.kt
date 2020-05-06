package com.newitzone.desitambola

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.internal.LinkedTreeMap
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.Constants
import com.newitzone.desitambola.utils.KCustomToast
import com.newitzone.desitambola.utils.SharedPrefManager
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.android.synthetic.main.activity_add_cash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import model.paytm.Paytm
import model.paytm.ResPaytmTrans
import retrofit.TambolaApiService


class AddCashActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    @BindView(R.id.text_rs_100) lateinit var txtRS100: TextView
    @BindView(R.id.text_rs_200) lateinit var txtRS200: TextView
    @BindView(R.id.text_rs_500) lateinit var txtRS500: TextView
    @BindView(R.id.text_view_offers) lateinit var txtViewOffers: TextView
    @BindView(R.id.text_avl_bal) lateinit var txtAvlBalance: TextView
    @BindView(R.id.text_btn_add_cash) lateinit var txtAddCash: TextView
    @BindView(R.id.text_input_amount) lateinit var inputAmount: TextInputLayout
    @BindView(R.id.text_input_promo_code) lateinit var inputPromoCode: TextInputLayout

    @BindView(R.id.text_input_withdrawal_amount) lateinit var inputWithdrawalAmount: TextInputLayout
    @BindView(R.id.text_input_bank_name) lateinit var tInputBankName: TextInputLayout
    @BindView(R.id.text_input_ac_holder_name) lateinit var tInputAcHoldName: TextInputLayout
    @BindView(R.id.text_input_account_no) lateinit var tInputAcNo: TextInputLayout
    @BindView(R.id.text_input_confirm_account_no) lateinit var tInputConfirmAcNo: TextInputLayout
    @BindView(R.id.text_input_ifsc) lateinit var tInputIfsc: TextInputLayout
    @BindView(R.id.text_withdrawal_cash) lateinit var txtWithdrawalCash: TextView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_add_cash)
        supportActionBar?.hide()
        this.context = this@AddCashActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        // display balance
        displayBalance(login)

        txtRS100.setOnClickListener {
            onSetAmount(txtRS100.text.toString())
        }
        txtRS200.setOnClickListener {
            onSetAmount(txtRS200.text.toString())
        }
        txtRS500.setOnClickListener {
            onSetAmount(txtRS500.text.toString())
        }
        txtViewOffers.setOnClickListener {
            onSetPromoCode("DESI100")
        }
        txtAddCash.setOnClickListener {
            onAddCash()
        }
        txtWithdrawalCash.setOnClickListener {
            onWithdrawalCash()
        }
    }
    override fun onResume() {
        super.onResume()
        val context = context as AddCashActivity
        if (login != null) {
            // display details of profile
            context?.let { getBankDetailsApi(it, login.id, login.sid) }
        }
        if (SharedPrefManager.getInstance(context).isLoggedIn) {
            loginApi(
                context
                , SharedPrefManager.getInstance(context).result.emailId
                , SharedPrefManager.getInstance(context).passKey.toString()
                , SharedPrefManager.getInstance(context).result.userType
                , "0"
                , SharedPrefManager.getInstance(context).result.sid
                , SharedPrefManager.getInstance(context).result.id
            )
        }
    }
    private fun displayBalance(login: Result){
        if (login != null){
            txtAvlBalance.text = "Avl balance : ₹"+UtilMethods.roundOffDecimal(login.acBal).toString()
        }
    }
    private fun displayBankDetails(result: List<model.bankdetails.Result>?) {
        if (result != null) {
            val getRow: Any = result.first()
            val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>
            tInputBankName.editText?.setText(t["bank_name"] as CharSequence)
            tInputAcHoldName.editText?.setText(t["account_holder"] as CharSequence)
            tInputAcNo.editText?.setText(t["account_no"] as CharSequence)
            tInputConfirmAcNo.editText?.setText(t["account_no"] as CharSequence)
            tInputIfsc.editText?.setText(t["ifcs_code"] as CharSequence)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onAddCash(){
        val amt = inputAmount.editText?.text.toString()
        val promoCode = inputPromoCode.editText?.text.toString()

        var cancel = false
        var focusable: View? = null
        if(amt.isEmpty()){
            inputAmount.error = "Amount cannot be blank"
            focusable = inputAmount
            cancel = true
        }
        if(amt.isNotEmpty()){
            if (amt.toDouble() < 1) {
                inputAmount.error = "Please enter valid amount"
                focusable = inputAmount
                cancel = true
            }
        }
        if (cancel){
            focusable!!.requestFocus()
        }else {
            // TODO: To add cash
            val context = this@AddCashActivity
            if (login != null){

                val amt = UtilMethods.roundOffDecimal(inputAmount.editText?.text.toString().toDouble()).toString()
                // TODO: Call to generate checksum
                getCheckSumApi(context, amt)
            }else{
                UtilMethods.ToastLong(context, "Something went wrong")
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onWithdrawalCash(){
        val context = this@AddCashActivity
        val withdrawalAmt = 500
        if (login != null){
            if (login.acBal >= withdrawalAmt){

                val amt = text_input_withdrawal_amount.editText!!.text.toString().trim()
                val bankName = text_input_bank_name.editText!!.text.toString().trim()
                val acHoldName = text_input_ac_holder_name.editText!!.text.toString().trim()
                val acNo = text_input_account_no.editText!!.text.toString().trim()
                val confirmAcNo = text_input_confirm_account_no.editText!!.text.toString().trim()
                val ifsc = text_input_ifsc.editText!!.text.toString().trim()

                text_input_withdrawal_amount.error = null
                text_input_bank_name.error = null
                text_input_ac_holder_name.error = null
                text_input_account_no.error = null
                text_input_confirm_account_no.error = null

                var cancel = false
                var focusable: View? = null
                if(amt.isEmpty()){
                    text_input_withdrawal_amount.error = "Withdrawal amount cannot be blank"
                    focusable = text_input_withdrawal_amount
                    cancel = true
                }
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
                    val context = this@AddCashActivity
                    withdrawalCashApi(context, amt, bankName, ifsc, acNo, acHoldName, login.id, login.sid)
                }
            }else{
                MessageDialog(context, "Alert", "You can withdrawal minimum ₹$withdrawalAmt").show()
            }
        }else{
            UtilMethods.ToastLong(context, "Something went wrong")
        }
    }
    private fun onSetAmount(amt: String){
        inputAmount.editText?.setText(amt)
    }
    private fun onSetPromoCode(promoCode: String){
        inputPromoCode.editText?.setText(promoCode)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCheckSumApi(context: Context, txnAmount: String){

        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            try {
                CoroutineScope(Dispatchers.IO).launch {
//                    val response = service.getChecksum(paytm.getmId()
//                        , paytm.orderId
//                        , login.id
//                        , paytm.channelId
//                        , paytm.txnAmount
//                        , paytm.website
//                        , paytm.callBackUrl
//                        , paytm.industryTypeId)
                    val response = service.getChecksum( login.id, login.sid, txnAmount)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                Log.i(PaymentActivity.TAG, "Checksum: "+ response.body())
                                if (response.body()?.status == 1) {
                                    // TODO: creating paytm object
                                    val paytm = Paytm(
                                        Constants.M_ID,
                                        Constants.CHANNEL_ID,
                                        txnAmount,
                                        Constants.WEBSITE,
                                        Constants.CALLBACK_URL,
                                        Constants.INDUSTRY_TYPE_ID,
                                        response.body()?.result?.get(0)?.ordno,
                                        login.id,
                                        response.body()?.result?.get(0)?.checkSum
                                    )
                                    // TODO: Call Payment Order
                                    onPaymentOrder(context, paytm)
                                }else{
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context,"Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }
            } catch (e: Throwable) {
                UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    private fun onPaymentOrder(context: Context, paytm: Paytm){
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(KEY_PAYTM, paytm)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, REQ_CODE_ADD_CASH)
    }
    // TODO: add cash Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addCashApi(context: Context, userID: String, sessionId: String, transId: String, remarks: String, amt: String, docNo: String, tranStatus: String, getwayResp: String, type: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.addCash(userID, sessionId, transId, remarks, amt, docNo, tranStatus, getwayResp, type)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                //onLoadAccountDetails(response.body()?.result?.get(0)!!)
                                if (response.body()?.status == 1){
                                    finish()
                                }
                                KCustomToast.toastWithFont(context as Activity,""+response.body()?.msg)
                                //UtilMethods.ToastLong(context,"Msg:${response.body()?.msg}")
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context,"Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }
            } catch (e: Throwable) {
                UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun withdrawalCashApi(context: Context, amt: String, bankName: String, ifsc: String
                                  , acNo: String, acHoldName: String, userid: String
                                  , sessionId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.userWithdrawalRequest(bankName,ifsc,acNo,acHoldName,amt,userid,sessionId)
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
    // TODO: login Api
    private fun loginApi(context: Context, userID: String, passKey: String
                         , userType: String, loginType: String, sesId: String, userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.getlogin(userID, passKey, userType, loginType, sesId, userId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                login.acBal = response.body()?.result?.get(0)!!.acBal
                                login.acChipsBal = response.body()?.result?.get(0)!!.acChipsBal
                                displayBalance(login)
                            } else {
                                UtilMethods.ToastLong(
                                    context,
                                    "Error: ${response.code()}" + "\nMsg:${response.body()?.msg}"
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Oops: Something else went wrong : " + e.message
                            )
                        }
                        //UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_ADD_CASH && data != null) {
            val resPaytmTrans =  data.getSerializableExtra(PAYTM_TRANSACTION) as ResPaytmTrans
            if (resPaytmTrans != null){
                // TODO: Call add cash api
                var  gatewayResponse = resPaytmTrans.toString()
                var type = "1" // type 1 is cash and type 2 is chips
                if (resPaytmTrans.rESPCODE == "01") { // Success
                    context?.let {
                        addCashApi(it, login.id, login.sid, resPaytmTrans.oRDERID,"testing", resPaytmTrans.tXNAMOUNT, "orderNo","1", gatewayResponse,type)
                    }
                }else{
                    context?.let {
                        addCashApi(it, login.id, login.sid, resPaytmTrans.oRDERID,"testing", resPaytmTrans.tXNAMOUNT, "orderNo","2", gatewayResponse,type)
                        UtilMethods.ToastLong(it, "${resPaytmTrans.rESPMSG}")
                    }
                }
            }
        }
    }
    override fun onBackPressed() { // do something here and don't write super.onBackPressed()
        super.onBackPressed()
        finish()
    }
    companion object {
        const val TAG = "AddCashScreen"
        const val KEY_ADD_CASH_AMT = "Key_add_cash_amount"
        const val REQ_CODE_ADD_CASH = 200
        const val PAYTM_TRANSACTION = "paytm_transaction"
        const val KEY_PAYTM = "key_paytm"
    }
}
