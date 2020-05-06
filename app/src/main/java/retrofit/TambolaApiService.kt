package retrofit

import com.google.gson.GsonBuilder
import com.newitzone.desitambola.utils.Constants
import model.DefaultResponse
import model.bankdetails.BankDetails
import model.claim.ClaimPrize
import model.gamein.GameIn
import model.gamerequest.ResGameRequest
import model.gamerequeststatus.ResGameReqStatus
import model.login.ResLogin
import model.money.transaction.MoneyTrans
import model.paytm.ResPaytmOrderChecksum
import model.tournament.ResTournament
import model.tournament.start.ResTournamentStart
import model.transaction.CashOrChipsTrans
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface TambolaApiService {

    /**
     * Companion object to create the Tambola Api Service
     */
    @FormUrlEncoded
    @POST("paytm-add-money-checksum")
    suspend fun getChecksum(@Field("userid") userid: String,
                            @Field("sesid") sesid: String,
                            @Field("amt") amt: String
    ): Response<ResPaytmOrderChecksum>
//    @POST("http://192.168.0.100:82/paytm/generateChecksum.php")
//    suspend fun getChecksum(@Field("MID") mId: String,
//                            @Field("ORDER_ID") orderId: String,
//                            @Field("CUST_ID") custId: String,
//                            @Field("CHANNEL_ID") channelId: String,
//                            @Field("TXN_AMOUNT") txnAmount: String,
//                            @Field("WEBSITE") website: String,
//                            @Field("CALLBACK_URL") callbackUrl: String,
//                            @Field("INDUSTRY_TYPE_ID") industryTypeId: String
//    ): Response<Checksum>

    @FormUrlEncoded
    @POST("game-prize-claim-and-claim-status")
    suspend fun gamePrizeClaimOrStatus(@Field("userid") userid: String,
                                       @Field("sesid") sesid: String,
                                       @Field("game_type") game_type: String,
                                       @Field("amt") amt: String,
                                       @Field("tournament_id") tournament_id: String,
                                       @Field("reqid") reqid: String,
                                       @Field("game_id") gameId: String,
                                       @Field("claimType") claimType: String,
                                       @Field("type") type: Int
    ): Response<ClaimPrize>

    @FormUrlEncoded
    @POST("tournament-start")
    suspend fun tournamentStart(@Field("userid") userid: String,
                       @Field("sesid") sesid: String,
                       @Field("tournament_id") tournament_id: String,
                       @Field("game_id") game_id: String
    ): Response<ResTournamentStart>

    @FormUrlEncoded
    @POST("game-in")
    suspend fun gameIn(@Field("userid") userid: String,
                       @Field("sesid") sesid: String,
                       @Field("game_type") game_type: String,
                       @Field("amt") amt: String,
                       @Field("tournament_id") tournament_id: String,
                       @Field("reqid") reqid: String
    ): Response<GameIn>

    @FormUrlEncoded
    @POST("game-request-status")
    suspend fun gameRequestStatus(@Field("userid") userid: String,
                                  @Field("sesid") sesid: String,
                                  @Field("game_type") game_type: String,
                                  @Field("amt") amt: String,
                                  @Field("tournament_id") tournament_id: String,
                                  @Field("reqid") reqid: String
    ): Response<ResGameReqStatus>

    @FormUrlEncoded
    @POST("cancle-tournament-request")
    suspend fun gameRequestTournamentCancel(@Field("userid") userid: String,
                                            @Field("sesid") sesid: String,
                                            @Field("tournament_id") tournament_id: String,
                                            @Field("game_id") game_id: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("tournament-game-request")
    suspend fun gameRequestTournament(@Field("userid") userid: String,
                            @Field("sesid") sesid: String,
                            @Field("amt") amt: String,
                            @Field("req_ticket") req_ticket: String,
                            @Field("game_type") game_type: String,
                            @Field("tournament_id") tournament_id: String
    ): Response<ResGameRequest>

    @FormUrlEncoded
    @POST("game-request")
    suspend fun gameRequest(@Field("userid") userid: String,
                            @Field("sesid") sesid: String,
                            @Field("amt") amt: String,
                            @Field("req_ticket") req_ticket: String,
                            @Field("game_type") game_type: String,
                            @Field("tournament_id") tournament_id: String
    ): Response<ResGameRequest>

    @FormUrlEncoded
    @POST("add-money")
    suspend fun addCash(@Field("userid") userid:String,
                         @Field("sesid") sesid: String,
                         @Field("transid") transid: String,
                         @Field("remarks") remarks: String,
                         @Field("amt") amt: String,
                        @Field("docno") docNo: String,
                        @Field("tran_status") tranStatus: String,
                        @Field("getway_resp") getwayResp: String,
                        @Field("type") type: String
    ): Response<DefaultResponse>
    @FormUrlEncoded
    @POST("tournament-list")
    suspend fun getTournament(@Field("userid") userid:String,
                        @Field("sesid") sesid: String
    ): Response<ResTournament>

    @FormUrlEncoded
    @POST("user-trans-list") // use type 2 is money transaction
    suspend fun userMoneyTransList(@Field("userid") userid: String,
                              @Field("sesid") sesid: String,
                              @Field("fromdt") fromdt: String,
                              @Field("todt") todt: String,
                              @Field("type") type: Int
    ): Response<MoneyTrans>

    @FormUrlEncoded
    @POST("user-trans-list") // use type 0 is cash and 1 is chips
    suspend fun userCashOrChipsTransList(@Field("userid") userid: String,
                              @Field("sesid") sesid: String,
                              @Field("fromdt") fromdt: String,
                              @Field("todt") todt: String,
                              @Field("type") type: Int
    ): Response<CashOrChipsTrans>

    @FormUrlEncoded
    @POST("user-withdrawal-request")
    suspend fun userWithdrawalRequest(@Field("banknm") fname: String,
                                      @Field("ifsc") lname: String,
                                      @Field("acno") mobileNo: String,
                                      @Field("ac_holdernm") passkey: String,
                                      @Field("amt") amt: String,
                                      @Field("userid") userid: String,
                                      @Field("sesid") sesid: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("user-bank-detail")
    suspend fun getBankDetails(@Field("userid") userid: String,
                               @Field("sesid") sesid: String
    ): Response<BankDetails>

    @FormUrlEncoded
    @POST("update-bank-detail")
    suspend fun updateBankDetails(@Field("banknm") fname: String,
                              @Field("ifsc") lname: String,
                              @Field("acno") mobileNo: String,
                              @Field("ac_holdernm") passkey: String,
                              @Field("userid") userid: String,
                              @Field("sesid") sesid: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("profile-update")
    suspend fun updateProfile(@Field("fname") fname: String,
                              @Field("lname") lname: String,
                              @Field("mobileNo") mobileNo: String,
                              @Field("passkey") passkey: String,
                              @Field("userid") userid: String,
                              @Field("sesid") sesid: String,
                              @Field("dob") dob: String,
                              @Field("img") img: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun getlogin(@Field("userName") userName:String,
                         @Field("passkey") passkey: String,
                         @Field("userType") userType: String,
                         @Field("loginType") loginType: String,
                         @Field("sesID") sesID: String,
                         @Field("userID") userID: String
    ): Response<ResLogin>

    @FormUrlEncoded
    @POST("registration")
    suspend fun registration(
        @Field("fname") fname:String,
        @Field("lname") lname:String,
        @Field("emailID") emailID:String,
        @Field("mobileNo") mobileNo:String,
        @Field("passkey") passkey:String,
        @Field("dob") dob:String,
        @Field("userType") userType:String,
        @Field("img") img:String
    ): Response<DefaultResponse>

    object RetrofitFactory {

        private fun okHttpBuilder(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        }

        fun makeRetrofitService(): TambolaApiService {
            return Retrofit.Builder()
                .baseUrl(Constants.API_BASE_PATH)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okHttpBuilder())
                .build().create(TambolaApiService::class.java)
        }
    }
}