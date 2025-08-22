package com.example.interchat.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

object JsonUtils {
    fun isJson(text: String): Boolean {
        val t = text.trim()
        if ((t.startsWith("{") && t.endsWith("}")) || (t.startsWith("[") && t.endsWith("]"))) {
            return true
        }
        return false
    }

    fun prettify(text: String): String {
        val t = text.trim()
        return try {
            val token = JSONTokener(t).nextValue()
            when (token) {
                is JSONObject -> token.toString(2)
                is JSONArray  -> token.toString(2)
                else          -> t
            }
        } catch (_: JSONException) {
            t
        }
    }
}
