package com.bluebubbles.telephony_plus

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import com.bluebubbles.telephony_plus.query.MessageQuery
import com.bluebubbles.telephony_plus.query.RecipientQuery
import com.bluebubbles.telephony_plus.query.ThreadQuery
import com.bluebubbles.telephony_plus.send.SendMessage
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


/** TelephonyPlusPlugin */
class TelephonyPlusPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context : Context

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "telephony_plus")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  @SuppressLint("MissingPermission")
  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    if (call.method == "message-query") {
      MessageQuery().query(context, call, result)
    } else if (call.method == "thread-query") {
      ThreadQuery().query(context, result)
    } else if (call.method == "recipient-query") {
      val list: List<Int>? = call.argument("ids")
      if (list != null) {
        RecipientQuery().query(context, result, list.joinToString(" "))
      }
    } else if (call.method == "send-message") {
      SendMessage().sendMessage(context, call, result)
    } else if (call.method == "set-default-app") {
      val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
      intent.putExtra(
        Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
        context.packageName
      )
      context.startActivity(intent)
    } else if (call.method == "number-query") {
      val mPhoneNumber = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
      result.success(mPhoneNumber.line1Number)
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
