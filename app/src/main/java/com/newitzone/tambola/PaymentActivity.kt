package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import com.paytm.pg.merchant.CheckSumServiceHelper
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import model.login.Result
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class PaymentActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private lateinit var amt: String
    private val ActivityRequestCode = 200

    private val MID = "GblQDP68212852342989"
    private val MercahntKey = "#sFadf3f9Xz6k79R"
    private val INDUSTRY_TYPE_ID = "Retail"
    private val CHANNLE_ID = "WAP"
    private val WEBSITE = "APPSTAGING"
    private val CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=ORDER12345"

    @BindView(R.id.webview_paytm) lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        amt = intent.getStringExtra(AddCashActivity.KEY_ADD_CASH_AMT)
        if(login != null && amt != null){
            val orderId = login.id+System.currentTimeMillis()
//            orderGenerate(getString(R.string.merchant_id)
//                        , getString(R.string.merchant_key)
//                        , orderId
//                        , amt
//                        , login.id)
            getCheckSum()
        }
    }
    private fun paytmOrder(orderId: String, mId: String, txnToken: String, amount: String, callbackurl: String){

        val paytmOrder = PaytmOrder(orderId, mId, txnToken, amount, callbackurl)
        val transactionManager = TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback{
            override fun onTransactionResponse(p0: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun clientAuthenticationFailed(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun someUIErrorOccurred(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun networkNotAvailable() {
                TODO("Not yet implemented")
            }

            override fun onErrorProceed(p0: String?) {
                TODO("Not yet implemented")
            }

            override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                TODO("Not yet implemented")
            }

            override fun onBackPressedCancelTransaction() {
                TODO("Not yet implemented")
            }

        }) // code statement);
        transactionManager.startTransaction(this, ActivityRequestCode);
    }
    private fun orderGenerate(merchantId: String, merchantKey: String, orderId: String, amount: String, custId: String){
        /* initialize an object */
        val paytmParams = JSONObject()

        /* body parameters */
        val body = JSONObject()

        /* for custom checkout value is 'Payment' and for intelligent router is 'UNI_PAY' */
        body.put("requestType","Payment")

        /* Find your MID in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
        body.put("mid", merchantId)

        /* Find your Website Name in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys */
        body.put("websiteName", "desitambola.com")

        /* Enter your unique order id */
        body.put("orderId", orderId)

        /* on completion of transaction, we will send you the response on this URL */
        // stage
        body.put("callbackUrl","https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$orderId")
        // production
        //body.put("callbackUrl","https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=$orderId")

        /* initialize an object for txnAmount */
        val txnAmount = JSONObject()

        /* Transaction Amount Value */
        txnAmount.put("value", "$amount.00")

        /* Transaction Amount Currency */
        txnAmount.put("currency", "INR")

        /* initialize an object for userInfo */
        val userInfo = JSONObject()

        /* unique id that belongs to your customer */
        userInfo.put("custId", "cust_$custId")

        /* put txnAmount object in body */
        body.put("txnAmount", txnAmount)

        /* put userInfo object in body */
        body.put("userInfo", userInfo)
        /**
         * Generate checksum by parameters we have in body
         * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */

        /* initialize a TreeMap object */
        var paytmParamsChecksum: TreeMap<String, String> = TreeMap<String, String>()

        /* put checksum parameters in TreeMap */
        paytmParamsChecksum["mid"] = merchantId
        paytmParamsChecksum["orderId"] = orderId

        /**
         * Generate checksum by parameters we have
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */
        val checksum: String = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(merchantKey, paytmParamsChecksum)
        //val checksum: String = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum("YOUR_KEY_HERE", body.toString())

        /* head parameters */
        val head = JSONObject()

        /* put generated checksum value here */
        head.put("signature", checksum)

        /* prepare JSON string for request */
        paytmParams.put("body", body)
        paytmParams.put("head", head)
        val postData = paytmParams.toString()

        /* for Staging */
        val url = URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid=$merchantId&orderId=$orderId")

        /* for Production */
        // URL url = new URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid=YOUR_MID_HERE&orderId=YOUR_ORDER_ID");
        try {
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            val requestWriter = DataOutputStream(connection.outputStream)
            requestWriter.writeBytes(postData)
            requestWriter.close()
            var responseData = ""
            val iStream: InputStream = connection.inputStream
            val responseReader = BufferedReader(InputStreamReader(iStream))
            if (responseReader.readLine().also { responseData = it } != null) {
                System.out.append("Response: $responseData")
            }
            // System.out.append("Request: " + post_data);
            responseReader.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
    fun getCheckSum(): String? {
        var checkSum: String? = null
        val paramMap = TreeMap<String, String?>()
        paramMap["MID"] = MID
        paramMap["ORDER_ID"] = "ORDER12345"
        paramMap["CUST_ID"] = "CUST02513"
        paramMap["INDUSTRY_TYPE_ID"] = INDUSTRY_TYPE_ID
        paramMap["CHANNEL_ID"] = CHANNLE_ID
        paramMap["TXN_AMOUNT"] = "1.00"
        paramMap["WEBSITE"] = WEBSITE
        paramMap["EMAIL"] = "rakesh@gmail.com"
        paramMap["MOBILE_NO"] = "9013448426"
        paramMap["CALLBACK_URL"] = CALLBACK_URL
        try {
            println("ChecksumGeneration.java getCheckSum-method Starts")
            checkSum = CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(MercahntKey, paramMap)
            println("checkSum: $checkSum")
            paramMap["CHECKSUMHASH"] = checkSum
            println("ChecksumGeneration.java getCheckSum-method Ends")
        } catch (e: java.lang.Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return checkSum
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ActivityRequestCode && data != null) {
            Toast.makeText(
                this,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    fun onTransactionResponse(inResponse: Bundle) {
        /*Display the message as below */
        Toast.makeText(
            applicationContext,
            "Payment Transaction response $inResponse",
            Toast.LENGTH_LONG
        ).show()
    }
}
