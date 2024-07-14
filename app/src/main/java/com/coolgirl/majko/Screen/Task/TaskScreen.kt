package com.coolgirl.majko.Screen.Task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.coolgirl.majko.commons.TaskCard
import com.coolgirl.majko.navigation.BottomBar
import com.coolgirl.majko.navigation.BottomBarScreens
import com.coolgirl.majko.R
import com.coolgirl.majko.navigation.Screen
import kotlin.math.roundToInt

@Composable
fun TaskScreen(navController: NavHostController) {
    val viewModel: TaskViewModel = viewModel(
        key = "taskViewModel",
        factory = TaskViewModelProvider.getInstance(LocalContext.current)
    )

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier
                    .fillMaxWidth()
                .fillMaxHeight(0.93f)) {
                SetTaskScreen(navController, viewModel)
            }
            BottomBar(navController, listOf(BottomBarScreens.Notifications, BottomBarScreens.Task, BottomBarScreens.Profile))
        }

        Button(onClick = { navController.navigate(Screen.TaskEditor.task_id(0))},
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp,65.dp)
                .size(56.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.blue))) {
            Text(text = "+", color = colorResource(R.color.white),
                fontSize = 34.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SetTaskScreen(navController: NavHostController, viewModel: TaskViewModel) {
    BoxWithConstraints {
        Column(Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top) {
            Row(Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(color = colorResource(R.color.blue))) {
                //строка поиска, бургер и тд и тп
            }

            val allTaskList = viewModel.uiState.collectAsState().value.allTaskList
            if (allTaskList != null) {
                val columnItems: Int = ((allTaskList.size).toFloat() / 2).roundToInt()
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    items(columnItems) { columnIndex ->
                        LazyRow(Modifier.fillMaxWidth()) {
                            val count =
                                if (columnIndex == columnItems - 1 && allTaskList.size % 2 != 0) 1 else 2
                            items(count) { rowIndex ->
                                val currentIndex = columnIndex * 2 + rowIndex
                                TaskCard(
                                    navController,
                                    viewModel.getPriority(allTaskList[currentIndex].priority),
                                    viewModel.getStatus(allTaskList[currentIndex].status),
                                    taskData = allTaskList[currentIndex])
                            }
                        }
                    }
                }
            }
        }
    }
}