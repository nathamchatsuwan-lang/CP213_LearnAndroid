package com.example.checkbill

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.checkbill.model.BillItem
import com.example.checkbill.model.Member
import com.example.checkbill.ui.theme.CheckBillTheme

class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val members = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("MEMBERS", Member::class.java) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("MEMBERS") ?: arrayListOf()
        }

        val items = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("ITEMS", BillItem::class.java) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("ITEMS") ?: arrayListOf()
        }

        setContent {
            CheckBillTheme {
                SummaryScreen(members, items)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(members: ArrayList<Member>, items: ArrayList<BillItem>) {
    val context = LocalContext.current
    var scText by remember { mutableStateOf("0") }
    var vatText by remember { mutableStateOf("0") }

    val scPercent = scText.toDoubleOrNull() ?: 0.0
    val vatPercent = vatText.toDoubleOrNull() ?: 0.0

    // Compute base amounts
    val baseAmountPerMember = remember(items) {
        val map = mutableMapOf<String, Double>()
        members.forEach { map[it.id] = 0.0 }
        
        items.forEach { item ->
            if (item.sharedBy.isNotEmpty()) {
                val costPerPerson = item.price / item.sharedBy.size
                item.sharedBy.forEach { memberId ->
                    map[memberId] = (map[memberId] ?: 0.0) + costPerPerson
                }
            }
        }
        map
    }

    val totalBase = items.sumOf { it.price }
    val totalSc = totalBase * (scPercent / 100)
    val totalVat = (totalBase + totalSc) * (vatPercent / 100)
    val grandTotal = totalBase + totalSc + totalVat

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bill Summary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Start Over") },
                icon = { Icon(Icons.Default.Home, contentDescription = "Start Over") },
                onClick = {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    (context as ComponentActivity).finishAffinity()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fees & Taxes", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = scText,
                                onValueChange = { scText = it },
                                label = { Text("Service Charge (%)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = vatText,
                                onValueChange = { vatText = it },
                                label = { Text("VAT (%)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:")
                            Text("฿${String.format("%.2f", totalBase)}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Charge:")
                            Text("฿${String.format("%.2f", totalSc)}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("VAT:")
                            Text("฿${String.format("%.2f", totalVat)}")
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text("฿${String.format("%.2f", grandTotal)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            item {
                Text(
                    "Individual Shares",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(members) { member ->
                val base = baseAmountPerMember[member.id] ?: 0.0
                val sc = base * (scPercent / 100)
                val vat = (base + sc) * (vatPercent / 100)
                val finalShare = base + sc + vat
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = member.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Base: ฿${String.format("%.2f", base)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "฿${String.format("%.2f", finalShare)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
