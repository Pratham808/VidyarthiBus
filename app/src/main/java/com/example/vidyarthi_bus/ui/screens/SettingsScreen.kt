package com.example.vidyarthi_bus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var showLangDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ListItem(
                headlineContent = { Text("Dark Mode") },
                trailingContent = { 
                    Switch(
                        checked = isDarkMode, 
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    ) 
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Language") },
                supportingContent = { Text("Toggle English / Kannada") },
                modifier = Modifier.clickable { showLangDialog = true }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Logout") },
                colors = ListItemDefaults.colors(headlineColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.clickable { 
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        if (showLangDialog) {
            AlertDialog(
                onDismissRequest = { showLangDialog = false },
                title = { Text("Choose Language") },
                text = {
                    Column {
                        TextButton(onClick = { viewModel.setLanguage("en"); showLangDialog = false }) { Text("English") }
                        TextButton(onClick = { viewModel.setLanguage("kn"); showLangDialog = false }) { Text("ಕನ್ನಡ (Kannada)") }
                    }
                },
                confirmButton = {}
            )
        }
    }
}