package com.ayaan.incompletion.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ayaan.incompletion.ui.theme.*

@Composable
fun GradientExtendedFloatingActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val gradient = Brush.horizontalGradient(
        colors = GradientBlue
    )

    val disabledGradient = Brush.horizontalGradient(
        colors = listOf(DisabledButtonColor, DisabledButtonColor)
    )

    // Outer container with gradient
    Box(
        modifier = modifier
            .background(
                brush = gradient,
                shape = RoundedCornerShape(16.dp)
            )
//            .padding(1.dp) // Optional: to create a nice crisp edge
    ) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            containerColor = GradientLightBlue,
//            contentColor = OnPrimary,
            shape = RoundedCornerShape(16.dp),
            modifier= Modifier.background(
                brush = if (enabled) gradient else disabledGradient,
                shape = RoundedCornerShape(16.dp)
            ),
//            contentColor=GradientLightBlue,
//            containerColor=Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp,
                hoveredElevation = 8.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = OnPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = OnPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }
}