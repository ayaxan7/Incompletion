package com.ayaan.incompletion.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ayaan.incompletion.ui.theme.*

@Composable
fun ThemedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector? = null,
    painter: Painter? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (Boolean) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    focusedBorderColor: androidx.compose.ui.graphics.Color? = null,
    unfocusedBorderColor: androidx.compose.ui.graphics.Color? = null
) {
    // Apply the same logic as icons for border colors
    val actualFocusedBorderColor = when {
        focusedBorderColor != null && value.isNotEmpty() -> focusedBorderColor
        else -> TextFieldFocusedBorder
    }

    val actualUnfocusedBorderColor = when {
        unfocusedBorderColor != null && value.isNotEmpty() -> unfocusedBorderColor
        else -> TextFieldBorder
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = PlaceholderTextColor
            )
        },
        leadingIcon = if (icon != null || painter != null) {
            {
                when {
                    icon != null -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (value.isNotEmpty()) IconColorActive else IconColor
                        )
                    }
                    painter != null -> {
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            tint = if (value.isNotEmpty()) IconColorRedActive else IconColor
                        )
                    }
                }
            }
        } else null,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = IconColor
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = actualFocusedBorderColor,
            unfocusedBorderColor = actualUnfocusedBorderColor,
            focusedLabelColor = actualFocusedBorderColor,
            unfocusedLabelColor = PlaceholderTextColor,
            cursorColor = actualFocusedBorderColor,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface,
            focusedContainerColor = TextFieldBackground,
            unfocusedContainerColor = TextFieldBackground
        ),
        shape = RoundedCornerShape(12.dp)
    )
}