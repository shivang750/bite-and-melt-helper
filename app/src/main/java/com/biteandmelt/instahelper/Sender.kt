package com.biteandmelt.instahelper

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Sends Instagram follow notifications to the POS. Failed ones are kept and retried with the next one. */
object Sender {
    private const val PREFS = "cfg"

    fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun send(base: String, key: String, body: JSONObject): Int {
        val url = URL(base.trim().trimEnd('/') + "/api/insta-follows/ingest")
        val c = url.openConnection() as HttpURLConnection
        try {
            c.requestMethod = "POST"
            c.connectTimeout = 15000
            c.readTimeout = 15000
            c.doOutput = true
            c.setRequestProperty("Content-Type", "application/json")
            c.setRequestProperty("x-helper-key", key.trim())
            c.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            val code = c.responseCode
            try { (if (code in 200..299) c.inputStream else c.errorStream)?.close() } catch (_: Exception) {}
            return code
        } finally {
            c.disconnect()
        }
    }

    private fun setStatus(ctx: Context, msg: String) {
        prefs(ctx).edit().putString("status", msg).putLong("status_at", System.currentTimeMillis()).apply()
    }

    /** Returns a short message for the screen. Never throws. */
    @Synchronized
    fun post(ctx: Context, title: String, text: String, test: Boolean = false): String {
        val p = prefs(ctx)
        val base = p.getString("url", "") ?: ""
        val key = p.getString("key", "") ?: ""
        if (base.isBlank() || key.isBlank()) { val m = "POS link / key not saved yet"; setStatus(ctx, m); return m }
        val queue = try { JSONArray(p.getString("queue", "[]")) } catch (_: Exception) { JSONArray() }
        if (!test) queue.put(JSONObject().put("title", title).put("text", text))
        return try {
            if (test) {
                val code = send(base, key, JSONObject().put("test", true))
                val m = when (code) { 200 -> "Connected OK"; 401 -> "Wrong helper key"; else -> "Server answered $code" }
                setStatus(ctx, m); m
            } else {
                val left = JSONArray()
                var sent = 0
                var lastCode = 200
                for (i in 0 until queue.length()) {
                    val item = queue.getJSONObject(i)
                    val code = try { send(base, key, item) } catch (e: Exception) { -1 }
                    lastCode = code
                    if (code in 200..299) sent++ else if (code != 401 && code != 400) left.put(item) // keep for retry
                }
                p.edit().putString("queue", left.toString()).apply()
                val m = if (left.length() == 0) "Sent $sent follow notification(s)" else "Could not send (code $lastCode). ${left.length()} waiting to retry"
                setStatus(ctx, m); m
            }
        } catch (e: Exception) {
            if (!test) p.edit().putString("queue", queue.toString()).apply()
            val m = "No connection: " + (e.message ?: e.javaClass.simpleName)
            setStatus(ctx, m); m
        }
    }
}
