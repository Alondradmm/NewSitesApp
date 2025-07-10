package com.app.newsites.ui.screen.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.R

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 50.dp, vertical = 16.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo y Título
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.newsites),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Text(
                    text = "NewSites",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )

            }

            // Espaciado
            Spacer(modifier = Modifier.height(20.dp))


            OutlinedTextField(
                value = viewModel.user,
                onValueChange = { viewModel.user = it },
                label = { Text("Usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.alata))),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Red
                )

            )

            OutlinedTextField(
                value = viewModel.phone,
                onValueChange = { viewModel.phone = it },
                label = { Text("Teléfono") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.alata))),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Red
                )

            )

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.alata))),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Red
                )

            )

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.alata))),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.Red
                )

            )

            // Espaciado
            Spacer(modifier = Modifier.height(10.dp))
            // Botón Acceder
            Button(
                onClick = {
                    viewModel.register()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                shape = RectangleShape
            ) {
                Text(
                    text = "Registrar",
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.alata))
                )
            }

            if (viewModel.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            viewModel.errorMessage?.let { msg ->
                AlertDialog(
                    onDismissRequest = { viewModel.errorMessage = null },
                    confirmButton = {
                        Button(onClick = { viewModel.errorMessage = null }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("Error") },
                    text = { Text(msg) }
                )
            }

            if (viewModel.isRegisterSuccessful) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, "Registro realizado. Inicia Sesión", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                    viewModel.isRegisterSuccessful = false
                }
            }

            // Botón Iniciar Sesión
            TextButton (
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia Sesión",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    color = Color.Red
                )
            }

        }
    }

}