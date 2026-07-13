package com.example.taras.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taras.api.apiClass.ApiF1DataInterface
import com.example.taras.api.apiClass.F1ApiObject
import com.example.taras.api.dataclass.championship_Driver.DriverschampionshipDataClassItem
import com.example.taras.api.dataclass.championship_Team.TeamsChampionshipDataClassItem
import com.example.taras.api.dataclass.driverData.DriverDetailDataClassItem
import com.example.taras.ui.theme.TarasTheme
import com.example.taras.viewmodel.F1DataViewModel
import com.example.taras.viewmodel.NewsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val f1ViewModel: F1DataViewModel = F1DataViewModel()
            val newsViewModel: NewsViewModel = NewsViewModel()
            val drivers by f1ViewModel.drivers.collectAsState()
            val teams by f1ViewModel.teams.collectAsState()
            val driverDetails by f1ViewModel.driverdetail.collectAsState()
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
                            driverDetails.find { it.driver_number == driverData.driver_number }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Position: ${driverData.position_current}")
                            Text(text = "Number: ${driverData.driver_number}")
                            Text(text = "Name: ${detail?.full_name ?: "Api Problem"}")
                            Text(text = "Points: ${driverData.points_current}")
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
                            Text(text = "Position: ${teamData.position_current}")
                            Text(text = "Team: ${teamData.team_name}")
                            Text(text = "Points: ${teamData.points_current}")
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
