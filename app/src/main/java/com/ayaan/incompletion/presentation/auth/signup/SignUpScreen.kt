package com.ayaan.incompletion.presentation.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayaan.incompletion.presentation.auth.components.GenderSelectionCard
import com.ayaan.incompletion.presentation.common.components.GradientButton
import com.ayaan.incompletion.presentation.common.components.ThemedTextField
import com.ayaan.incompletion.R
import kotlinx.coroutines.launch
import com.ayaan.incompletion.ui.theme.*

@Composable
fun SignUpScreen(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    viewModel: SignUpViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val signUpState by viewModel.signUpState
    val scope = rememberCoroutineScope()

    val genderOptions = listOf("Female", "Male")
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }

    LaunchedEffect(signUpState) {
        when (signUpState) {
            is SignUpState.Success -> {
                onSignUpClick()
                viewModel.resetState()
            }
            is SignUpState.Error -> {
                // Handle error state
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = OnSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Join us and start your journey",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Form Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Full Name Field
                ThemedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full Name",
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    enabled = signUpState !is SignUpState.Loading
                )

                // Gender Selection
                GenderSelectionCard(
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    genderOptions = genderOptions,
                    enabled = signUpState !is SignUpState.Loading
                )

                // Phone Field
                ThemedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number",
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    enabled = signUpState !is SignUpState.Loading
                )

                // Email Field
                ThemedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email_address),
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    enabled = signUpState !is SignUpState.Loading
                )

                // Password Field
                ThemedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = it },
                    enabled = signUpState !is SignUpState.Loading
                )

                // Confirm Password Field
                ThemedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = confirmPasswordVisible,
                    onPasswordVisibilityChange = { confirmPasswordVisible = it },
                    enabled = signUpState !is SignUpState.Loading
                )

                // Error Message
                if (signUpState is SignUpState.Error) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ErrorRed.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ErrorRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = (signUpState as SignUpState.Error).message,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sign Up Button
                GradientButton(
                    text = "Create Account",
                    isLoading = signUpState is SignUpState.Loading,
                    enabled = signUpState !is SignUpState.Loading,
                    onClick = {
                        scope.launch {
                            viewModel.signUpWithEmailPassword(
                                phone = phone,
                                fullName = fullName,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                gender = selectedGender
                            )
                        }
                    }
                )

                // Sign In Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = { onSignInClick() },
                        enabled = signUpState !is SignUpState.Loading
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in),
                            color = LinkColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }
    }
}
