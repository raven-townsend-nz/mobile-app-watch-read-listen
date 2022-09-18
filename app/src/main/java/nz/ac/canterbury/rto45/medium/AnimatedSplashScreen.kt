/** Based on tutorial at: https://www.youtube.com/watch?v=SLZPgdek18o&ab_channel=Stevdza-San */

package nz.ac.canterbury.rto45.medium

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Tv
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false)}
    val alphaAnimation = animateFloatAsState(
        targetValue = if(startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 2000
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3000)
        navController.popBackStack()
        navController.navigate(Screen.Watch.route)
    }
    Splash(alpha = alphaAnimation.value)
}

@Composable
fun Splash(alpha: Float) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .background(if (isSystemInDarkTheme()) Color.Black else Color(0xFFBB86FC))
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha),
                imageVector = Icons.Filled.Tv,
                contentDescription = context.getString(R.string.watch_list),
                tint = Color.White
            )
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha),
                imageVector = Icons.Filled.AutoStories,
                contentDescription = context.getString(R.string.read_list),
                tint = Color.White
            )
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(alpha),
                imageVector = Icons.Filled.Audiotrack,
                contentDescription = context.getString(R.string.listen_list),
                tint = Color.White
            )
        }
    }
}