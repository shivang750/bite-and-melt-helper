package com.biteandmelt.instahelper

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.DateFormat
import java.util.Date

class MainActivity : Activity() {
    private lateinit var access: TextView
    private lateinit var status: TextView
    private lateinit var url: EditText
    private lateinit var key: EditText

    private fun dp(n: Int) = (n * resources.displayMetrics.density).toInt()

    private fun text(s: String, size: Float = 15f, bold: Boolean = false): TextView = TextView(this).apply {
        text = s
        textSize = size
        if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        setPadding(0, dp(6), 0, dp(6))
    }

    private fun button(label: String, onClick: () -> Unit): Button = Button(this).apply {
        text = label
        isAllCaps = false
        setOnClickListener { onClick() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(24), dp(18), dp(24)) }
        val p = Sender.prefs(this)

        box.addView(text("Bite and Melt - Instagram Helper", 22f, true))
        box.addView(text("Aa phone ma Instagram ni \"started following you\" notification POS ne moklse. Bija koi notification nu kai vanchtu nathi."))

        access = text("")
        box.addView(access)
        box.addView(button("1. Notification access chalu karo") { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) })
        box.addView(button("2. Battery restriction band karo (jaruri)") { startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) })

        box.addView(text("3. POS link ane Helper key (POS > Settings ma che)", 15f, true))
        url = EditText(this).apply { hint = "https://....ts.net"; setText(p.getString("url", "")); inputType = InputType.TYPE_TEXT_VARIATION_URI; setSingleLine() }
        key = EditText(this).apply { hint = "Helper key"; setText(p.getString("key", "")); inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS; setSingleLine() }
        box.addView(url)
        box.addView(key)
        box.addView(button("Save ane test karo") {
            p.edit().putString("url", url.text.toString().trim()).putString("key", key.text.toString().trim()).apply()
            status.text = "Testing..."
            Thread {
                val r = Sender.post(applicationContext, "", "", test = true)
                runOnUiThread { status.text = r; refresh() }
            }.start()
        })

        status = text("")
        box.addView(status)
        box.addView(text("Test ma \"Connected OK\" aave to badhu taiyar che. Pachhi app band kari shako cho - te potey chalti rahse."))
        setContentView(ScrollView(this).apply { addView(box) })
    }

    override fun onResume() {
        super.onResume()
        // ask Android to reconnect the notification reader if it was stopped
        try { NotificationListenerService.requestRebind(ComponentName(this, FollowListener::class.java)) } catch (_: Exception) {}
        refresh()
    }

    private fun refresh() {
        val enabled = (Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: "").contains(packageName)
        access.text = if (enabled) "Notification access: CHALU che ✓" else "Notification access: BAND che. Niche nu button dabavi \"Bite and Melt Helper\" ne chalu karo."
        access.visibility = View.VISIBLE
        val p = Sender.prefs(this)
        val st = p.getString("status", "") ?: ""
        val at = p.getLong("status_at", 0L)
        if (st.isNotEmpty() && status.text.toString() != "Testing...") status.text = "Chhelo result: $st" + if (at > 0) " (" + DateFormat.getTimeInstance().format(Date(at)) + ")" else ""
    }
}
