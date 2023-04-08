package com.andr.zahar2.blitzcrikmanager.api

import com.andr.zahar2.blitzcrikmanager.data.*
import com.andr.zahar2.blitzcrikmanager.data.question.Question
import com.andr.zahar2.blitzcrikmanager.data.question.toQuestion
import com.zahar2.andr.data.Video
import com.zahar2.andr.data.toVideo
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Api(private val client: HttpClient) {

    var host = "192.168.10.89"
    var port = 2207
    private val userPath = "/user"
    private val gamePath = "/state"
    private val questionPath = "/question"
    private val videoPath = "/video"
    private val managerPath = "/manager"

    private fun <T> baseWebSocket(path: String, toType: (String) -> T): Flow<T> = flow {
        client.webSocket(host = host, port = port, path = path) {
            incoming.consumeEach { frame ->
                if (frame !is Frame.Text) return@consumeEach
                val data = toType(frame.readText())
                emit(data)
            }
        }
    }

    fun gameStateListener(): Flow<GameState> = baseWebSocket(gamePath) { it.toGameState() }

    fun questionListener(): Flow<Question> =
        baseWebSocket(questionPath) { it.toQuestion() }

    fun videoListener(): Flow<Video> =
        baseWebSocket(videoPath) { it.toVideo() }

    private lateinit var userScoreSocket: WebSocketSession

    fun userScoreListener(): Flow<UserScore> = flow {
        userScoreSocket = client.webSocketSession(host = host, port = port, path = userPath)
        userScoreSocket.incoming.consumeEach { frame ->
            if (frame !is Frame.Text) return@consumeEach
            val userScore = frame.readText().toUserScore()
            emit(userScore)
        }
    }

    fun sendUserScore(userScore: UserScore): Flow<Any> = flow {
        userScoreSocket.send(userScore.toString())
    }

    private lateinit var managerSocket: WebSocketSession

    fun managerListener(): Flow<Manager> = flow {
        managerSocket = client.webSocketSession(host = host, port = port, path = managerPath)
        managerSocket.incoming.consumeEach { frame ->
            if (frame !is Frame.Text) return@consumeEach
            val manager = frame.readText().toManager()
            emit(manager)
        }
    }

    fun sendManager(manager: Manager): Flow<Any> = flow {
        managerSocket.send(manager.toString())
    }
}