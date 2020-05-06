package com.newitzone.desitambola

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.desitambola.adapter.CashOrChipsTransAdapter
import com.newitzone.desitambola.adapter.MoneyTransAdapter
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import model.money.transaction.MoneyTrans
import model.transaction.CashOrChipsTrans
import retrofit.TambolaApiService
import java.util.*


class TransactionActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener{
    private var context: Context? = null
    private lateinit var login: Result
    private var type = 0

    @BindView(R.id.text_from_date) lateinit var txtFromDate: TextView
    @BindView(R.id.text_to_date) lateinit var txtToDate: TextView
    @BindView(R.id.spinner_type) lateinit var spnType: Spinner

    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_transaction)
        supportActionBar?.hide()
        this.context = this@TransactionActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        // Intent
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result

        if (login != null) {
            txtFromDate.setOnClickListener {
                onFromDate(context as TransactionActivity)
            }
            txtToDate.setOnClickListener {
                onToDate(context as TransactionActivity)
            }
            spnType!!.onItemSelectedListener = this
        }
    }
    private fun onFromDate(context: Context){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            txtFromDate.text = "" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year
        }, year, month, day)

        dpd.show()
    }
    private fun onToDate(context: Context) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            txtToDate.text = "" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year
        }, year, month, day)

        dpd.show()
    }
    private fun cashOrChipsRecyclerView(context: Context, cashOrChipsTrans: CashOrChipsTrans){
        if (cashOrChipsTrans.status == 1){
            val adapter  = CashOrChipsTransAdapter(cashOrChipsTrans.result.first(), context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }else{
            val adapter  = CashOrChipsTransAdapter(emptyList(), context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }
    }
    private fun moneyRecyclerView(context: Context, moneyTrans: MoneyTrans){
        if (moneyTrans.status == 1){
            val adapter  = MoneyTransAdapter(moneyTrans.result.first(), context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }else{
            val adapter  = MoneyTransAdapter(emptyList(), context)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun userTransListApi(context: Context, userid: String, sessionId: String
                                 , fromdt: String, todt: String, type: Int){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = if (type == 2) service.userMoneyTransList(userid,sessionId,fromdt,todt,type) else service.userCashOrChipsTransList(userid,sessionId,fromdt,todt,type)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (type == 2){
                                    val res = response.body() as MoneyTrans
                                    if (res.status == 1){
                                        moneyRecyclerView(context, res)
                                    }else{
                                        UtilMethods.ToastLong(context, "${res.msg}")
                                        moneyRecyclerView(context, res)
                                    }
                                } else {
                                    val res = response.body() as CashOrChipsTrans
                                    if (res.status == 1){
                                        cashOrChipsRecyclerView(context, res)
                                    }else{
                                        UtilMethods.ToastLong(context, "${res.msg}")
                                        cashOrChipsRecyclerView(context, res)
                                    }
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.message()}")
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        // TODO: ("Not yet implemented")
        type = position
        val fromDt = UtilMethods.getDateYYYYMMDD(txtFromDate.text.toString())
        val toDt = UtilMethods.getDateYYYYMMDD(txtToDate.text.toString())
        // TODO: Call user trans list api
        userTransListApi(context as TransactionActivity, login.id, login.sid, fromDt, toDt, type)
    }
    override fun onNothingSelected(p0: AdapterView<*>?) {
        // TODO: ("Not yet implemented")
    }
}
