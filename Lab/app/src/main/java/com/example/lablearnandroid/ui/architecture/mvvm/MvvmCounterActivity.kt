package com.example.lablearnandroid.ui.architecture.mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lablearnandroid.ui.architecture.mvvm.MvvmCounterViewModel

class MvvmCounterActivity : ComponentActivity() {

    private val viewModel: MvvmCounterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MvvmCounterScreen(viewModel)
        }
    }
}

@Composable
fun MvvmCounterScreen(viewModel: MvvmCounterViewModel) {
    val count by viewModel.count.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Count: $count",
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = { viewModel.onIncrementClicked() }) {
            Text(text = "Increment")
        }
    }
}
