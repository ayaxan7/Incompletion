package com.ayaan.incompletion.presentation.favorites.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.data.model.BusStopData
import com.ayaan.incompletion.R

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

    // Use the same bus stop options from BusStopData
    val busStopOptions = BusStopData.busStopOptions

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
                            text = stringResource(R.string.add_favorite_route),
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
                                contentDescription = stringResource(R.string.close),
                                tint = Color(0xFF666666)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Source field
                    Text(
                        text = stringResource(R.string.from_source),
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
                            value = sourceSelection?.let { BusStopData.getStopName(it) } ?: stringResource(R.string.select_source_stop),
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(R.string.dropdown)
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
                            modifier= Modifier.height(300.dp)
                                .background(Color.White)
                        ) {
                            busStopOptions.forEach { busStop ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = busStop.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF333333)
                                            )
                                            Text(
                                                text = busStop.id,
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    },
                                    onClick = {
                                        sourceSelection = busStop.id
                                        sourceDropdownExpanded = false
                                    }
                                )
                                HorizontalDivider(modifier=Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally))
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
                                contentDescription = stringResource(R.string.swap_source_destination),
                                tint = Color(0xFF2196F3)
                            )
                        }
                    }

                    // Destination field
                    Text(
                        text = stringResource(R.string.to_destination),
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
                            value = destinationSelection?.let { BusStopData.getStopName(it) } ?: stringResource(R.string.select_destination_stop),
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(R.string.dropdown)
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
                            onDismissRequest = { destinationDropdownExpanded = false },
                            modifier= Modifier.height(300.dp)
                                .background(Color.White)
                        ) {
                            busStopOptions.forEach { busStop ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = busStop.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF333333)
                                            )
                                            Text(
                                                text = busStop.id,
                                                fontSize = 12.sp,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    },
                                    onClick = {
                                        destinationSelection = busStop.id
                                        destinationDropdownExpanded = false
                                    }
                                )
                                HorizontalDivider(modifier=Modifier.fillMaxWidth(0.95f).align(Alignment.CenterHorizontally))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add button
                    GradientButton(
                        text = stringResource(R.string.add_to_favorites),
                        isLoading = false,
                        enabled = !sourceSelection.isNullOrEmpty() && !destinationSelection.isNullOrEmpty(),
                        onClick = {
                            if (!sourceSelection.isNullOrEmpty() && !destinationSelection.isNullOrEmpty()) {
                                val sourceName = BusStopData.getStopName(sourceSelection!!)
                                val destinationName = BusStopData.getStopName(destinationSelection!!)
                                // Pass: sourceName, sourceId, destinationName, destinationId
                                onAddFavorite(sourceName, sourceSelection!!, destinationName, destinationSelection!!)
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