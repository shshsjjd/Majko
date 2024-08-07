package com.coolgirl.majko.Screen.Profile

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolgirl.majko.data.remote.ApiError
import com.coolgirl.majko.data.remote.ApiExeption
import com.coolgirl.majko.data.remote.ApiSuccess
import com.coolgirl.majko.data.MajkoUserRepository
import com.coolgirl.majko.data.dataStore.UserDataStore
import com.coolgirl.majko.data.remote.dto.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.coolgirl.majko.R
import com.coolgirl.majko.commons.Constantas
import com.coolgirl.majko.data.dataUi.User.toUi
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileViewModel(private val majkoRepository: MajkoUserRepository) : ViewModel(), KoinComponent {
    private val dataStore: UserDataStore by inject()
    private val _uiState = MutableStateFlow(ProfileUiState.default())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun updateUserName(username: String) {
        _uiState.update { it.copy(userName = username) }
    }

    fun updateUserEmail(email: String) {
        _uiState.update { it.copy(userEmail = email) }
    }

    fun updateOldPassword(password: String) {
        _uiState.update { it.copy(oldPassword = password) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun updateNameData() {
        if(!uiState.value.userName.equals(uiState.value.currentUser!!.name)){
            viewModelScope.launch {
                majkoRepository.updateUserName(UserUpdateName(uiState.value.userName))
                    .collect() { response ->
                        when (response) {
                            is ApiSuccess -> {
                                _uiState.update { it.copy(currentUser = response.data) }
                                isMessage(R.string.message_success)
                            }
                            is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                            is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                        }
                    }
            }
        }
    }

    fun isError(message: Int?) {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = message,
                isError = message != null // Устанавливаем isError в true, если message не null
            )
        }
    }

    fun isMessage(message: Int?){
        if(uiState.value.isMessage){
            _uiState.update { it.copy(isMessage = false)}
        }else{
            _uiState.update { it.copy(message = message, isMessage = true )}
        }
    }

    fun updateEmailData() {
        viewModelScope.launch {
            majkoRepository.updateUserEmail(UserUpdateEmail(uiState.value.userName, uiState.value.userEmail)).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        _uiState.update { it.copy(currentUser = response.data) }
                        isMessage(R.string.message_success)
                    }
                    is ApiError -> {
                        if(response.code==422){
                            isError(R.string.error_email_notnew)
                        }else{
                            isError(R.string.error_data)
                        }
                        Log.d("TAG", "error message = " + response.message)
                    }
                    is ApiExeption -> {
                        isError(R.string.error_data)
                        Log.d("TAG", "exeption e = " + response.e)}
                    else -> {}
                }
            }
        }
    }

    fun changePasswordScreen(){
        if(uiState.value.isChangePassword){
            _uiState.update { it.copy(isChangePassword = false)}
        }else{
            _uiState.update { it.copy(isChangePassword = true)}
        }
    }

    fun forgetAccount(){
        viewModelScope.launch {
            dataStore.setAccesToken("")
        }
    }

    fun loadData() {
        viewModelScope.launch {
            majkoRepository.currentUser().collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        _uiState.update { it.copy(
                            currentUser = response.data,
                            userName = response.data.name,
                            userEmail = response.data.email,
                        ) }

                        if(response.data.image.isNotEmpty()){
                            _uiState.update { it.copy(avatar = Constantas.BASE_URI + response.data.image)}
                        }
                }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                    else -> {}
                }
            }
        }
    }

    fun changePassword(){
        viewModelScope.launch {
            majkoRepository.updateUserPassword(UserUpdatePassword(uiState.value.userName,
                uiState.value.newPassword,uiState.value.confirmPassword, uiState.value.oldPassword)).collect() { response ->
                when(response){
                    is ApiSuccess ->{ changePasswordScreen() }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                    else -> {}
                }
            }
        }
    }

    @SuppressLint("Range", "SuspiciousIndentation")
    @Composable
    fun OpenGallery(context: Context = LocalContext.current): ManagedActivityResultLauncher<String, Uri?> {
        var file by remember { mutableStateOf<File?>(null) }
        val coroutineScope = rememberCoroutineScope()
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                 file = getFileFromUri(context, uri)
                coroutineScope.launch {
                    if (file != null) {
                        updateUserImage(UserUpdateImage(uiState.value.userName), file!!)
                    }
                }
            }
        }

        return launcher
    }

    private suspend fun updateUserImage(user: UserUpdateImage, file: File) {
        Log.d("tag", "updateUserImage file")
            majkoRepository.updateUserImage(user, file).collect { response ->
                when (response) {
                    is ApiSuccess -> {
                        loadData()
                    }
                    is ApiError -> {
                        Log.d("TAG", "Error message = ${response.message}")
                    }
                    is ApiExeption -> {
                        Log.d("TAG", "Exception e = ${response.e}")
                    }
                    else -> {}
                }
            }
    }


    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_image")

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("OpenGallery", "Error getting file from URI: ${e.message}", e)
            null
        }
    }
}
