package test.zadanie.app.com

import android.app.Application
import android.content.Context
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.yandex.metrica.YandexMetrica

import com.yandex.metrica.YandexMetricaConfig




const val ONESIGNAL_APP_ID ="0bef5a9d-c7ca-4376-b4f6-4c892872bd80"

const val APP_METRICA_API_KEY = "b9d8e2d2-efdc-4c7b-8cac-3668fa0ff402"
class TestVebViewApplication: Application() {
    init {
        app = this
    }
    override fun onCreate() {
        super.onCreate()

        // Verbose Logging set to help debug issues, remove before releasing your app.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // optIn will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.User.pushSubscription.optIn()


        // Init FirebaseApp for all processes
       // FirebaseApp.initializeApp(this)
        // Creating an extended library configuration.
        // Creating an extended library configuration.
        val config = YandexMetricaConfig.newConfigBuilder(APP_METRICA_API_KEY).build()
        // Initializing the AppMetrica SDK.
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)

    }

    companion object {
        private lateinit var app : TestVebViewApplication

        fun getAppContext(): Context = app.applicationContext
    }
}