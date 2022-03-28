package com.test.searchbook.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.android.support.DaggerAppCompatActivity

open class BaseActivity : DaggerAppCompatActivity() {

    inline fun <reified T : Fragment> hasFragment(
        fragmentManager: FragmentManager,
        tag: String
    ): T? {
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment is T) {
            return fragment
        }
        return null
    }

    inline fun <reified T : Fragment> replaceFragmentIfNotExists(
        fragmentManager: FragmentManager,
        contentId: Int,
        tag: String,
        supplier: () -> T
    ): T {
        val oldFragment: T? = hasFragment(fragmentManager, tag)
        if (oldFragment != null) {
            return oldFragment
        }
        val fragment = supplier()
        fragmentManager.beginTransaction()
            .replace(contentId, fragment, tag)
            .setPrimaryNavigationFragment(fragment)
            .addToBackStack(tag)
            .commitAllowingStateLoss()
        return fragment
    }

}