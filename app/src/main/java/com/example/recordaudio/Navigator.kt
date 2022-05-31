package com.example.recordaudio

class Navigator {

    fun showHome(activity: MainActivity) {
        activity.loadAndClearStackFragment(RecordFragment.with())
    }
}