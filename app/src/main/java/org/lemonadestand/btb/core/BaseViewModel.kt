package org.lemonadestand.btb.core.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aisynchronized.helper.Helper
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BaseRepository
import retrofit2.Response

open class BaseViewModel<T: BaseRepository<*>>(
    app: Application,
    private val repository: T
): AndroidViewModel(app) {
    val noInternet: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val error: LiveData<Response<*>>
        get() = repository.error

    protected fun hasInternetConnection(): Boolean = Helper.isNetworkConnectionAvailable()
}