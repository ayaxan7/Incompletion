package com.ayaan.incompletion.presentation.auth.signin

import android.widget.Toast
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ayaan.incompletion.presentation.common.components.GradientButton
import kotlinx.coroutines.launch
import com.ayaan.incompletion.ui.theme.*
import com.ayaan.incompletion.presentation.common.components.ThemedTextField
import com.ayaan.incompletion.R

@Composable
fun SignInScreen(
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
    language:String="en"
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val signInState by viewModel.signInState
    val scope = rememberCoroutineScope()

    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInState.Success -> {
                onSignInClick()
            }
            is SignInState.Error -> {
                Toast.makeText(context, (signInState as SignInState.Error).message, Toast.LENGTH_SHORT).show()
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
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text(
                    text = stringResource(R.string.welcome_back),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = OnSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.sign_in_subtitle),
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
                // Email Field
                ThemedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = stringResource(R.string.email_address),
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    enabled = signInState !is SignInState.Loading
                )

                // Password Field
                ThemedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = stringResource(R.string.password),
                    icon = Icons.Default.Lock,
                    keyboardType = KeyboardType.Password,
                    isPassword = true,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = it },
                    enabled = signInState !is SignInState.Loading
                )

                // Forgot Password Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onForgotPasswordClick() },
                        enabled = signInState !is SignInState.Loading
                    ) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            color = LinkColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Error Message
                if (signInState is SignInState.Error) {
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
                                text = (signInState as SignInState.Error).message,
                                color = ErrorRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sign In Button
                GradientButton(
                    text = stringResource(R.string.sign_in),
                    isLoading = signInState is SignInState.Loading,
                    enabled = signInState !is SignInState.Loading,
                    onClick = {
                        scope.launch {
                            viewModel.signInWithEmailPassword(email, password)
                        }
                    }
                )

                // Or Sign In Text (Optional)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dont_have_account),
                        color = OnSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = { onSignUpClick() },
                        enabled = signInState !is SignInState.Loading
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up),
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