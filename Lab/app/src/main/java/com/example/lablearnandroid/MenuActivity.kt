package com.example.lablearnandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ตัวอย่างการใช้ Log ระดับต่างๆ (อย่าลืม import android.util.Log)
        Log.v("MyTag", "Verbose: ข้อมูลยิบย่อย (เช่น ค่าแกน XYZ จาก Sensor ทุกๆ มิลลิวินาที)")
        Log.d("MyTag", "Debug: ข้อมูลไว้หาบั๊ก (เช่น ค่า ID ที่ดึงมาจาก Database = 123)")
        Log.i("MyTag", "Info: แจ้งสถานะทั่วไป (เช่น โหลดข้อมูล API สำเร็จแล้ว)")
        Log.w("MyTag", "Warn: เตือนว่าแปลกๆ นะ (เช่น โหลดภาพไม่ขึ้น เลยใช้ภาพ Default แทน)")
        Log.e("MyTag", "Error: พังแล้วจ้า (เช่น catch Exception ได้ หรือ API ร่วง)")

        setContent {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, RPGCardActivity::class.java))
                }) {
                    Text("RPGCardActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, PokedexActivity::class.java))
                }) {
                    Text("PokedexActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, LifeCycleComposeActivity::class.java))
                }) {
                    Text("LifeCycleComposeActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, SharedPreferencesActivity::class.java))
                }) {
                    Text("SharedPreferencesActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, PokemonViewModel::class.java))
                }) {
                    Text("PokemonViewModel")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, GalleryActivity::class.java))
                }) {
                    Text("GalleryActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, SensorActivity::class.java))
                }) {
                    Text("SensorActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part1AnimationActivity::class.java))
                }) {
                    Text("AnimationActivity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part2Activity::class.java))
                }) {
                    Text("Part2Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part3Activity::class.java))
                }) {
                    Text("Part3Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part4Activity::class.java))
                }) {
                    Text("Part4Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part5Activity::class.java))
                }) {
                    Text("Part5Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part6Activity::class.java))
                }) {
                    Text("Part6Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part7Activity::class.java))
                }) {
                    Text("Part7Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, Part8Activity::class.java))
                }) {
                    Text("Part8Activity")
                }
                Button(onClick = {
                    startActivity(Intent(this@MenuActivity, DetailActivity::class.java))
                }) {
                    Text("DetailActivity")
                }
            }
        }
    }
}

// check in 24 feb