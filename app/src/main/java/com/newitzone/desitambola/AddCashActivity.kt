package com.newitzone.desitambola

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.newitzone.desitambola.databinding.ActivityAddCashBinding
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.DesiTambolaPreferences
import com.newitzone.desitambola.utils.KCustomToast
import com.newitzone.desitambola.utils.UtilMethods
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
    private lateinit var paytm: Paytm
    private lateinit var orderId: String
    private lateinit var binding: ActivityAddCashBinding

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
        binding = ActivityAddCashBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_add_cash)
        setContentView(binding.root)
        supportActionBar?.hide()
        this.context = this@AddCashActivity
        //  ButterKnife.bind(this)
        // Hide the status bar.


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        // display balance
        displayBalance(login)

        binding.textRs100.setOnClickListener {
            onSetAmount(binding.textRs100.text.toString())
        }
        binding.textRs200.setOnClickListener {
            onSetAmount(binding.textRs200.text.toString())
        }
        binding.textRs500.setOnClickListener {
            onSetAmount(binding.textRs500.text.toString())
        }
        binding.textViewOffers.setOnClickListener {
            onSetPromoCode("DESI100")
        }
        binding.textBtnAddCash.setOnClickListener {
            onAddCash()
        }
        binding.textWithdrawalCash.setOnClickListener {
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
        val mLogin = DesiTambolaPreferences.getLogin(context)
        val passKey = DesiTambolaPreferences.getPassKey(context)
        if (mLogin.isLogin) {
            loginApi(
                context,
                mLogin.emailId,
                passKey,
                mLogin.userType,
                LoginActivity.LOGIN_TYPE_INFO,
                mLogin.sid,
                mLogin.id
            )
        }
    }

    private fun displayBalance(login: Result) {
        if (login != null) {
            binding.textAvlBal.text =
                "Avl balance : ₹" + UtilMethods.roundOffDecimal(login.acBal.toDouble()).toString()
        }
    }

    private fun displayBankDetails(result: List<model.bankdetails.Result>?) {
        if (result != null) {
//            val getRow: Any = result.first()
//            val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>
            binding.textInputBankName.editText?.setText(result.first().bankName)//t["bank_name"] as CharSequence)
            binding.textInputAcHolderName.editText?.setText(result.first().accountHolder)//t["account_holder"] as CharSequence)
            binding.textInputAccountNo.editText?.setText(result.first().accountNo)//t["account_no"] as CharSequence)
            binding.textInputConfirmAccountNo.editText?.setText(result.first().accountNo)//t["account_no"] as CharSequence)
            binding.textInputIfsc.editText?.setText(result.first().ifcsCode)//t["ifcs_code"] as CharSequence)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onAddCash() {
        val amt = binding.textInputAmount.editText?.text.toString()
        val promoCode = binding.textInputPromoCode.editText?.text.toString()

        var cancel = false
        var focusable: View? = null
        if (amt.isEmpty()) {
            binding.textInputAmount.error = "Amount cannot be blank"
            focusable = binding.textInputAmount
            cancel = true
        }
        if (amt.isNotEmpty()) {
            if (amt.toDouble() < 25) {
                binding.textInputAmount.error = "Please add cash minimum ₹25"
                focusable = binding.textInputAmount
                cancel = true
            }
        }
        if (cancel) {
            focusable!!.requestFocus()
        } else {
            // TODO: To add cash
            val context = this@AddCashActivity
            if (login != null) {

                val amt =
                    UtilMethods.roundOffDecimal(
                        binding.textInputAmount.editText?.text.toString().toDouble()
                    )
                        .toString()
                // TODO: Call to generate checksum
                getCheckSumApi(context, amt)
            } else {
                UtilMethods.ToastLong(context, "Something went wrong")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onWithdrawalCash() {
        val context = this@AddCashActivity
        val withdrawalAmt = 500
        if (login != null) {
            if (login.acBal.toDouble() >= withdrawalAmt) {

                val amt = binding.textInputWithdrawalAmount.editText!!.text.toString().trim()
                val bankName = binding.textInputBankName.editText!!.text.toString().trim()
                val acHoldName = binding.textInputAcHolderName.editText!!.text.toString().trim()
                val acNo = binding.textInputAccountNo.editText!!.text.toString().trim()
                val confirmAcNo =
                    binding.textInputConfirmAccountNo.editText!!.text.toString().trim()
                val ifsc = binding.textInputIfsc.editText!!.text.toString().trim()

                binding.textInputWithdrawalAmount.error = null
                binding.textInputBankName.error = null
                binding.textInputAcHolderName.error = null
                binding.textInputAccountNo.error = null
                binding.textInputConfirmAccountNo.error = null

                var cancel = false
                var focusable: View? = null
                if (amt.isEmpty()) {
                    binding.textInputWithdrawalAmount.error = "Withdrawal amount cannot be blank"
                    focusable = binding.textInputWithdrawalAmount
                    cancel = true
                }
                if (amt.isNotEmpty()) {
                    if (amt.toDouble() < withdrawalAmt) {
                        binding.textInputWithdrawalAmount.error = "You can withdraw minimum ₹500"
                        focusable = binding.textInputWithdrawalAmount
                        cancel = true
                    }
                }
                if (bankName.isEmpty()) {
                    binding.textInputBankName.error = "Bank name cannot be blank"
                    focusable = binding.textInputBankName
                    cancel = true
                }
                if (acHoldName.isEmpty()) {
                    binding.textInputAcHolderName.error = "Account holder name cannot be blank"
                    focusable = binding.textInputAcHolderName
                    cancel = true
                }
                if (acNo.isEmpty()) {
                    binding.textInputAccountNo.error = "Account no cannot be blank"
                    focusable = binding.textInputAccountNo
                    cancel = true
                }
                if (confirmAcNo.isEmpty()) {
                    binding.textInputConfirmAccountNo.error = "Confirm account no cannot be blank"
                    focusable = binding.textInputConfirmAccountNo
                    cancel = true
                }
                if (acNo != confirmAcNo) {
                    binding.textInputConfirmAccountNo.error = "Account no is not match"
                    focusable = binding.textInputConfirmAccountNo
                    cancel = true
                }
                if (ifsc.isEmpty()) {
                    binding.textInputIfsc.error = "IFSC code cannot be blank"
                    focusable = binding.textInputIfsc
                    cancel = true
                }
                if (UtilMethods.validateByRegex(ifsc, UtilMethods.IFSC).isNotEmpty()) {
                    binding.textInputIfsc.error = "IFSC code is not valid"
                    focusable = binding.textInputIfsc
                    cancel = true
                }

                if (cancel) {
                    focusable!!.requestFocus()
                } else {
                    // TODO: To update bank details
                    // this block is only called if form is valid.
                    // do something with a valid form state.
                    val context = this@AddCashActivity
                    withdrawalCashApi(
                        context,
                        amt,
                        bankName,
                        ifsc,
                        acNo,
                        acHoldName,
                        login.id,
                        login.sid
                    )
                }
            } else {
                MessageDialog(context, "Alert", "You can withdrawal minimum ₹$withdrawalAmt").show()
            }
        } else {
            UtilMethods.ToastLong(context, "Something went wrong")
        }
    }

    private fun onSetAmount(amt: String) {
        binding.textInputAmount.editText?.setText(amt)
    }

    private fun onSetPromoCode(promoCode: String) {
        binding.textInputPromoCode.editText?.setText(promoCode)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCheckSumApi(context: Context, txnAmount: String) {

        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getChecksum(login.id, login.sid, txnAmount)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                Log.i(TAG, "Checksum: " + response.body())
                                if (response.body()?.status == 1) {
                                    // TODO: creating paytm object
                                    orderId = response.body()?.result?.get(0)?.ordid.toString()
                                    paytm = Paytm(
                                        response.body()?.result?.get(0)?.mId,
                                        response.body()?.result?.get(0)?.channelId,
                                        txnAmount,
                                        response.body()?.result?.get(0)?.website,
                                        response.body()?.result?.get(0)?.callBackUrl,
                                        response.body()?.result?.get(0)?.industryTypeId,
                                        response.body()?.result?.get(0)?.ordno,
                                        login.id,
                                        response.body()?.result?.get(0)?.checkSum
                                    )
                                    // TODO: Call Payment Order
                                    onPaymentOrder(context, paytm)
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "Error: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Oops: Something else went wrong : " + e.message
                            )
                        }
                        UtilMethods.hideLoading()
                    }
                }
            } catch (e: Throwable) {
                UtilMethods.ToastLong(context, "Oops: Something else went wrong : " + e.message)
            }
        } else {
            UtilMethods.ToastLong(context, "No Internet Connection")
        }
    }

    private fun onPaymentOrder(context: Context, paytm: Paytm) {
        val intent = Intent(context, PaymentActivity::class.java)
        intent.putExtra(KEY_PAYTM, paytm)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivityForResult(intent, REQ_CODE_ADD_CASH)
    }

    // TODO: add cash Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addCashApi(
        context: Context,
        userID: String,
        sessionId: String,
        transId: String,
        remarks: String,
        amt: String,
        docNo: String,
        tranStatus: String,
        getwayResp: String,
        type: String,
    ) {
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.addCash(
                        userID,
                        sessionId,
                        transId,
                        remarks,
                        amt,
                        docNo,
                        tranStatus,
                        getwayResp,
                        type
                    )
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                //onLoadAccountDetails(response.body()?.result?.get(0)!!)
                                if (response.body()?.status == 1) {
                                    finish()
                                    KCustomToast.toastWithFont(
                                        context as Activity,
                                        "" + response.body()?.msg
                                    )
                                } else {
                                    KCustomToast.toastWithFont(
                                        context as Activity,
                                        "" + response.body()?.msg
                                    )
                                }
                            } else {
                                UtilMethods.ToastLong(
                                    context,
                                    "Error: ${response.code()}" + "\nMsg:${response.body()?.msg}"
                                )
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Oops: Something else went wrong : " + e.message
                            )
                        }
                        UtilMethods.hideLoading()
                    }
                }
            } catch (e: Throwable) {
                UtilMethods.ToastLong(context, "Oops: Something else went wrong : " + e.message)
            }
        } else {
            UtilMethods.ToastLong(context, "No Internet Connection")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun withdrawalCashApi(
        context: Context, amt: String, bankName: String, ifsc: String,
        acNo: String, acHoldName: String, userid: String,
        sessionId: String,
    ) {
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.userWithdrawalRequest(
                        bankName,
                        ifsc,
                        acNo,
                        acHoldName,
                        amt,
                        userid,
                        sessionId
                    )
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

    private fun getBankDetailsApi(context: Context, userid: String, sessionId: String) {
        val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getBankDetails(userid, sessionId)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()?.status == 1) {
                                displayBankDetails(response.body()?.result?.first())
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
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
                }
            } catch (e: Throwable) {
                runOnUiThread {
                    UtilMethods.ToastLong(context, "Server or Internet error : ${e.message}")
                }
                Log.e("TAG", "Throwable : $e")
                UtilMethods.hideLoading()
            }
        }
    }

    // TODO: login Api
    private fun loginApi(
        context: Context, userID: String, passKey: String,
        userType: String, loginType: String, sesId: String, userId: String,
    ) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response =
                        service.getlogin(userID, passKey, userType, loginType, sesId, userId, "")
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
                            //UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Oops: Something else went wrong : " + e.message
                            )
                        }
                        //UtilMethods.hideLoading()
                    }
                } catch (e: Throwable) {
                    Log.e("TAG", "Throwable : $e")
                }
            }
        } else {
            UtilMethods.ToastLong(context, "No Internet Connection")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_ADD_CASH && data != null) {
            val resPaytmTrans = data.getSerializableExtra(PAYTM_TRANSACTION) as ResPaytmTrans
            if (resPaytmTrans != null) {
                // TODO: Call add cash api
                var gatewayResponse = resPaytmTrans.toString()
                var type = "1" // type 1 is cash and type 2 is chips
                if (resPaytmTrans.rESPCODE == "01") { // Success
                    context?.let {
                        addCashApi(
                            it,
                            login.id,
                            login.sid,
                            orderId,
                            "Success",
                            resPaytmTrans.tXNAMOUNT,
                            resPaytmTrans.oRDERID,
                            "1",
                            gatewayResponse,
                            type
                        )
                    }
                } else {
                    context?.let {
                        addCashApi(
                            it,
                            login.id,
                            login.sid,
                            orderId,
                            "failed",
                            resPaytmTrans.tXNAMOUNT,
                            resPaytmTrans.oRDERID,
                            "2",
                            gatewayResponse,
                            type
                        )
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
