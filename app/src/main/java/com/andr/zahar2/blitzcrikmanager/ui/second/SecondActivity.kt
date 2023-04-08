package com.andr.zahar2.blitzcrikmanager.ui.second

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andr.zahar2.blitzcrikmanager.data.GameState
import com.andr.zahar2.blitzcrikmanager.data.Manager
import com.andr.zahar2.blitzcrikmanager.data.UserScore
import com.andr.zahar2.blitzcrikmanager.data.question.Question
import com.andr.zahar2.blitzcrikmanager.data.question.QuestionState
import com.andr.zahar2.blitzcrikmanager.ui.theme.BlitzCrikManagerTheme
import com.zahar2.andr.data.Video
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SecondActivity : ComponentActivity() {

    private lateinit var managerChange: (Manager) -> Unit

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            if (it.action == KeyEvent.ACTION_DOWN) {
                when (it.keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        managerChange(Manager.BACK)
                        return true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        managerChange(Manager.NEXT)
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {

            val viewModel = hiltViewModel<SecondActivityViewModel>()

            val gameState = viewModel.stateGameState.value
            val users = viewModel.stateUsersScore.value
            val question = viewModel.stateQuestion.value
            val video = viewModel.stateVideo.value

            managerChange = viewModel::onManager

            Content(
                gameState = gameState,
                usersScore = users,
                question = question,
                video = video,
                onUserScore = viewModel::onUserScore
            )
        }
    }
}

private fun pointsToString(points: Float): String {
    val d2 = points % 1
    val res = if (d2 == 0f) {
        (points - d2).toInt().toString()
    } else {
        points.toString().replace('.', ',')
    }
    return res
}

@Composable
private fun Content(
    gameState: GameState,
    usersScore: Map<String, Float>,
    question: Question,
    video: Video,
    onUserScore: (UserScore) -> Unit
) {
    BlitzCrikManagerTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    for (user in usersScore) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.key.uppercase(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = pointsToString(user.value),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row {
                                Button(
                                    onClick = { onUserScore(UserScore(user.key, user.value + 1)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("+1")
                                }
                                Button(
                                    onClick = { onUserScore(UserScore(user.key, user.value + 2)) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("+2")
                                }
                            }
                            Row {
                                var p by remember { mutableStateOf("0.0") }
                                TextField(
                                    value = p,
                                    onValueChange = { p = it },
                                    modifier = Modifier.weight(1f)
                                )
                                val value = p.toFloatOrNull()
                                Button(
                                    onClick = {
                                        onUserScore(
                                            UserScore(
                                                user.key,
                                                user.value + value!!
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = value != null
                                ) {
                                    Text("+")
                                }
                                Button(
                                    onClick = {
                                        onUserScore(
                                            UserScore(
                                                user.key,
                                                user.value - value!!
                                            )
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    enabled = value != null
                                ) {
                                    Text("-")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "State = $gameState, video = $video, round = ${question.roundName}, ${question.number + 1} / ${question.total}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = question.questionState.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = question.author + "\n" + question.question,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 15.sp,
                    color = if (question.questionState != QuestionState.INVISIBLE) Color.Green else Color.Unspecified
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = question.answer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 15.sp,
                    color = if (question.questionState == QuestionState.ANSWER) Color.Red else Color.Unspecified
                )
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = question.comment,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.DESKTOP
)
@Composable
fun BeforeStartPreview() {
    Content(
        gameState = GameState.SPLASH_SCREEN,
        usersScore = mapOf(
            "Матвей" to 1f,
            "Полина" to 2f,
            "Анастейша" to 0f,
            "Диана" to 4f
        ),
        question = Question(
            "Джордж Гордон Байрон",
            "Всем известно, что древнегреческие статуи изображали усредненные, но прекрасные версии персонажей или реальных личностей. Эти фигуры являлись идеальными в понимании греков. Одна из античных статуй Венеры носит имя Каллипига, что переводится буквально как…",
            "сойдет с ума и ужалит себя до \nсмерти",
            "",
            QuestionState.ANSWER,
            "",
            2,
            10
        ),
        video = Video.NONE
    ) {

    }
}