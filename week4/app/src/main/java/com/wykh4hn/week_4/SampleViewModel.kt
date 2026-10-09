package com.wykh4hn.week_4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SampleViewModel : ViewModel() {
    private val _count = MutableLiveData<Int>(0)
    var number = 0

    val badgeCount: LiveData<Int>
        get() = _count

    fun incrementBadgeCount() {
        _count.postValue(++number)
    }

    override fun onCleared() {
        super.onCleared()
    }
}
