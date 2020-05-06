package com.newitzone.desitambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.desitambola.utils.UtilMethods
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import model.paytm.Paytm
import model.paytm.ResPaytmTrans


class PaymentActivity : AppCompatActivity() , PaytmPaymentTransactionCallback {
    private var context: Context? = null
    private lateinit var paytm: Paytm
    private lateinit var amt: String

    @BindView(R.id.start_transaction) lateinit var btnStartTranscation: Button
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ButterKnife.bind(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.context = this@PaymentActivity
        paytm = intent.getSerializableExtra(AddCashActivity.KEY_PAYTM) as Paytm
        if(paytm != null){
            //generateCheckSum(context as PaymentActivity, UtilMethods.roundOffDecimal(amt.toDouble()).toString())
            onStartTransaction(paytm)
        }
    }
    override fun onStart() {
        super.onStart()
        //initOrderId();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onStartTransaction(paytm: Paytm) { //(context: Context, checkSumResult: String, paytm: Paytm) {
        val pgService = PaytmPGService.getStagingService()
        //val pgService = PaytmPGService.getProductionService()
        val paramMap: HashMap<String, String> = HashMap()

        // these are mandatory parameters
        paramMap["MID"] = paytm.mId
        paramMap["CHANNEL_ID"] = paytm.channelId
        paramMap["INDUSTRY_TYPE_ID"] = paytm.industryTypeId
        paramMap["TXN_AMOUNT"] = paytm.txnAmount
        paramMap["WEBSITE"] = paytm.website
        paramMap["CALLBACK_URL"] = paytm.callBackUrl
        paramMap["ORDER_ID"] = paytm.orderId
        paramMap["CUST_ID"] = paytm.custId
        paramMap["CHECKSUMHASH"] = paytm.checkSumHash
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
        //context?.let { UtilMethods.ToastLong(it, "inResponse : ${inResponse.toString()}") }
        Log.d("LOG", "Payment Transaction is successful $inResponse")
        val resPaytmTrans = ResPaytmTrans(inResponse?.getString("BANKTXNID")!!
                                        , inResponse.getString("CHECKSUMHASH")!!
                                        , inResponse.getString("CURRENCY")!!
                                        , inResponse.getString("MID")!!
                                        , inResponse.getString("ORDERID")!!
                                        , inResponse.getString("RESPCODE")!!
                                        , inResponse.getString("RESPMSG")!!
                                        , inResponse.getString("STATUS")!!
                                        , inResponse.getString("TXNAMOUNT")!!
                                        , inResponse.getString("BANKNAME")!!
                                        , inResponse.getString("TXNDATE")!!
                                        , inResponse.getString("TXNID")!!
                                        , inResponse.getString("PAYMENTMODE")!!
                                        , inResponse.getString("GATEWAYNAME")!!)

        //context?.let { UtilMethods.ToastLong(it, "Payment Transaction response $resPaytmTrans") }
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
