package com.biteandmelt.instahelper

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/** Reads Instagram notifications on this phone. Only ones that mention "follow" are sent to the POS. */
class FollowListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName != "com.instagram.android" && sbn.packageName != "com.instagram.lite") return
        val e = sbn.notification?.extras ?: return
        val title = e.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val lines = ArrayList<String>()
        e.getCharSequence(Notification.EXTRA_TEXT)?.let { lines.add(it.toString()) }
        e.getCharSequence(Notification.EXTRA_BIG_TEXT)?.let { lines.add(it.toString()) }
        e.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach { lines.add(it.toString()) }
        val text = lines.distinct().joinToString("\n")
        if (!(title + " " + text).lowercase().contains("follow")) return
        val ctx = applicationContext
        Thread { Sender.post(ctx, title, text) }.start()
    }
}
