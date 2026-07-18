package com.example.taras.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun naveGridScreen() {

    Scaffold(
        bottomBar = { NavBar()}
    ) {
            innerPadding ->

        Column(){
            Text(text = "grid")
        }
    }

}