package com.bluebubbles.telephony_plus.query
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import kotlin.collections.*
import kotlinx.coroutines.*
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel.Result

private var tableUri = Telephony.MmsSms.CONTENT_CONVERSATIONS_URI
private var singleMmsUri = Uri.parse("content://mms/part")

class MessageQuery {
    fun query(context: Context, call: MethodCall, result: Result) {
        // launch in coroutine (heavy IO task)
        CoroutineScope(Dispatchers.IO).launch {
            val messages = mutableListOf<HashMap<String, Any>>()
            val threadId: Int? = call.argument("threadId")
            val getMmsData: Boolean = call.argument("mmsData")!!
            val getMmsAddress: Boolean = call.argument("mmsAddress")!!
            // query the MMS/SMS table with provided params
            val cursor = context.contentResolver.query(tableUri, null, if (threadId == null) null else "thread_id=${threadId}", null, null)
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
                // If requested, get the MMS data
                if (getMmsData && dataObject["ct_t"] == "application/vnd.wap.multipart.related") {
                    dataObject["mms_data"] = mutableListOf<HashMap<String, Any>>()
                    // query the MMS Part table with the MMS ID
                    val mmsCursor = context.contentResolver.query(singleMmsUri, null, "${Telephony.Mms.Part.MSG_ID}=${dataObject["_id"]}", null, null)
                    while (mmsCursor != null && mmsCursor.moveToNext()) {
                        val mmsDataObject = HashMap<String, Any>()
                        // get columns in the table, their data types, and their values. Insert into the
                        // data object for the MMS.
                        for (columnName in mmsCursor.columnNames) {
                            val index = mmsCursor.getColumnIndexOrThrow(columnName)
                            val type = mmsCursor.getType(index)
                            val value : Any?
                            value = when (type) {
                                Cursor.FIELD_TYPE_BLOB -> mmsCursor.getBlob(index)
                                Cursor.FIELD_TYPE_FLOAT -> mmsCursor.getFloat(index)
                                Cursor.FIELD_TYPE_INTEGER -> mmsCursor.getInt(index)
                                Cursor.FIELD_TYPE_STRING -> mmsCursor.getString(index)
                                Cursor.FIELD_TYPE_NULL -> null
                                else -> null
                            }
                            if (value != null) {
                                mmsDataObject[columnName] = value
                            }
                        }
                        @Suppress("UNCHECKED_CAST")
                        (dataObject["mms_data"] as MutableList<HashMap<String, Any>>).add(mmsDataObject)
                    }
                    mmsCursor?.close()
                }
                // If requested, get the MMS address
                if (getMmsAddress && dataObject["ct_t"] == "application/vnd.wap.multipart.related") {
                    // query the MMS address table with the MMS ID and type as PduHeaders.FROM (131)
                    val mmsAddrCursor = context.contentResolver.query(Uri.parse("content://mms/${dataObject["_id"]}/addr"), null, "${Telephony.Mms.Addr.MSG_ID}=${dataObject["_id"]} AND ${Telephony.Mms.Addr.TYPE}=137", null, null)
                    // Get the MMS address and contact ID, and add to the base message data object
                    while (mmsAddrCursor != null && mmsAddrCursor.moveToNext()) {
                        val number = mmsAddrCursor.getString(mmsAddrCursor.getColumnIndexOrThrow(Telephony.Mms.Addr.ADDRESS))
                        val contactId = mmsAddrCursor.getInt(mmsAddrCursor.getColumnIndexOrThrow(Telephony.Mms.Addr.CONTACT_ID))
                        if (number != null) {
                            dataObject["address"] = number
                        }
                        dataObject["person"] = contactId
                    }
                    mmsAddrCursor?.close()
                }
                messages.add(dataObject)
            }
            cursor?.close()
            // send data back to Flutter
            result.success(messages)
        }
    }
}