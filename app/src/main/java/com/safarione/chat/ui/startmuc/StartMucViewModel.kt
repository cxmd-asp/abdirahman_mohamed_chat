package com.safarione.chat.ui.startmuc

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StartMucViewModel: ViewModel() {
    var name by mutableStateOf("")
    val members = mutableStateMapOf<String, Unit>()
}