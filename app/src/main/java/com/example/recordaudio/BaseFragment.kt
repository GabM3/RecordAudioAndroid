package com.example.recordaudio

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped


@AndroidEntryPoint
@ActivityScoped
abstract class BaseFragment : Fragment() {
    val app: App
        get() = (mainActivity.application as App)

    val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    val navigator: Navigator
        get() = mainActivity.navigator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnClickListener { /**/ }
    }
}