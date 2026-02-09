package com.example.stackoverflow.features.details.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.stackoverflow.features.details.viewmodels.QuestionDetailViewModel
import com.example.stackoverflow.repository.models.AnswerItem
import com.example.stackoverflow.repository.models.Question
import dagger.hilt.android.AndroidEntryPoint

private object Colors {
    val Background = Color.White
    val TopBarBackground = Color(0xFFF8F9F9)
    val AcceptedGreen = Color(0xFF5FA537)
    val LinkBlue = Color(0xFF0074CC)
    val TagBackground = Color(0xFFFFCC80)
    val TagText = Color(0xFF8A4B00)
}

@AndroidEntryPoint
class QuestionDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val question = intent.getParcelableExtra<Question>("QUESTION")

        setContent {
            MaterialTheme {
                question?.let {
                    QuestionDetailScreen(question = it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    question: Question,
    viewModel: QuestionDetailViewModel = hiltViewModel()
) {
    val answers by viewModel.answers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(question.questionId) {
        viewModel.loadAnswers(question.questionId)
    }

    Scaffold(
        containerColor = Colors.Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("More Info", fontWeight = FontWeight.SemiBold)
                },
                colors = centerAlignedTopAppBarColors(containerColor = Colors.TopBarBackground)
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Colors.Background)
                .padding(horizontal = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(16.dp))
                QuestionHeader(question, viewModel)
                Spacer(modifier = Modifier.height(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "${question.answerCount} Answers",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                error != null -> {
                    item {
                        Text(
                            text = "Error loading answers: $error",
                            color = Color.Red
                        )
                    }
                }

                else -> {
                    items(answers) { answer ->
                        AnswerItemLayout(answer, viewModel)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun QuestionHeader(question: Question, viewModel: QuestionDetailViewModel) {
    Column {
        Text(
            text = question.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {

            Text(
                text = buildAnnotatedString {
                    append("Asked ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(viewModel.toTimeAgo(question.creationDate))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Answered ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(viewModel.toTimeAgo(question.lastActivityDate))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = buildAnnotatedString {
                    append("Viewed ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("${question.viewCount} times")
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        HorizontalDivider()
        HtmlText(html = question.body,)
        Spacer(modifier = Modifier.height(12.dp))
        TagRow(question.tags)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                append("Asked ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(viewModel.formatDate(question.creationDate))
                }
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            UserAvatar(question.owner.profileImage)

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = question.owner.displayName,
                color = Colors.LinkBlue,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AnswerItemLayout(answer: AnswerItem, viewModel: QuestionDetailViewModel) {
    Row {
        VoteColumn(answer.score)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            HtmlText(answer.body)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = buildAnnotatedString {
                    append("Answered ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(viewModel.formatDate(answer.creationDate))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (answer.isAccepted) {
                    Text(
                        "✔",
                        color = Colors.AcceptedGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                UserAvatar(answer.owner.profileImage)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = answer.owner.displayName,
                    color = Colors.LinkBlue,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun VoteColumn(score: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Votes",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun TagRow(tags: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .background(Colors.TagBackground, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = Colors.TagText
                )
            }
        }
    }
}

@Composable
private fun UserAvatar(imageUrl: String?) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "User Avatar",
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun HtmlText(
    html: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                setTextColor(android.graphics.Color.BLACK)
                textSize = 14f
                setLineSpacing(1f, 1.2f)
            }
        },
        update = { view ->
            view.text = HtmlCompat.fromHtml(
                html,
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
        }
    )
}
