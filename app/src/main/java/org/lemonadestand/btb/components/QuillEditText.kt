package org.lemonadestand.btb.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import org.lemonadestand.btb.utils.Utils

@SuppressLint("SetJavaScriptEnabled")
class QuillEditText @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
) : WebView(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = QuillEditText::class.java.simpleName
	}

	var html = ""
		get() = this.toString()

	init {
		settings.apply {
			javaScriptEnabled = true
			domStorageEnabled = true
			loadWithOverviewMode = true
			useWideViewPort = true
			builtInZoomControls = true
			displayZoomControls = false
			defaultTextEncodingName = "utf-8"
			setSupportZoom(false)
		}
		loadUrl("file:///android_asset/quill.html")
		addJavascriptInterface(JavaScriptInterface(), "Android")
		webViewClient = object : WebViewClient() {
			override fun onPageFinished(view: WebView?, url: String?) {
				super.onPageFinished(view, url)

				val accessToken = Utils.getData(context, Utils.TOKEN)
				val script = "setQuillUsers('${accessToken}')"
				evaluateJavascript(script, null)
			}

			override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
				super.onReceivedError(view, request, error)
				Log.e(TAG, "onReceivedError: $error")
			}
		}
		webChromeClient = object : WebChromeClient() {
			override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
				return super.onConsoleMessage(consoleMessage)
			}
		}
	}

	private inner class JavaScriptInterface {

		@JavascriptInterface
		fun showToast(message: String) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
		}
	}
}