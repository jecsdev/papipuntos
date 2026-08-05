package com.jecsdev.papipuntos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.jecsdev.papipuntos.data.remote.handleAuthDeepLink

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // The OAuth redirect can be the very intent that started us (app was not running)...
        handleAuthDeepLink(intent)

        setContent {
            App()
        }
    }

    // ...or it arrives on the existing instance, which `launchMode="singleTop"` guarantees
    // instead of stacking a second copy of the activity on top.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleAuthDeepLink(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
