package org.lemonadestand.btb.core

import androidx.lifecycle.MutableLiveData
import retrofit2.Response

abstract class BaseRepository<T> {
    val response = MutableLiveData<T>()
    val error = MutableLiveData<Response<*>>()
}