package com.coolgirl.majko.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Task : Screen("tasks")

    object Notification : Screen("notification")

    object Profile : Screen("profile")

    object TaskEditor : Screen("task_editor/{task_id}"){
        fun task_id(task_id: Int): String{
            return "task_editor/$task_id"
        }
    }

}