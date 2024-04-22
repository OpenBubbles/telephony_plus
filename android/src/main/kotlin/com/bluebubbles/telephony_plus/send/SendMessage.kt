package com.bluebubbles.telephony_plus.send

import android.content.Context
import android.content.Intent
import android.os.Handler
import kotlin.collections.*
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel.Result
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction

class SendMessage {
    fun sendMessage(context: Context, call: MethodCall, result: Result) {
        val addresses: ArrayList<String> = call.argument("addresses")!!
        val threadId: Int? = call.argument("threadId")
        val message: String? = call.argument("message")
        val subject: String? = call.argument("subject")
        val attachments: ArrayList<HashMap<String, Any>>? = call.argument("attachments")
        val isMMS = addresses.size > 1 || subject != null || attachments != null

        val settings = Settings()
        settings.useSystemSending = true
        val transaction = Transaction(context.applicationContext, settings)
        val toSend = Message(message, addresses.toTypedArray(), subject)
        if (attachments?.isNotEmpty() == true) {
            for (attachment in attachments) {
                toSend.addMedia(attachment["bytes"] as ByteArray, attachment["mimeType"] as String, attachment["filename"] as String)
            }
        }
        val tid = MySentReceiver.register(result)
        if (isMMS) {
            val sentIntent = Intent(context, MySentReceiver::class.java)
            sentIntent.putExtra("reqId", tid)
            transaction.setExplicitBroadcastForSentMms(sentIntent)
        } else {
            val sentIntent = Intent(context, MySentReceiver::class.java)
            sentIntent.putExtra("reqId", tid)
            transaction.setExplicitBroadcastForSentSms(sentIntent)
//            val deliveredIntent = Intent("SMS_DELIVERED")
//            deliveredIntent.putExtra("transactionId", transactionId)
//            transaction.setExplicitBroadcastForDeliveredSms(deliveredIntent)
        }
        transaction.sendNewMessage(toSend, threadId?.toLong() ?: Transaction.NO_THREAD_ID)
        Handler(context.mainLooper).postDelayed({
            // if it's still in here, clean it up as a timeout
            MySentReceiver.callbacks.remove(tid)?.success(99999999)
        }, 15000)
    }
}