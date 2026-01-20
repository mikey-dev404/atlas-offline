package com.mikey.atlasoffline

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke


@Composable
fun MoreScreen(
    viewModel: AtlasViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var tapCount by remember { mutableStateOf(0) }
    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            delay(3000)
            tapCount = 0
        }
    }

    MinimalBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                Text(
                    text = "MORE",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    modifier = Modifier.clickable {
                        tapCount++
                        if (tapCount >= 7) {
                            showResetDialog = true
                            tapCount = 0
                        }
                    }
                )
            }

            item {
                Text(
                    text = "TOOLS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.6f),
                    letterSpacing = 2.sp
                )
            }

            item {
                MoreMenuItem(
                    title = "Body Measurements",
                    subtitle = "Track your progress",
                    icon = Icons.Default.MonitorWeight,  // Changed from StraightenSharp
                    onClick = { navController.navigate("measurements") }
                )
            }


            item {
                MoreMenuItem(
                    title = "Plate Calculator",
                    subtitle = "Calculate barbell loading",
                    icon = Icons.Default.Calculate,
                    onClick = { navController.navigate("platecalc") }
                )
            }

            item {
                MoreMenuItem(
                    title = "Export Data",
                    subtitle = "Backup your workouts",
                    icon = Icons.Default.CloudUpload,
                    onClick = { exportData(context, viewModel) }
                )
            }

            item {
                Text(
                    text = "ABOUT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(0.6f),
                    letterSpacing = 2.sp
                )
            }

            item {
                GlassCard {
                    Column {
                        Text(
                            text = "ATLAS OFFLINE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Minimalist Fitness Tracker",
                            fontSize = 13.sp,
                            color = Color.White.copy(0.7f)
                        )

                        Text(
                            text = "Beta Testing Version",
                            fontSize = 12.sp,
                            color = Color.White.copy(0.5f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Built by Matic Muha using Jetpack Compose",
                            fontSize = 11.sp,
                            color = Color.White.copy(0.4f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    if (showResetDialog) {
        ResetDataDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = {
                viewModel.resetAllData()
                showResetDialog = false
            }
        )
    }
}

@Composable
fun MoreMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(0.6f)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.White.copy(0.4f)
            )
        }
    }
}

@Composable
fun ResetDataDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var confirmText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AtlasColors.BackgroundLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color.Red.copy(0.5f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = "DANGER ZONE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "This will permanently delete ALL your data including:",
                    fontSize = 14.sp,
                    color = Color.White
                )

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("• All workout sessions", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Text("• Exercise history", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Text("• Personal records", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Text("• Body measurements", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Text("• Achievements", fontSize = 13.sp, color = Color.White.copy(0.8f))
                    Text("• Templates", fontSize = 13.sp, color = Color.White.copy(0.8f))
                }

                Text(
                    text = "Type 'DELETE' to confirm:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                OutlinedTextField(
                    value = confirmText,
                    onValueChange = { confirmText = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color.Red.copy(0.5f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.Red
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color.White.copy(0.5f))
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = confirmText == "DELETE",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            disabledContainerColor = Color.Red.copy(0.3f)
                        )
                    ) {
                        Text("DELETE ALL", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun exportData(context: Context, viewModel: AtlasViewModel) {
    // TODO: Implement CSV export
    // For now, show a toast
    android.widget.Toast.makeText(
        context,
        "Export feature coming soon!",
        android.widget.Toast.LENGTH_SHORT
    ).show()
}
