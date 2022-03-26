package com.example.refesher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.refesher.ui.theme.RefesherTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RefesherTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CustomPullRefresher()

                }
            }
        }
    }
}

@Composable
fun CustomPullRefresher() {
    val items = remember { mutableStateListOf<Int>() }
    items.add(1)
    var k : Int = 1


    var isRefreshing by remember { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Pull To Refresh",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                }
            )
        }
    ) {
        Column {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                },
                indicator = { state, refreshTrigger ->
                    CustomViewPullRefreshView(state, refreshTrigger)
                }
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items.size) {
                        ColumnRefreshItems(k)
                        k++
                    }
                }
                LaunchedEffect(isRefreshing) {
                    if (isRefreshing) {
                        delay(100L)
                        items.add(k)
                        isRefreshing = false
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnRefreshItems(number: Int) {
    Card(
        modifier = Modifier.wrapContentSize(),
        elevation = 10.dp, shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$number", color = Color.Black, fontFamily = FontFamily.Serif)
        }
    }
}

@Composable
fun CustomViewPullRefreshView(
    state: SwipeRefreshState,
    refreshTrigger: Dp,
    color: Color = MaterialTheme.colors.primary
) {
    Box(
        modifier = Modifier
            .drawWithCache {
                onDrawBehind {
                    val distance = refreshTrigger.toPx()
                    val progress = (state.indicatorOffset / distance).coerceIn(0f, 1f)
                    val brush = Brush.verticalGradient(
                        0f to color.copy(alpha = 0.45f),
                        1f to color.copy(alpha = 0f)
                    )
                    drawRect(
                        brush = brush,
                        alpha = FastOutLinearInEasing.transform(progress)
                    )
                }
            }
            .fillMaxWidth()
            .height(80.dp)
    ){
        if (state.isRefreshing){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(), color = Color.Red
            )
        }else{
            val trigger = with(LocalDensity.current){refreshTrigger.toPx()}
            val progress = (state.indicatorOffset / trigger).coerceIn(0f,1f)
            LinearProgressIndicator(
                progress = progress, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
