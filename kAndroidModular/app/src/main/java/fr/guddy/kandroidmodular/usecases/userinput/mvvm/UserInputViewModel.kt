package fr.guddy.kandroidmodular.usecases.userinput.mvvm

import android.arch.lifecycle.ViewModel
import fr.guddy.kandroidmodular.common.mvvm.SingleLiveEvent

class UserInputViewModel: ViewModel() {
    val model: UserInputModel = UserInputModel()
    val onSelectEvent = SingleLiveEvent<String>()

    fun onSelectButtonClicked() {
        onSelectEvent.postValue(model.user.get())
    }
}