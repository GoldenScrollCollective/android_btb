package org.lemonadestand.btb.core.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.core.models.MemberListResponseModel
import org.lemonadestand.btb.core.repositories.MemberRepository

class MemberViewModel(
    app: Application,
    private val memberRepository: MemberRepository
): BaseViewModel<MemberRepository>(app, memberRepository) {
    val response: MutableLiveData<MemberListResponseModel>
        get() = memberRepository.response

    fun getTeams(page: Int) = viewModelScope.launch {
        if (hasInternetConnection()) {
            memberRepository.getTeams(page)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }
}