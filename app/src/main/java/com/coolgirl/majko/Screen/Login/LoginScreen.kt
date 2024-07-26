package com.coolgirl.majko.Screen.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coolgirl.majko.R
import com.coolgirl.majko.navigation.Screen
import org.koin.androidx.compose.getViewModel

@Composable
fun LoginScreen(navController: NavController){
    val viewModel = getViewModel<LoginViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    SetLoginScreen(navController, viewModel, uiState)
}

@Composable
fun SetLoginScreen(navController: NavController, viewModel: LoginViewModel, uiState: LoginUiState){
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.Top){
        Column(
            Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center) {
            Row(){
                Spacer(modifier = Modifier.width(60.dp))
                Text(text = stringResource(R.string.app_name), fontSize = 35.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(){
                Spacer(modifier = Modifier.width(25.dp))
                Text(text = stringResource(R.string.login_welcomtext), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.height(1.dp))
            Column(
                Modifier
                    .fillMaxHeight(0.55f)
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 60.dp)
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(30.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                Column(modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly){

                    TextField(
                        value = uiState.userLogin,
                        modifier = Modifier.height(55.dp),
                        placeholder = { Text(text = stringResource(id = R.string.login_login), color = MaterialTheme.colors.onSurface) },
                        onValueChange = { viewModel.updateUserLogin(it) },
                        colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colors.background,
                            unfocusedContainerColor = MaterialTheme.colors.background)
                    )
                    TextField(
                        value = uiState.userPassword,
                        modifier = Modifier.height(55.dp),
                        placeholder = { Text(text = stringResource(id = R.string.login_password), color = MaterialTheme.colors.onSurface) },
                        onValueChange = { viewModel.updateUserPassword(it) },
                        colors = TextFieldDefaults.colors(focusedContainerColor = colorResource(R.color.white),
                            unfocusedContainerColor = MaterialTheme.colors.background)
                    )
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom) {

        Button(onClick = { viewModel.signIn(navController) },
            modifier = Modifier
                .fillMaxWidth(0.73f),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary)) {
            Text(text = stringResource(R.string.login_enter), color = MaterialTheme.colors.background,
                fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = stringResource(R.string.login_registrationoffer),
            modifier = Modifier.clickable { navController.navigate(Screen.Register.route) })
    }
}
