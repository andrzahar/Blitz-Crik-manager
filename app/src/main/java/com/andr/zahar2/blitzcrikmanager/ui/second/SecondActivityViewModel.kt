package com.andr.zahar2.blitzcrikmanager.ui.second

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andr.zahar2.blitzcrikmanager.api.Api
import com.andr.zahar2.blitzcrikmanager.data.GameState
import com.andr.zahar2.blitzcrikmanager.data.Manager
import com.andr.zahar2.blitzcrikmanager.data.UserScore
import com.andr.zahar2.blitzcrikmanager.data.question.Question
import com.zahar2.andr.data.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SecondActivityViewModel @Inject constructor(private val api: Api): ViewModel() {

    private val _stateGameState = mutableStateOf(GameState.BEFORE_START)
    val stateGameState: State<GameState> = _stateGameState

    private val _stateUsersScore = mutableStateOf(mapOf<String, Float>())
    val stateUsersScore: State<Map<String, Float>> = _stateUsersScore

    private val _stateQuestion = mutableStateOf(Question.emptyQuestion())
    val stateQuestion: State<Question> = _stateQuestion

    private val _stateVideo = mutableStateOf(Video.NONE)
    val stateVideo: State<Video> = _stateVideo

    init {
        api.userScoreListener().onEach {
            val old = _stateUsersScore.value.toMutableMap()
            old[it.name] = it.points?: 0f
            _stateUsersScore.value = old
        }.launchIn(viewModelScope)

        api.gameStateListener().onEach {
            _stateGameState.value = it
        }.launchIn(viewModelScope)

        api.questionListener().onEach {
            _stateQuestion.value = it
        }.launchIn(viewModelScope)

        api.videoListener().onEach {
            _stateVideo.value = it
        }.launchIn(viewModelScope)

        api.managerListener().launchIn(viewModelScope)
    }

    fun onManager(manager: Manager) {
        api.sendManager(manager).launchIn(viewModelScope)
    }

    fun onUserScore(userScore: UserScore) {
        api.sendUserScore(userScore).launchIn(viewModelScope)
    }
}