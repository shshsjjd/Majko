package com.coolgirl.majko.Screen.Task

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolgirl.majko.R
import com.coolgirl.majko.commons.ApiError
import com.coolgirl.majko.commons.ApiExeption
import com.coolgirl.majko.commons.ApiSuccess
import com.coolgirl.majko.data.MajkoRepository
import com.coolgirl.majko.data.dataStore.UserDataStore
import com.coolgirl.majko.data.remote.dto.MessageData
import com.coolgirl.majko.data.remote.dto.TaskData.TaskById
import com.coolgirl.majko.data.remote.dto.TaskData.TaskDataResponse
import com.coolgirl.majko.data.remote.dto.UserUpdateEmail
import com.coolgirl.majko.di.ApiClient
import com.coolgirl.majko.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TaskViewModel(private val dataStore : UserDataStore, private val majkoRepository: MajkoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState:StateFlow<TaskUiState> = _uiState.asStateFlow()

    init{ loadData() }

    fun updateSearchString(newSearchString:String){
        _uiState.update { currentState ->
            val filteredAllTasks = currentState.allTaskList?.filter { task ->
                task.title?.contains(newSearchString, ignoreCase = true) == true ||
                        task.text?.contains(newSearchString, ignoreCase = true) == true
            }

            val filteredFavoritesTasks = currentState.favoritesTaskList?.filter { task ->
                task.title?.contains(newSearchString, ignoreCase = true) == true ||
                        task.text?.contains(newSearchString, ignoreCase = true) == true
            }

            currentState.copy(
                searchString = newSearchString,
                searchAllTaskList = filteredAllTasks,
                searchFavoritesTaskList = filteredFavoritesTasks
            )
        }
    }

    fun getPriority(priorityId: Int): Int{
        return when (priorityId) {
            1 -> R.color.green
            2 -> R.color.orange
            3 -> R.color.red
            else -> R.color.white
        }
    }

    fun getStatus(priorityId: Int): String{
        return when (priorityId) {
            1 -> "Не выбрано"
            2 -> "Обсуждается"
            3 -> "Ожидает"
            4 -> "В процессе"
            5 -> "Завершена"
            else -> "Нет статуса"
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val accessToken = dataStore.getAccessToken().first() ?: ""
            majkoRepository.getAllUserTask("Bearer " + accessToken).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        val notFavorite: MutableList<TaskDataResponse> = mutableListOf()
                        response.data?.forEach { item ->
                            if (!item.is_favorite && item.mainTaskId==null) {
                                notFavorite.add(item)
                            }
                        }
                        _uiState.update { it.copy(allTaskList = notFavorite)}
                        _uiState.update { it.copy(searchAllTaskList = notFavorite)}
                    }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }

            majkoRepository.getAllFavorites("Bearer " + accessToken).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        _uiState.update { it.copy(favoritesTaskList = response.data)}
                        _uiState.update { it.copy(searchFavoritesTaskList =  response.data)}
                    }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun addFavotite(task_id: String){
        viewModelScope.launch {
            val accessToken = dataStore.getAccessToken().first() ?: ""
            majkoRepository.addToFavorite("Bearer " + accessToken,  TaskById(task_id)).collect() { response ->
                when(response){
                    is ApiSuccess ->{ loadData() }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun removeFavotite(task_id: String){
        viewModelScope.launch {
            val accessToken = dataStore.getAccessToken().first() ?: ""
            majkoRepository.removeFavotire("Bearer " + accessToken,  TaskById(task_id)).collect() { response ->
                when(response){
                    is ApiSuccess ->{ loadData() }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }
}
