package com.newitzone.desitambola.utils

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.newitzone.desitambola.R
import kotlinx.android.synthetic.main.custom_toast.view.*

class KCustomToast {
    companion object {
        private const val LONG_DELAY = 3500 // 3.5 seconds
        private const val SHORT_DELAY = 2000 // 2 seconds
        private lateinit var layoutInflater: LayoutInflater

        fun infoToast(context: Activity, message: String) {

            layoutInflater = LayoutInflater.from(context)
            val layout = layoutInflater.inflate(R.layout.custom_toast, (context).findViewById(R.id.custom_toast))
            val drawable = ContextCompat.getDrawable(context, R.drawable.toast_round_bg_1)
            drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.color_yellow), PorterDuff.Mode.MULTIPLY)
            layout.background = drawable
            layout.custom_toast_message.setTextColor(Color.BLACK)
            layout.custom_toast_message.text = message

            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.view = layout //setting the view of custom toast layout
            toast.show()
        }
        fun infoToast(context: Activity, message: String, drawableRes: Int) {

            layoutInflater = LayoutInflater.from(context)
            val layout = layoutInflater.inflate(R.layout.custom_toast, (context).findViewById(R.id.custom_toast))
            layout.custom_toast_image.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
            val drawable = ContextCompat.getDrawable(context, R.drawable.toast_round_bg)
            drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.color_yellow), PorterDuff.Mode.MULTIPLY)
            layout.background = drawable
            layout.custom_toast_message.setTextColor(Color.BLACK)
            layout.custom_toast_message.text = message

            val toast = Toast(context.applicationContext)
            toast.duration = Toast.LENGTH_LONG
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.view = layout //setting the view of custom toast layout
            toast.show()
        }
        fun toastWithFont(context: Activity, message: String) {
            layoutInflater = LayoutInflater.from(context)
            val layout = layoutInflater.inflate(R.layout.custom_toast, (context).findViewById(R.id.custom_toast))
            //layout.custom_toast_image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_information))
            val drawable = ContextCompat.getDrawable(context, R.drawable.toast_round_bg)
            //drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.info), PorterDuff.Mode.MULTIPLY)
            layout.background = drawable
            layout.custom_toast_message.setTextColor(Color.BLACK)
            layout.custom_toast_message.text = message
            layout.custom_toast_message.typeface = ResourcesCompat.getFont(context,R.font.maldini_bold)
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_LONG
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.view = layout//setting the view of custom toast layout
            toast.show()
        }
    }
}