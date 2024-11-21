package org.lemonadestand.btb.core.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseRepository
import org.lemonadestand.btb.core.models.MembersResponse
import org.lemonadestand.btb.network.RetrofitInstance
import org.lemonadestand.btb.singleton.Singleton

class TeamRepository : BaseRepository<MembersResponse>() {

	suspend fun getTeams(page: Int) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getTeams(
				limit = Singleton.API_LIST_LIMIT,
				page = page,
			)
			if (response.isSuccessful) {
				this@TeamRepository.response.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}
}