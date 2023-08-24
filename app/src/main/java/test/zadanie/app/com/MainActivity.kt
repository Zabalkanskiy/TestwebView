package test.zadanie.app.com

import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivity : AppCompatActivity() {
    lateinit var viewModelTest: ViewModelTest
    private var webView: WebView? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        val remoteString: String = loadRemoteString(context = this)

            //internetCall
        if (isOnline()){
            viewModelTest = ViewModelProvider(this).get(ViewModelTest::class.java)
            viewModelTest.getWeather(viewModelTest.testUrl)
            viewModelTest.urlString.observe(this) { observ ->
                if(observ == "URL_NOT_FOUND"){

                    formViewActivity(savedInstanceState = savedInstanceState)
                } else{
                    var urlString = loadURLString(this)
                    if(urlString == ""){ urlString = observ }
                    webViewActivity(savedInstanceState = savedInstanceState, remoteUrl = urlString)
                }

            }

        } else{
            showNotInternetDialog()
        }



    }

    fun formViewActivity(savedInstanceState: Bundle?){
        setContentView(R.layout.activity_wellcome)
        val buttonSignUp = findViewById<Button>(R.id.login_btn_sign_up)
        buttonSignUp.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
        val buttonLogin = findViewById<Button>(R.id.login_btn_login)
        buttonLogin.setOnClickListener{
          val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }
    fun webViewActivity(savedInstanceState: Bundle?, remoteUrl:String){
        setContentView(R.layout.web_view_activity)
        val editTextAdress = findViewById<EditText>(R.id.web_view_editText)
        editTextAdress.setText(webView?.url.toString())






        webView = findViewById(R.id.webView)
        webView?.webViewClient= MyViewViewClient(editTextAdress)



      editTextAdress.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
          if (keyCode == KeyEvent.KEYCODE_ENTER  && event.action == KeyEvent.ACTION_UP ) {
              //Perform Code

             val text =  editTextAdress.text.toString()
              webView?.loadUrl(text)

              this.currentFocus?.let { view ->
                  val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                  imm?.hideSoftInputFromWindow(view.windowToken, 0)
              }
              return@OnKeyListener true
          }
          false
      })
        //webView!!.webChromeClient = ChromeClient()
        var webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.loadWithOverviewMode =true
        webSettings?.useWideViewPort =true
        webSettings?.domStorageEnabled =true
        webSettings?.databaseEnabled = true
        webSettings?.setSupportZoom(true)
        webSettings?.allowFileAccess = true
        webSettings?.allowContentAccess = true
        webSettings?.loadWithOverviewMode =true
        webSettings?.useWideViewPort =true
        webView?.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(
                Uri.parse(url)
            )

           // request.allowScanningByMediaScanner()

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(
                    url, contentDisposition, mimetype)
            )
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(
                applicationContext,
                "Downloading File",  //To notify the Client that the file is being downloaded
                Toast.LENGTH_LONG
            ).show()
        })





        webSettings?.javaScriptCanOpenWindowsAutomatically =true

        if( savedInstanceState != null){
            webView?.restoreState(savedInstanceState)
        } else webView?.loadUrl(remoteUrl)

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
    }





    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)
        webView?.restoreState(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        val currentUrl: String? = webView?.url
        saveURLString(this, currentUrl)
    }

    fun isOnline(): Boolean {

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }


    }



    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (webView != null && webView!!.canGoBack()) {
                if (webView!!.canGoBack()) {
                    webView!!.goBack()
                }
            } else{   showDialog()}
            //showing dialog and then closing the application..

        }
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setTitle("are you sure?")
            setMessage("want to close the application ?")
            setPositiveButton("Yes") { _, _ -> finish() }
            setNegativeButton("No", null)
            show()
        }
    }

    fun showNotInternetDialog(){
        MaterialAlertDialogBuilder(this).apply {
            setIcon(R.drawable.icon_error)
            setTitle("Error")
            setMessage("Please, check your Internet connection status and restart app")
            setPositiveButton("OK") { _, _ -> finish() }
            show()
        }

    }

    //override fun getOnBackInvokedDispatcher(): OnBackInvokedDispatcher {
   //     return super.getOnBackInvokedDispatcher()
  //  }

    companion object {
        private const val INPUT_FILE_REQUEST_CODE = 1
        private const val FILECHOOSER_RESULTCODE = 1
    }
}

const val PREFS_NAME = "TESTZADANIE"
const val REMOTE_STRING = "REMOTESTRING"
const val URLSTRING = "URLSTRING"
const val DEFAULT_STRING = ""

fun loadRemoteString(context: Context): String{
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val prefString = prefs.getString(REMOTE_STRING, DEFAULT_STRING)
    return  prefString ?: DEFAULT_STRING
}

fun saveRemoteString(context: Context, remoteString: String){
    val putstring = context.getSharedPreferences(PREFS_NAME, 0).edit().putString(REMOTE_STRING, remoteString).apply()
}

fun saveURLString(context: Context, url: String?){
    if (url != null){
        context.getSharedPreferences(PREFS_NAME, 0).edit().putString(URLSTRING, url).apply()
    }
}

fun loadURLString(context: Context):String{
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val prefString = prefs.getString(URLSTRING, DEFAULT_STRING)
    return  prefString ?: DEFAULT_STRING
}

class MyViewViewClient(val editText: EditText ): WebViewClient(){
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        // your code here
        editText.setText(url.toString())
        super.doUpdateVisitedHistory(view, url, isReload)
    }
}