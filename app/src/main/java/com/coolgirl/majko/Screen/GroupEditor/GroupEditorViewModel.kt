package com.coolgirl.majko.Screen.GroupEditor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.coolgirl.majko.data.dataUi.ProjectData.ProjectDataResponseUi
import com.coolgirl.majko.data.remote.ApiError
import com.coolgirl.majko.data.remote.ApiExeption
import com.coolgirl.majko.data.remote.ApiSuccess
import com.coolgirl.majko.data.remote.dto.GroupData.*
import com.coolgirl.majko.data.remote.dto.ProjectData.*
import com.coolgirl.majko.data.remote.dto.TaskData.SearchTask
import com.coolgirl.majko.data.repository.MajkoGroupRepository
import com.coolgirl.majko.data.repository.MajkoProjectRepository
import com.coolgirl.majko.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupEditorViewModel(private val majkoRepository: MajkoGroupRepository,
                           private val majkoProjectRepository: MajkoProjectRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(GroupEditorUiState.default())
    val uiState: StateFlow<GroupEditorUiState> = _uiState.asStateFlow()

    fun updateGroupName(name: String){
        _uiState.update { currentState ->
            currentState.groupData?.let { currentProjectData ->
                currentState.copy(
                    groupData = currentProjectData.copy(title = name)
                )
            } ?: currentState
        }
    }

    fun updateExitDialog(){
        if(uiState.value.exitDialog){
            _uiState.update { it.copy(exitDialog = false) }
        }
        else{
            _uiState.update { it.copy(exitDialog = true) }
        }

    }

    fun addingProject(){
        if(uiState.value.isAdding){
            _uiState.update { it.copy(isAdding = false) }
        }else{
            getProjectData()
            _uiState.update { it.copy(isAdding = true) }
        }
    }

    fun newInvite(){
        if(uiState.value.isInvite){
            _uiState.update { it.copy(isInvite = false) }
        }else{
            _uiState.update { it.copy(isInvite = true) }
        }
    }

    fun showMembers(){
        if(uiState.value.isMembers){
            _uiState.update { it.copy(isMembers = false) }
        }else{
            _uiState.update { it.copy(isMembers = true) }
        }
    }


    fun updateExpanded(){
        if(uiState.value.expanded){
            _uiState.update { it.copy(expanded = false)}
        }else{
            _uiState.update { it.copy(expanded = true)}
        }
    }


    fun updateGroupDescription(description: String){
        _uiState.update { currentState ->
            currentState.groupData?.let { currentProjectData ->
                currentState.copy(
                    groupData = currentProjectData.copy(description = description)
                )
            } ?: currentState
        }
    }

    fun loadData(groupId: String){
        _uiState.update { it.copy(groupId = groupId) }
        viewModelScope.launch {
            majkoRepository.getGroupById(GroupById(uiState.value.groupId)).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        _uiState.update { it.copy(groupData = response.data) }
                        if(!response.data.members.isNullOrEmpty()){
                            _uiState.update { it.copy(members = response.data.members) }
                        }
                    }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun saveGroup(navHostController: NavHostController, groupUpdate: GroupUpdate){
        navHostController.popBackStack()
        viewModelScope.launch {
            majkoRepository.updateGroup(groupUpdate).collect() { response ->
                when(response){
                    is ApiSuccess ->{  }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun removeGroup(navHostController: NavHostController){
        navHostController.popBackStack()
        viewModelScope.launch {
            majkoRepository.removeGroup(GroupById(uiState.value.groupId)).collect() { response ->
                when(response){
                    is ApiSuccess -> { }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun saveProject(project_id: String){
        viewModelScope.launch {
            majkoRepository.addProjectInGroup(ProjectInGroup(project_id, uiState.value.groupId)).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        addingProject()
                        loadData(uiState.value.groupId) }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    private fun getProjectData(){
        viewModelScope.launch {
            majkoProjectRepository.getPersonalProject(SearchTask()).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        val validData: MutableList<ProjectDataResponseUi> = mutableListOf()
                        response.data?.forEach { item ->
                            if (item.isPersonal && item.isArchive == 0) {
                                validData.add(item)
                            }
                        }
                        _uiState.update { it.copy(projectData = validData) } }
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

    fun createInvite(){
        viewModelScope.launch {
            majkoRepository.createInvitetoGroup(GroupByIdUnderscore(uiState.value.groupId)).collect() { response ->
                when(response){
                    is ApiSuccess ->{
                        _uiState.update { it.copy(invite = response.data.invite) }
                        newInvite()}
                    is ApiError -> { Log.d("TAG", "error message = " + response.message) }
                    is ApiExeption -> { Log.d("TAG", "exeption e = " + response.e) }
                }
            }
        }
    }

}