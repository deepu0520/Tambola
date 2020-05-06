package com.newitzone.desitambola

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.desitambola.adapter.ResultAdapter
import com.newitzone.desitambola.utils.SharedPrefManager
import model.result.GameResult
import model.result.Result

class ResultActivity : AppCompatActivity() {
    private var context: Context? = null
    @BindView(R.id.text_btn_play_again) lateinit var tvPlayAgain: TextView
    @BindView(R.id.recycler_view_result) lateinit var rvResult: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_result)
        supportActionBar?.hide()
        this.context = this@ResultActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        val gameResult = intent.getSerializableExtra(PlayActivity.GAME_RESULT) as GameResult
        if (gameResult != null){
            // load game result
            onLoadRecyclerView(gameResult.resultList)
        }

        tvPlayAgain.setOnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(HomeActivity.KEY_LOGIN,SharedPrefManager.getInstance(context as ResultActivity).result)
            startActivity(intent)
            finish()
        }
    }
    private fun onLoadRecyclerView(resultList: List<Result>){
        val adapter = ResultAdapter(resultList, this)
        rvResult.layoutManager = LinearLayoutManager(context)
        rvResult.adapter = adapter
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                val intent = Intent(context, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra(HomeActivity.KEY_LOGIN,SharedPrefManager.getInstance(context as ResultActivity).result)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
