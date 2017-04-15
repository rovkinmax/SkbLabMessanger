package ru.rovkinmax.skblabmessanger.util

import android.app.Fragment
import android.app.FragmentManager

inline fun <reified T : Fragment> FragmentManager.showFragment(fragment: T): Unit {
    beginTransaction()
            .add(fragment, T::class.java.name)
            .commitAllowingStateLoss()
}
