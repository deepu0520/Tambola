package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {
    private var context: Context? = null
    internal val DELAY_MS: Long = 4000  //delay in milliseconds before task is to be executed
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_loading)
        supportActionBar?.hide()
        this.context = this@LoadingActivity
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()


        // handler
        Handler().postDelayed({
            // TODO Auto-generated method stub
            val intent = Intent(context, PlayActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_MS)

    }

}
