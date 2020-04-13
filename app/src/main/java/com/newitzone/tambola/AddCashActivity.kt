package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.textfield.TextInputLayout
import com.newitzone.tambola.utils.UtilMethods
import model.login.Result


class AddCashActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    @BindView(R.id.text_rs_100) lateinit var txtRS100: TextView
    @BindView(R.id.text_rs_200) lateinit var txtRS200: TextView
    @BindView(R.id.text_rs_500) lateinit var txtRS500: TextView
    @BindView(R.id.text_view_offers) lateinit var txtViewOffers: TextView
    @BindView(R.id.text_btn_add_cash) lateinit var txtAddCash: TextView
    @BindView(R.id.text_input_amount) lateinit var inputAmount: TextInputLayout
    @BindView(R.id.text_input_promo_code) lateinit var inputPromoCode: TextInputLayout
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

        txtRS100.setOnClickListener {
            onSetAmount("100")
        }
        txtRS200.setOnClickListener {
            onSetAmount("200")
        }
        txtRS500.setOnClickListener {
            onSetAmount("500")
        }
        txtViewOffers.setOnClickListener {
            onSetPromoCode("DESI100")
        }
        txtAddCash.setOnClickListener {
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
                    val intent = Intent(context, PaymentActivity::class.java)
                    intent.putExtra(KEY_ADD_CASH_AMT, amt)
                    intent.putExtra(HomeActivity.KEY_LOGIN, login)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                }else{
                    UtilMethods.ToastLong(context, "Something went wrong")
                }
            }

        }
    }
    private fun onSetAmount(amt: String){
        inputAmount.editText?.setText(amt)
    }
    private fun onSetPromoCode(promoCode: String){
        inputPromoCode.editText?.setText(promoCode)
    }
    override fun onBackPressed() { // do something here and don't write super.onBackPressed()
        super.onBackPressed()
        finish()
    }
    companion object {
        const val TAG = "AddCashScreen"
        const val KEY_ADD_CASH_AMT = "Key_add_cash_amount"
    }
}
