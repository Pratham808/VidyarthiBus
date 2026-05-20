package com.example.vidyarthi_bus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vidyarthi_bus.domain.model.Route
import com.example.vidyarthi_bus.domain.model.AutoContact
import com.example.vidyarthi_bus.domain.model.Announcement
import com.example.vidyarthi_bus.domain.model.AnnouncementType
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    var routeName by remember { mutableStateOf("") }
    var from by remember { mutableStateOf("") }
    var to by remember { mutableStateOf("") }
    
    var autoName by remember { mutableStateOf("") }
    var autoPhone by remember { mutableStateOf("") }
    var autoVillage by remember { mutableStateOf("") }

    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AdminStatCard("Routes", "12", Icons.Default.BusAlert, Modifier.weight(1f))
                AdminStatCard("Reports", "450", Icons.Default.Assessment, Modifier.weight(1f))
                AdminStatCard("Users", "1.2k", Icons.Default.People, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Announcement System
            Text("Broadcast Alert", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = alertTitle, onValueChange = { alertTitle = it }, label = { Text("Alert Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = alertMessage, onValueChange = { alertMessage = it }, label = { Text("Alert Message") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val db = FirebaseDatabase.getInstance().getReference("announcements")
                    val id = db.push().key ?: ""
                    val alert = Announcement(id, alertTitle, alertMessage, AnnouncementType.EMERGENCY)
                    db.child(id).setValue(alert)
                    alertTitle = ""; alertMessage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Send Real-time Broadcast") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // Route Management
            Text("Route Management", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = routeName, onValueChange = { routeName = it }, label = { Text("Route Name") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = from, onValueChange = { from = it }, label = { Text("From") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = to, onValueChange = { to = it }, label = { Text("To") }, modifier = Modifier.weight(1f))
            }
            Button(
                onClick = {
                    val db = FirebaseDatabase.getInstance().getReference("routes")
                    val id = db.push().key ?: ""
                    val route = Route(id = id, name = routeName, from = from, to = to)
                    db.child(id).setValue(route)
                    routeName = ""; from = ""; to = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Publish Route") }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            // Auto Management
            Text("Auto Service Management", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = autoName, onValueChange = { autoName = it }, label = { Text("Driver Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = autoPhone, onValueChange = { autoPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = autoVillage, onValueChange = { autoVillage = it }, label = { Text("Village Area") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val db = FirebaseDatabase.getInstance().getReference("auto_contacts")
                    val id = db.push().key ?: ""
                    val auto = AutoContact(id = id, name = autoName, phoneNumber = autoPhone, village = autoVillage)
                    db.child(id).setValue(auto)
                    autoName = ""; autoPhone = ""; autoVillage = ""
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text("Register Auto Service") }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}