package org.lemonadestand.btb.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.text.HtmlCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.utils.Storage

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

	var value: String = ""
		set(value) {
			field = value
			Handler().postDelayed({
				evaluateJavascript("setQuillContent($value)", null)
			}, 1000)
		}
	var onReceivedHtml: ((value: String) -> Unit)? = null

	init {
		isFocusable = true
		isFocusableInTouchMode = true
		settings.apply {
			javaScriptEnabled = true
			domStorageEnabled = true
			loadWithOverviewMode = true
			useWideViewPort = true
			builtInZoomControls = false
			displayZoomControls = false
			defaultTextEncodingName = "utf-8"
			setSupportMultipleWindows(true)
			setSupportZoom(false)
		}
		setOnLongClickListener {
			val popupMenu = PopupMenu(context, this)
			popupMenu.show()
			popupMenu.dismiss()
			true
		}
		webViewClient = object : WebViewClient() {
			override fun onPageFinished(view: WebView?, url: String?) {
				super.onPageFinished(view, url)

				evaluateJavascript("setQuillContent('$value')", null)
				evaluateJavascript("setQuillUsers('${Storage.rawToken}')", null)
			}

			override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
				super.onReceivedError(view, request, error)
				Log.e(TAG, "onReceivedError: $error")
			}

			override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
				request ?: return super.shouldInterceptRequest(view, request)
				if (request.url.toString().startsWith("https://api.buildthenbless.app")) {
					request.requestHeaders?.apply {
						set("X-API-KEY", Storage.rawToken)
					}
				}
				return super.shouldInterceptRequest(view, request)
			}
		}
		webChromeClient = object : WebChromeClient() {
			override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
				result?.cancel()
				return true
			}

			override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
				result?.cancel()
				return true
			}

			override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
				result?.cancel()
				return true
			}

			override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
				return super.onConsoleMessage(consoleMessage)
			}
		}
		loadUrl("file:///android_asset/quill.html")
		addJavascriptInterface(JavaScriptInterface(), "Android")
	}

	fun requestHtml() {
		val script = "sendContentToAndroid()"
		evaluateJavascript(script, null)
	}

	private inner class JavaScriptInterface {

		@JavascriptInterface
		fun showToast(message: String) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
		}

		@JavascriptInterface
		fun handleHtml(html: String) {
			val value = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
			CoroutineScope(Dispatchers.Main).launch { onReceivedHtml?.invoke(if (value.isBlank()) "" else html) }
		}
	}
}