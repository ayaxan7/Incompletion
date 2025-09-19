package com.ayaan.incompletion.presentation.auth.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ayaan.incompletion.R
import com.ayaan.incompletion.ui.theme.*

@Composable
 fun GenderSelectionCard(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    genderOptions: List<String>,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.gender),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = OnSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                genderOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = (option == selectedGender),
                                onClick = { onGenderSelected(option) },
                                role = Role.RadioButton,
                                enabled = enabled
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedGender),
                            onClick = null,
                            enabled = enabled,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = RadioButtonSelected,
                                unselectedColor = RadioButtonUnselected
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (option == selectedGender) SelectionColor else OnSurface
                        )
                    }
                }
            }
        }
    }
}