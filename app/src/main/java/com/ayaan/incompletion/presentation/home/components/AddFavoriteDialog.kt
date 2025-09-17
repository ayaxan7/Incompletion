package com.ayaan.incompletion.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ayaan.incompletion.presentation.common.components.GradientButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFavoriteDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAddFavorite: (String, String, String, String) -> Unit
) {
    var sourceSelection by remember { mutableStateOf<String?>(null) }
    var destinationSelection by remember { mutableStateOf<String?>(null) }
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var destinationDropdownExpanded by remember { mutableStateOf(false) }

    // Generate bus stop options (S1 to S25)
    val busStopOptions = (1..25).map { "S$it" }

    fun resetFields() {
        sourceSelection = null
        destinationSelection = null
        sourceDropdownExpanded = false
        destinationDropdownExpanded = false
    }

    if (isVisible) {
        Dialog(onDismissRequest = {
            resetFields()
            onDismiss()
        }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Favorite Route",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        IconButton(onClick = {
                            resetFields()
                            onDismiss()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF666666)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Source field
                    Text(
                        text = "From (Source)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = sourceDropdownExpanded,
                        onExpandedChange = { sourceDropdownExpanded = !sourceDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = sourceSelection ?: "Select source stop",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = sourceDropdownExpanded,
                            onDismissRequest = { sourceDropdownExpanded = false },
                            modifier= Modifier.background(Color.White)
                        ) {
                            busStopOptions.forEach { stopId ->
                                DropdownMenuItem(
                                    text = { Text(stopId) },
                                    onClick = {
                                        sourceSelection = stopId
                                        sourceDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Swap button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                val temp = sourceSelection
                                sourceSelection = destinationSelection
                                destinationSelection = temp
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap Source and Destination",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }

                    // Destination field
                    Text(
                        text = "To (Destination)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = destinationDropdownExpanded,
                        onExpandedChange = { destinationDropdownExpanded = !destinationDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = destinationSelection ?: "Select destination stop",
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFF44336),
                                unfocusedBorderColor = Color(0xFFF44336)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = destinationDropdownExpanded,
                            onDismissRequest = { destinationDropdownExpanded = false }
                        ) {
                            busStopOptions.forEach { stopId ->
                                DropdownMenuItem(
                                    text = { Text(stopId) },
                                    onClick = {
                                        destinationSelection = stopId
                                        destinationDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add button
                    GradientButton(
                        text = "Add to Favorites",
                        isLoading = false,
                        enabled = !sourceSelection.isNullOrEmpty() && !destinationSelection.isNullOrEmpty(),
                        onClick = {
                            if (!sourceSelection.isNullOrEmpty() && !destinationSelection.isNullOrEmpty()) {
                                // Pass: sourceName, sourceId, destinationName, destinationId
                                // For bus stops like S1, S2, etc., the name and ID are the same
                                onAddFavorite(sourceSelection!!, sourceSelection!!, destinationSelection!!, destinationSelection!!)
                                resetFields()
                                onDismiss()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }
        }
    }
}