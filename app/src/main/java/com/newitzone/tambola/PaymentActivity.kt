package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.tambola.utils.Constants
import com.newitzone.tambola.utils.UtilMethods
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import model.paytm.Paytm
import model.paytm.ResPaytmTrans
import retrofit.TambolaApiService


class PaymentActivity : AppCompatActivity() , PaytmPaymentTransactionCallback {
    private var context: Context? = null
    private lateinit var login: Result
    private lateinit var amt: String

    @BindView(R.id.start_transaction) lateinit var btnStartTranscation: Button
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ButterKnife.bind(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.context = this@PaymentActivity
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        amt = intent.getStringExtra(AddCashActivity.KEY_ADD_CASH_AMT)
        if(login != null && amt != null){
            generateCheckSum(context as PaymentActivity, UtilMethods.roundOffDecimal(amt.toDouble()).toString())
//            btnStartTranscation.setOnClickListener {
//                generateCheckSum(context as PaymentActivity, amt)
//            }
        }
    }
    override fun onStart() {
        super.onStart()
        //initOrderId();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun generateCheckSum(context: Context, txnAmount: String){
        //creating paytm object
        //containing all the values required
        val paytm = Paytm(
            Constants.M_ID,
            Constants.CHANNEL_ID,
            txnAmount,
            Constants.WEBSITE,
            Constants.CALLBACK_URL,
            Constants.INDUSTRY_TYPE_ID
        )

        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getChecksum( paytm.getmId(),
                        paytm.getOrderId(),
                        paytm.getCustId(),
                        paytm.getChannelId(),
                        paytm.getTxnAmount(),
                        paytm.getWebsite(),
                        paytm.getCallBackUrl(),
                        paytm.getIndustryTypeId())
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                Log.i(TAG , "Checksum: "+response.body()?.checksumHash)
                                UtilMethods.ToastLong(context,"Checksum: "+response.body()?.checksumHash)
                                // TODO: Initialize Paytm Payment
                                response.body()?.checksumHash?.let { onStartTransaction(context, it, paytm) };
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
    private fun onStartTransaction(context: Context, checksumHash: String, paytm: Paytm) {
        val pgService = PaytmPGService.getProductionService()
        val paramMap: HashMap<String, String> = HashMap()

        // these are mandatory parameters
        paramMap["CALLBACK_URL"] = paytm.callBackUrl
        paramMap["CHANNEL_ID"] = paytm.channelId
        paramMap["CHECKSUMHASH"] = checksumHash
        paramMap["CUST_ID"] = paytm.custId
        paramMap["INDUSTRY_TYPE_ID"] = paytm.industryTypeId
        paramMap["MID"] = paytm.getmId()
        paramMap["ORDER_ID"] = paytm.orderId
        paramMap["TXN_AMOUNT"] = paytm.txnAmount
        paramMap["WEBSITE"] = paytm.website

        /*
        paramMap.put("MID" , "WorldP64425807474247");
        paramMap.put("ORDER_ID" , "210lkldfka2a27");
        paramMap.put("CUST_ID" , "mkjNYC1227");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1");
        paramMap.put("WEBSITE" , "worldpressplg");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/
        val paytmOrder = PaytmOrder(paramMap)

        /*PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/
        pgService.initialize(paytmOrder, null)
        pgService.startPaymentTransaction(this, true, true, this)
    }


    override fun onTransactionResponse(inResponse: Bundle?) {
        // TODO:("Not yet implemented")
        context?.let { UtilMethods.ToastLong(it, "inResponse : ${inResponse.toString()}") }
        Log.d("LOG", "Payment Transaction is successful $inResponse")
        val resPaytmTrans = ResPaytmTrans(inResponse?.getString("BANKTXNID")!!
                                        , inResponse.getString("CHECKSUMHASH")!!
                                        , inResponse.getString("CURRENCY")!!
                                        , inResponse.getString("MID")!!
                                        , inResponse.getString("ORDERID")!!
                                        , inResponse.getInt("RESPCODE")
                                        , inResponse.getString("RESPMSG")!!
                                        , inResponse.getString("STATUS")!!
                                        , inResponse.getDouble("TXNAMOUNT"))

        context?.let { UtilMethods.ToastLong(it, "Payment Transaction response $resPaytmTrans") }
        val intent = Intent(context, AddCashActivity::class.java)
        intent.putExtra(AddCashActivity.PAYTM_TRANSACTION, resPaytmTrans)
        setResult(AddCashActivity.REQ_CODE_ADD_CASH, intent)
        finish()
    }

    override fun clientAuthenticationFailed(inErrorMessage: String?) {
        // TODO:("Not yet implemented")
        context?.let { UtilMethods.ToastLong(it, "inErrorMessage : ${inErrorMessage.toString()}") }
    }

    override fun someUIErrorOccurred(inErrorMessage: String?) {
        // TODO:("Not yet implemented")
        context?.let { UtilMethods.ToastLong(it, "inErrorMessage : ${inErrorMessage.toString()}") }
    }

    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
        // TODO:("Not yet implemented")
        context?.let { UtilMethods.ToastLong(it, "inErrorMessage : ${inErrorMessage.toString()}") }
        context?.let { UtilMethods.ToastLong(it, "inResponse : ${inResponse.toString()}") }
    }

    override fun networkNotAvailable() {
        // TODO:("Not yet implemented")
    }

    override fun onErrorLoadingWebPage(
        iniErrorCode: Int,
        inErrorMessage: String?,
        inFailingUrl: String?
    ) {
        // TODO:("Not yet implemented")
        context?.let { UtilMethods.ToastLong(it, "inErrorMessage : ${inErrorMessage.toString()}") }
    }

    override fun onBackPressedCancelTransaction() {
        // TODO:("Not yet implemented")
    }

    companion object{
        const val TAG = "PaymentScreen"
    }
}
