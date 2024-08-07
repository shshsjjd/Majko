package com.coolgirl.majko.Sc

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import com.coolgirl.majko.Screen.GroupEditor.GroupEditorUiState
import com.coolgirl.majko.Screen.GroupEditor.GroupEditorViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.coolgirl.majko.R
import com.coolgirl.majko.components.*
import com.coolgirl.majko.data.dataUi.ProjectData.toUi
import com.coolgirl.majko.data.remote.dto.GroupData.GroupUpdate
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
@Composable
fun GroupEditorScreen(navController: NavHostController, groupId: String){

    val viewModel: GroupEditorViewModel = koinViewModel()

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit){
        coroutineScope.launch {
            viewModel.loadData(groupId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    BackHandler { viewModel.updateExitDialog() }

    if (uiState.exitDialog) {
        ExitAlertDialog(
            onConfirm = { viewModel.updateExitDialog()
                viewModel.saveGroup(navController, GroupUpdate(uiState.groupId,
                    uiState.groupData!!.title, uiState.groupData!!.description)) },
            onDismiss = { viewModel.updateExitDialog()
                navController.popBackStack()})
    }

    SetGroupEditorScreen(uiState, viewModel, navController)
}

@Composable
fun SetGroupEditorScreen(uiState: GroupEditorUiState, viewModel: GroupEditorViewModel, navController: NavHostController){
    if(uiState.isInvite){
        SetInviteWindow(uiState, viewModel::newInvite)
    }

    if(uiState.isMembers){
        SetMembersWindow(uiState, viewModel::showMembers)
    }

    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {

                ButtonBack({viewModel.saveGroup(navController, GroupUpdate(uiState.groupId,
                    uiState.groupData!!.title, uiState.groupData!!.description))})

                Box() {
                    IconButton(onClick = {viewModel.updateExpanded() }) {
                        Icon(painter = painterResource(R.drawable.icon_menu),
                            contentDescription = "", tint = MaterialTheme.colorScheme.background)
                    }
                    DropdownMenu(
                        expanded = uiState.expanded,
                        onDismissRequest = { viewModel.updateExpanded() },
                        modifier = Modifier.fillMaxWidth(0.5f)) {

                        if (!uiState.members.isNullOrEmpty()) {
                            Row(Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.showMembers()
                                        viewModel.updateExpanded()}) {
                                Text(stringResource(R.string.projectedit_showmembers), fontSize = 18.sp,
                                    modifier = Modifier.padding(all = 10.dp))
                            }
                        }
                        Row(Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.removeGroup(navController)
                                    viewModel.updateExpanded()}) {
                            Text(stringResource(R.string.project_delite), fontSize = 18.sp, modifier = Modifier.padding(all = 10.dp))
                        }
                        Row(Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.createInvite()
                                    viewModel.updateExpanded()}) {
                            Text(stringResource(R.string.project_createinvite), fontSize = 18.sp, modifier = Modifier.padding(all = 10.dp))
                        }
                    }

                }
            }
        }
    ) {
        Column(Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(it)) {

            Column(Modifier
                    .fillMaxWidth()) {
                uiState.groupData?.let {
                    BasicTextField(
                        value = it.title,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                        textStyle = TextStyle.Default.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                        onValueChange = { viewModel.updateGroupName(it) },
                        decorationBox = { innerTextField ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (uiState.groupData!!.title.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.group_name),
                                        color = MaterialTheme.colorScheme.surface, fontSize = 20.sp)
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                uiState.groupData?.let {
                    BasicTextField(
                        value = it.description,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxHeight(),
                        textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                        onValueChange = { viewModel.updateGroupDescription(it) },
                        decorationBox = { innerTextField ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                if (uiState.groupData!!.description.isEmpty()) {
                                    Text(text = stringResource(R.string.group_description),
                                        color = MaterialTheme.colorScheme.surface, fontSize = 18.sp)
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            //отображение пректов, добавленных в группу
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp)) {
                if(uiState.groupData!=null){
                    if(!uiState.groupData.projectsGroup.isNullOrEmpty()){
                        val projectData = uiState.groupData.projectsGroup

                        for (item in projectData){
                            ProjectCard(navController, projectData = item, onLongTap = {}, onLongTapRelease =  {}, isSelected = false)
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                BlueRoundedButton({ viewModel.addingProject() }, stringResource(R.string.groupeditor_addproject))
            }

            //добавление проекта в группу
            if(uiState.isAdding){

                LazyRow(Modifier.fillMaxWidth().padding(all = 5.dp)) {

                    if(!uiState.projectData.isNullOrEmpty()){

                        val projectData = uiState.projectData
                        val count = uiState.projectData.size?:0

                        items(count) { rowIndex ->
                            Column(Modifier.width(200.dp)) {

                                ProjectCard(navController, projectData = projectData[rowIndex])
                                Row(Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center){
                                    Button(onClick = { viewModel.saveProject(projectData[rowIndex].id) },
                                        shape = RoundedCornerShape(15.dp),
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .padding(vertical = 10.dp),
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
                                        Text(text = stringResource(R.string.project_add), color = MaterialTheme.colorScheme.background,
                                            fontSize = 18.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetMembersWindow(uiState: GroupEditorUiState, onDismissRequest: () -> Unit){
    if (!uiState.members.isNullOrEmpty()) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.secondary)) {

                Column(Modifier.padding(start = 15.dp)) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(text = stringResource(R.string.projectedit_members), fontWeight = FontWeight.Medium,
                        fontSize = 18.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

                    Column(Modifier
                            .padding(horizontal = 20.dp, vertical = 10.dp)) {
                        uiState.members?.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 5.dp)) {
                                Icon(painter = painterResource(R.drawable.icon_line),
                                    contentDescription = "", tint = MaterialTheme.colorScheme.background)
                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(text = stringResource(R.string.groupeditor_name) + " " + item.user.name)
                                    Text(text = stringResource(R.string.groupeditor_role) + " " + item.role.name)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    else{
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = stringResource(R.string.projectedit_membersempty), color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SetInviteWindow(uiState: GroupEditorUiState, onDismissRequest: () -> Unit){
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.secondary)) {

            WhiteRoundedTextField(uiState.invite, { },
                stringResource(R.string.invite), Modifier.padding(bottom = 20.dp))

            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically){
                BlueRoundedButton(onDismissRequest, stringResource(R.string.projectedit_close),
                    modifier = Modifier.padding(bottom = 15.dp))
            }

        }
    }
}

