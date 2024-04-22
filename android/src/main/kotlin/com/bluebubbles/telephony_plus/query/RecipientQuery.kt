package com.bluebubbles.telephony_plus.query

import android.content.Context
import android.database.Cursor
import android.net.Uri
import kotlin.collections.*
import kotlinx.coroutines.*
import io.flutter.plugin.common.MethodChannel.Result

private var tableUri = Uri.parse("content://mms-sms/canonical-addresses")

class RecipientQuery {
    fun query(c: Context, r: Result?, a: String) : MutableList<HashMap<String, Any>>? {
        // launch in coroutine (heavy IO task)
        if (r != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val addresses = mutableListOf<HashMap<String, Any>>()
                // query the MMS/SMS table with provided params
                val recipients = a.split(" ")
                val cursor = c.contentResolver.query(tableUri, null, "_ID IN ('${recipients.joinToString("', '")}')", null, null)
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
                    addresses.add(dataObject)
                }
                cursor?.close()
                // send data back to Flutter
                r.success(addresses)
            }
            return null
        } else {
            val addresses = mutableListOf<HashMap<String, Any>>()
            // query the MMS/SMS table with provided params
            val recipients = a.split(" ")
            val cursor = c.contentResolver.query(tableUri, null, "_ID IN ('${recipients.joinToString("', '")}')", null, null)
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
                addresses.add(dataObject)
            }
            cursor?.close()
            // send data back to Flutter
            return addresses
        }
    }
}