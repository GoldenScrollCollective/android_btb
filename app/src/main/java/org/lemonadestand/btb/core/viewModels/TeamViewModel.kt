package org.lemonadestand.btb.core.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.core.models.MembersResponse
import org.lemonadestand.btb.core.repositories.TeamRepository

class TeamViewModel(
	app: Application,
	private val repository: TeamRepository
) : BaseViewModel<TeamRepository>(app, repository) {
	val response: MutableLiveData<MembersResponse>
		get() = repository.response

	fun getTeams(page: Int) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
		}
		repository.getTeams(page)
	}
}