package test.zadanie.app.com

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class ViewModelTest:  ViewModel(){
   val mRequestQueue = Volley.newRequestQueue(TestVebViewApplication.getAppContext());
    val testUrl:String = "https://dat003.ru/clock.php?id=8hd6mmcpjgmxv7uk9dab"
    private var resultStringUrl= MutableLiveData<String>("")
    var urlString: LiveData<String> = resultStringUrl

     fun getWeather(url: String) {
        val request = JsonObjectRequest(
            Request.Method.GET,  //GET - API-запрос для получение данных
            url, null, { response ->
                try {
                    if(response.has("url")){
                        val resultUrl = response.getString("url")
                        saveRemoteString(TestVebViewApplication.getAppContext(), resultUrl)
                        resultStringUrl.postValue( resultUrl)
                        Log.d("STRING", resultUrl)
                    } else{
                        resultStringUrl.postValue("URL_NOT_FOUND")
                        Log.d("STRING", "URL_NOT_FOUND")
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            // в случае возникновеня ошибки
            error.printStackTrace()
        }
        mRequestQueue.add(request) // добавляем запрос в очередь
    }
}