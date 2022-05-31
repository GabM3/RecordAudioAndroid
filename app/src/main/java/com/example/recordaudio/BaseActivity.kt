package com.example.recordaudio

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity: AppCompatActivity() {
    @Inject
    lateinit var navigator: Navigator

    var contentId: Int = androidx.appcompat.R.id.content

    fun loadAndClearStackFragment(fragment: Fragment) {

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(contentId, fragment, fragment::class.qualifiedName)
        fragmentTransaction.commit()
    }

}