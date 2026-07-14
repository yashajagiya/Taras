package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.viewmodel.DriversViewModel
import com.example.taras.viewmodel.TeamsViewModel
import com.example.taras.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val driversViewModel: DriversViewModel = DriversViewModel()
            val teamsViewModel: TeamsViewModel = TeamsViewModel()
            val newsViewModel: NewsViewModel = NewsViewModel()

            val drivers by driversViewModel.drivers.collectAsState()
            val teams by teamsViewModel.teams.collectAsState()
            val driverDetails by driversViewModel.driverDetails.collectAsState()
            val news by newsViewModel.news.collectAsState()

            TarasTheme {
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp)
            ) {

                if (drivers.isEmpty()) {
                    item {
                        Text(
                            text = "No Details Found",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(drivers) { driverData ->
                        val detail =
                            driverDetails.find { it.driverNumber == driverData.driverNumber }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row() {

                                AsyncImage(model = detail?.headshotUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(70.dp),
                                    contentScale = ContentScale.Fit,
                                    alignment = Alignment.Center
                                    )

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Position: ${driverData.positionCurrent}")
                                    Text(text = "Number: ${driverData.driverNumber}")
                                    Text(text = "Name: ${detail?.fullName ?: "Api Problem"}")
                                    Text(text = "Points: ${driverData.pointsCurrent}")
                                }
                            }
                        }
                    }
                }
                if (teams.isEmpty()) {
                    item {
                        Text(
                            text = "No Details Found",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(teams) { teamData ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Position: ${teamData.positionCurrent}")
                            Text(text = "Team: ${teamData.teamName}")
                            Text(text = "Points: ${teamData.pointsCurrent}")
                        }
                    }
                }

                if (news.isNotEmpty()) {
                    item {
                        Text(
                            text = "Latest News",
                            modifier = Modifier.padding(16.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
                        )
                    }
                    items(news) { newsItem ->
                        NewsCard(item = newsItem)
                    }
                }
            }
        }
    }
}
