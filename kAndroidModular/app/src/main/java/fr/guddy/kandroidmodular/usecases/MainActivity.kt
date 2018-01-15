package fr.guddy.kandroidmodular.usecases

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import au.com.ds.ef.EasyFlow
import au.com.ds.ef.FlowBuilder.from
import au.com.ds.ef.FlowBuilder.on
import fr.guddy.kandroidmodular.R
import fr.guddy.kandroidmodular.fsm.*
import fr.guddy.kandroidmodular.usecases.userinput.ShowingUserRepos
import fr.guddy.kandroidmodular.usecases.userinput.fsm.UserFilled
import fr.guddy.kandroidmodular.usecases.userinput.fsm.WaitingUserInput
import fr.guddy.kandroidmodular.usecases.userinput.fsm.clearUserInputResult
import fr.guddy.kandroidmodular.usecases.userinput.fsm.userInputResult
import fr.guddy.kandroidmodular.usecases.userinput.mvvm.UserInputFragment
import fr.guddy.kandroidmodular.usecases.userrepos.mvvm.UserReposFragment
import org.koin.android.architecture.ext.getViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var fsmViewModel: FsmViewModel
    private lateinit var flow: EasyFlow<FsmContext>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fsmViewModel = getViewModel()
        buildFsm()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            fsmViewModel.trigger(BackPressed)
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun buildFsm() {
        flow = from<FsmContext>(WaitingUserInput).transit(
                on(UserFilled).to(ShowingUserRepos).transit(
                        on(BackPressed).to(WaitingUserInput)
                )
        )
        flow.executor<FsmContext>(FsmUiThreadExecutor())
        flow.whenEnter(WaitingUserInput) { showUserInputFragment() }
        flow.whenEnter(ShowingUserRepos) { context ->
            showUserReposFragment(context.userInputResult.user)
        }
        flow.whenLeave(ShowingUserRepos) { context ->
            context.clearUserInputResult()
        }
        fsmViewModel.fsmModel.observe(
                this,
                Observer<FsmModel> { model ->
                    model?.let {
                        flow.start(model.forceEnterInitialState, model.flowContext)
                    }
                }
        )
    }

    private fun showUserInputFragment() {
        if (supportFragmentManager.findFragmentById(R.id.ActivityMain_ViewGroup_Container) == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.ActivityMain_ViewGroup_Container, UserInputFragment.newInstance())
                    .commit()
        }
    }

    fun showUserReposFragment(user: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.ActivityMain_ViewGroup_Container, UserReposFragment.newInstance(user))
                .addToBackStack(null)
                .commit()
    }

}