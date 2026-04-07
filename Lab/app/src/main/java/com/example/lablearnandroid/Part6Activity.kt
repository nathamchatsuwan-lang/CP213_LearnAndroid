package com.example.lablearnandroid

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lablearnandroid.ui.theme.LabLearnAndroidTheme

// 1. ViewModel สำหรับจัดการ URL State
class WebViewModel : ViewModel() {
    var url by mutableStateOf("https://www.google.com")
        private set

    fun updateUrl(newUrl: String) {
        // ตรวจสอบเบื้องต้นเพื่อให้แน่ใจว่า URL มีโปรโตคอล (http/https)
        val formattedUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
            "https://$newUrl"
        } else {
            newUrl
        }
        url = formattedUrl
    }
}

class Part6Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabLearnAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WebScreen(
    modifier: Modifier = Modifier,
    viewModel: WebViewModel = viewModel()
) {
    var textInput by remember { mutableStateOf(viewModel.url) }

    Column(modifier = modifier.fillMaxSize()) {
        // 2. ส่วนแถบพิมพ์ URL (TextField และ Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Enter URL") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.updateUrl(textInput) }) {
                Text("Go")
            }
        }

        // 3. ใช้ AndroidView เพื่อฝัง WebView (Android View ดั้งเดิม)
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                // สร้าง WebView และตั้งค่าเบื้องต้น
                WebView(context).apply {
                    webViewClient = WebViewClient() // โหลดในหน้าแอป
                    settings.javaScriptEnabled = true // เปิดใช้งาน JS
                }
            },
            update = { webView ->
                // 4. เมื่อ URL ใน ViewModel เปลี่ยน ให้ WebView โหลดหน้าใหม่
                if (webView.url != viewModel.url) {
                    webView.loadUrl(viewModel.url)
                }
            }
        )
    }
}