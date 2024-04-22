package com.bluebubbles.telephony_plus.query

import android.content.Context
import android.database.Cursor
import android.net.Uri
import kotlin.collections.*
import kotlinx.coroutines.*
import io.flutter.plugin.common.MethodChannel.Result

private var tableUri = Uri.parse("content://mms-sms/conversations?simple=true")

class ThreadQuery {
    fun query(c: Context, r: Result) {
        // launch in coroutine (heavy IO task)
        CoroutineScope(Dispatchers.IO).launch {
            val threads = mutableListOf<HashMap<String, Any>>()
            // query the MMS/SMS table with provided params
            val cursor = c.contentResolver.query(tableUri, arrayOf("_id", "archived", "date", "error", "has_attachment", "message_count", "read", "recipient_ids", "snippet", "snippet_cs", "type"), null, null, null)
            while (cursor != null && cursor.moveToNext()) {
                val dataObject = HashMap<String, Any>()
                // get columns in the table, their data types, and their values. Insert into the
                // data object for the message.
                for (columnName in cursor.columnNames) {
                    val index = cursor.getColumnIndexOrThrow(columnName)
                    val type = cursor.getType(index)
                    val value : Any?
                    value = when (type) {
                        Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(index)
                        Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(index)
                        Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(index)
                        Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                        Cursor.FIELD_TYPE_NULL -> null
                        else -> null
                    }
                    if (value != null) {
                        dataObject[columnName] = value
                    }
                }
                dataObject["recipients"] = RecipientQuery().query(c, null, dataObject["recipient_ids"] as String)!!
                threads.add(dataObject)
            }
            cursor?.close()
            // send data back to Flutter
            r.success(threads)
        }
    }
}