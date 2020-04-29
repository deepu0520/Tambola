package com.newitzone.tambola.utils

import android.content.Context
import android.util.Base64
import java.io.IOException

/**
 * Created by rakesh on 22/02/2020.
 */
object Constants {
    const val API_CONTENT_TYPE = "application/json; charset=utf-8"
    const val API_BASE_PATH = "https://api.newitzone.com/tambola/"
    val API_AUTHORIZATION_KEY = "Basic "+ Base64.encodeToString("rakesh:15081990".toByteArray(), Base64.NO_WRAP);

    const val M_ID = "GblQDP68212852342989"//"Nqdtmu49156635409548" //Paytm Merchand Id we got it in paytm credentia// ls
    const val M_KEY = "#sFadf3f9Xz6k79R" //"EZGpToHUdjPz#Sqj" //Paytm Merchand Key we got it in paytm credentia// ls
    const val CHANNEL_ID = "WEB" //Paytm Channel Id, got it in paytm credentials
    const val INDUSTRY_TYPE_ID = "Retail" //Paytm industry type got it in paytm credential
    const val WEBSITE = "WEBSTAGING"
    const val CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback" //"https://securegw.paytm.in/theia/paytmCallback"
//    private val WEBSITE = "APPSTAGING"
//    private val CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=ORDER12345"

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    const val TERMS_CONDITIONS = "<!DOCTYPE html>\n" +
            "    <html>\n" +
            "    <head>\n" +
            "      <meta charset='utf-8'>\n" +
            "      <meta name='viewport' content='width=device-width'>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "    <p>\n" +
            "                  By downloading or using the app, these terms will\n" +
            "                  automatically apply to you – you should make sure therefore\n" +
            "                  that you read them carefully before using the app. You’re not\n" +
            "                  allowed to copy, or modify the app, any part of the app, or\n" +
            "                  our trademarks in any way. You’re not allowed to attempt to\n" +
            "                  extract the source code of the app, and you also shouldn’t try\n" +
            "                  to translate the app into other languages, or make derivative\n" +
            "                  versions. The app itself, and all the trade marks, copyright,\n" +
            "                  database rights and other intellectual property rights related\n" +
            "                  to it, still belong to Raj Kumar Gupta.\n" +
            "                </p> <p>\n" +
            "                  Raj Kumar Gupta is committed to ensuring that the app is\n" +
            "                  as useful and efficient as possible. For that reason, we\n" +
            "                  reserve the right to make changes to the app or to charge for\n" +
            "                  its services, at any time and for any reason. We will never\n" +
            "                  charge you for the app or its services without making it very\n" +
            "                  clear to you exactly what you’re paying for.\n" +
            "                </p> <p>\n" +
            "                  The Desi Tambola app stores and processes personal data that\n" +
            "                  you have provided to us, in order to provide my\n" +
            "                  Service. It’s your responsibility to keep your phone and\n" +
            "                  access to the app secure. We therefore recommend that you do\n" +
            "                  not jailbreak or root your phone, which is the process of\n" +
            "                  removing software restrictions and limitations imposed by the\n" +
            "                  official operating system of your device. It could make your\n" +
            "                  phone vulnerable to malware/viruses/malicious programs,\n" +
            "                  compromise your phone’s security features and it could mean\n" +
            "                  that the Desi Tambola app won’t work properly or at all.\n" +
            "                </p> <div><p>\n" +
            "                    The app does use third party services that declare their own\n" +
            "                    Terms and Conditions.\n" +
            "                  </p> <p>\n" +
            "                    Link to Terms and Conditions of third party service\n" +
            "                    providers used by the app\n" +
            "                  </p> <ul><li><a href=\"https://policies.google.com/terms\" target=\"_blank\">Google Play Services</a></li><!----><!----><!----><li><a href=\"https://www.facebook.com/legal/terms/plain_text_terms\" target=\"_blank\">Facebook</a></li><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----></ul></div> <p>\n" +
            "                  You should be aware that there are certain things that\n" +
            "                  Raj Kumar Gupta will not take responsibility for. Certain\n" +
            "                  functions of the app will require the app to have an active\n" +
            "                  internet connection. The connection can be Wi-Fi, or provided\n" +
            "                  by your mobile network provider, but Raj Kumar Gupta\n" +
            "                  cannot take responsibility for the app not working at full\n" +
            "                  functionality if you don’t have access to Wi-Fi, and you don’t\n" +
            "                  have any of your data allowance left.\n" +
            "                </p> <p></p> <p>\n" +
            "                  If you’re using the app outside of an area with Wi-Fi, you\n" +
            "                  should remember that your terms of the agreement with your\n" +
            "                  mobile network provider will still apply. As a result, you may\n" +
            "                  be charged by your mobile provider for the cost of data for\n" +
            "                  the duration of the connection while accessing the app, or\n" +
            "                  other third party charges. In using the app, you’re accepting\n" +
            "                  responsibility for any such charges, including roaming data\n" +
            "                  charges if you use the app outside of your home territory\n" +
            "                  (i.e. region or country) without turning off data roaming. If\n" +
            "                  you are not the bill payer for the device on which you’re\n" +
            "                  using the app, please be aware that we assume that you have\n" +
            "                  received permission from the bill payer for using the app.\n" +
            "                </p> <p>\n" +
            "                  Along the same lines, Raj Kumar Gupta cannot always take\n" +
            "                  responsibility for the way you use the app i.e. You need to\n" +
            "                  make sure that your device stays charged – if it runs out of\n" +
            "                  battery and you can’t turn it on to avail the Service,\n" +
            "                  Raj Kumar Gupta cannot accept responsibility.\n" +
            "                </p> <p>\n" +
            "                  With respect to Raj Kumar Gupta’s responsibility for your\n" +
            "                  use of the app, when you’re using the app, it’s important to\n" +
            "                  bear in mind that although we endeavour to ensure that it is\n" +
            "                  updated and correct at all times, we do rely on third parties\n" +
            "                  to provide information to us so that we can make it available\n" +
            "                  to you. Raj Kumar Gupta accepts no liability for any\n" +
            "                  loss, direct or indirect, you experience as a result of\n" +
            "                  relying wholly on this functionality of the app.\n" +
            "                </p> <p>\n" +
            "                  At some point, we may wish to update the app. The app is\n" +
            "                  currently available on Android – the requirements for\n" +
            "                  system(and for any additional systems we\n" +
            "                  decide to extend the availability of the app to) may change,\n" +
            "                  and you’ll need to download the updates if you want to keep\n" +
            "                  using the app. Raj Kumar Gupta does not promise that it\n" +
            "                  will always update the app so that it is relevant to you\n" +
            "                  and/or works with the Android version that you have\n" +
            "                  installed on your device. However, you promise to always\n" +
            "                  accept updates to the application when offered to you, We may\n" +
            "                  also wish to stop providing the app, and may terminate use of\n" +
            "                  it at any time without giving notice of termination to you.\n" +
            "                  Unless we tell you otherwise, upon any termination, (a) the\n" +
            "                  rights and licenses granted to you in these terms will end;\n" +
            "                  (b) you must stop using the app, and (if needed) delete it\n" +
            "                  from your device.\n" +
            "                </p> <p><strong>Changes to This Terms and Conditions</strong></p> <p>\n" +
            "                  I may update our Terms and Conditions\n" +
            "                  from time to time. Thus, you are advised to review this page\n" +
            "                  periodically for any changes. I will\n" +
            "                  notify you of any changes by posting the new Terms and\n" +
            "                  Conditions on this page.\n" +
            "                </p> <p>\n" +
            "                  These terms and conditions are effective as of 2020-04-28\n" +
            "                </p> <p><strong>Contact Us</strong></p> <p>\n" +
            "                  If you have any questions or suggestions about my\n" +
            "                  Terms and Conditions, do not hesitate to contact me\n" +
            "                  at rajcon326@gmail.com.\n" +
            "                </p> " +
            "    </body>\n" +
            "    </html>\n" +
            "      "
}