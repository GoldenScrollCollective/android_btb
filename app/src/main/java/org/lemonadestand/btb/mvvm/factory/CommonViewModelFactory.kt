package org.lemonadestand.btb.mvvm.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.core.repositories.CompaniesRepository
import org.lemonadestand.btb.core.repositories.MemberRepository
import org.lemonadestand.btb.core.viewModels.CompaniesViewModel
import org.lemonadestand.btb.core.viewModels.MemberViewModel
import org.lemonadestand.btb.mvvm.repository.EventRepository
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.repository.InterestRepository
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.EventViewModel
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.mvvm.viewmodel.InterestViewModel
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel


@Suppress("UNCHECKED_CAST")
class CommonViewModelFactory(val app: Application, private val repository: Any) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app, repository as HomeRepository) as T
        } else if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(app, repository as UserRepository) as T
        } else if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(app, repository as EventRepository) as T
        } else if (modelClass.isAssignableFrom(InterestViewModel::class.java)) {
            return InterestViewModel(app, repository as InterestRepository) as T
        } else if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            return MemberViewModel(app, repository as MemberRepository) as T
        } else if (modelClass.isAssignableFrom(CompaniesViewModel::class.java)) {
            return CompaniesViewModel(app, repository as CompaniesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}