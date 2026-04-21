package com.dg.flex.ui.common

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dg.flex.data.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MediaPlayingState(
    val title: String? = null,
    val artist: String? = null,
    val artwork: Bitmap? = null,
    val packageName: String? = null,
    val isPlaying: Boolean = false,
    val needsAccess: Boolean = false,
    val hasSession: Boolean = false,
    val canAskAccess: Boolean = false,
    val activityIntent: PendingIntent? = null
)

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val preferences: PreferenceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MediaPlayingState())
    val state: StateFlow<MediaPlayingState> = _state.asStateFlow()

    init {
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun togglePlayPause() {
    }

    fun playNext() {
    }

    fun resetCanRequestAccess() {
        viewModelScope.launch {
            preferences.setDontWantNotificationAccess(false)
        }
    }
}
