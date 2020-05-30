package com.newitzone.desitambola.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.Window
import android.widget.TextView
import com.newitzone.desitambola.R

class MessageDialog(context: Context, title: String, body: String) : Dialog(context) {

    private lateinit var tVTitle: TextView
    private lateinit var tVBody: TextView
    private lateinit var tVOk: TextView

    private val mTitle = title
    private val mBody = body

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_message)
        tVTitle = findViewById<TextView>(R.id.text_title)
        tVBody = findViewById<TextView>(R.id.text_body)
        tVOk = findViewById<TextView>(R.id.text_ok)
        // TODO: set Title and Body
        setText()
        // TODO: ok to dismiss dialog
        tVOk.setOnClickListener {
            dismiss()
        }
    }
    private fun setText(){
        if (mTitle.isNotEmpty()) {
            tVTitle.text = mTitle
            tVTitle.visibility = View.VISIBLE
        }else{
            tVTitle.text = mTitle
            tVTitle.visibility = View.GONE
        }
        if (mBody.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tVBody.text = Html.fromHtml(mBody, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tVBody.text = Html.fromHtml(mBody)
            }
            tVBody.visibility = View.VISIBLE
        }else{
            tVBody.text = mBody
            tVBody.visibility = View.GONE
        }
    }
}