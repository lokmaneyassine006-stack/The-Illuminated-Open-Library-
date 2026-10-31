package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.data.auth.AuthManager
import com.example.ui.LibraryApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LibraryViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.util.configureAppCheck(this, intent)
        enableEdgeToEdge()

        val credentialManager = CredentialManager.create(this)
        AuthManager.attemptAutoSignIn(
            context = this,
            credentialManager = credentialManager,
            onAuthSuccess = {},
            onUnauthenticated = {},
            scope = lifecycleScope
        )

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LibraryApp(viewModel = viewModel)
                }
            }
        }
    }
}
