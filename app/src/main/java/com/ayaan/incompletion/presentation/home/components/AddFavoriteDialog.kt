package com.ayaan.incompletion.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ayaan.incompletion.data.PlaceSuggestion
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.presentation.common.components.ThemedTextField
import com.ayaan.incompletion.presentation.home.getPlaceSuggestions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFavoriteDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAddFavorite: (String, String, String, String) -> Unit
) {
    var sourceText by remember { mutableStateOf("") }
    var destinationText by remember { mutableStateOf("") }
    var sourcePlaceId by remember { mutableStateOf<String?>(null) }
    var destinationPlaceId by remember { mutableStateOf<String?>(null) }
    var suggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var activeField by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val scope = rememberCoroutineScope()

    fun fetchSuggestions(query: String, field: String) {
        activeField = field
        if (query.length > 2) {
            scope.launch {
                delay(300)
                suggestions = getPlaceSuggestions(query, placesClient)
            }
        } else {
            suggestions = emptyList()
        }
    }

    fun resetFields() {
        sourceText = ""
        destinationText = ""
        sourcePlaceId = null
        destinationPlaceId = null
        suggestions = emptyList()
        activeField = null
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
                    ThemedTextField(
                        value = sourceText,
                        onValueChange = {
                            sourceText = it
                            sourcePlaceId = null
                            fetchSuggestions(it, "source")
                        },
                        label = "From (Source)",
                        icon = Icons.Default.LocationOn,
                        focusedBorderColor = Color(0xFF4CAF50), // Green color
                        unfocusedBorderColor = Color(0xFF4CAF50)
                    )

                    // Show suggestions for source field
                    if (suggestions.isNotEmpty() && activeField == "source") {
                        SuggestionsDropdown(
                            suggestions = suggestions,
                            onItemClick = { suggestion ->
                                sourceText = suggestion.primaryText
                                sourcePlaceId = suggestion.placeId
                                suggestions = emptyList()
                                activeField = null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Swap button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                val temp = sourceText
                                sourceText = destinationText
                                destinationText = temp

                                val tempId = sourcePlaceId
                                sourcePlaceId = destinationPlaceId
                                destinationPlaceId = tempId
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
                    ThemedTextField(
                        value = destinationText,
                        onValueChange = {
                            destinationText = it
                            destinationPlaceId = null
                            fetchSuggestions(it, "destination")
                        },
                        label = "To (Destination)",
                        icon = Icons.Default.LocationOn,
                        focusedBorderColor = Color(0xFFF44336), // Red color
                        unfocusedBorderColor = Color(0xFFF44336)
                    )

                    // Show suggestions for destination field
                    if (suggestions.isNotEmpty() && activeField == "destination") {
                        SuggestionsDropdown(
                            suggestions = suggestions,
                            onItemClick = { suggestion ->
                                destinationText = suggestion.primaryText
                                destinationPlaceId = suggestion.placeId
                                suggestions = emptyList()
                                activeField = null
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Add button
                    GradientButton(
                        text = "Add to Favorites",
                        isLoading = false,
                        enabled = !sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty(),
                        onClick = {
                            if (!sourcePlaceId.isNullOrEmpty() && !destinationPlaceId.isNullOrEmpty()) {
                                onAddFavorite(sourceText, sourcePlaceId!!, destinationText, destinationPlaceId!!)
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
