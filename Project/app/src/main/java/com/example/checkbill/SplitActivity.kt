package com.example.checkbill

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.checkbill.model.BillItem
import com.example.checkbill.model.Member
import com.example.checkbill.ui.theme.CheckBillTheme

class SplitActivity : ComponentActivity() {
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
                SplitScreen(members, items)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SplitScreen(members: ArrayList<Member>, initialItems: ArrayList<BillItem>) {
    val context = LocalContext.current
    // Mutable state list for items allows triggering recomposition when elements are updated
    val items = remember { mutableStateListOf(*initialItems.toTypedArray()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Who ate what?") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Next") },
                icon = { Icon(Icons.Default.ArrowForward, contentDescription = "Next") },
                onClick = {
                    val intent = Intent(context, SummaryActivity::class.java).apply {
                        putParcelableArrayListExtra("MEMBERS", members)
                        putParcelableArrayListExtra("ITEMS", ArrayList(items))
                    }
                    context.startActivity(intent)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(items) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "฿${String.format("%.2f", item.price)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            members.forEach { member ->
                                val isChecked = item.sharedBy.contains(member.id)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        val newSharedBy = item.sharedBy.toMutableList()
                                        if (isChecked) {
                                            newSharedBy.remove(member.id)
                                        } else {
                                            newSharedBy.add(member.id)
                                        }
                                        items[index] = item.copy(sharedBy = newSharedBy)
                                    },
                                    label = { Text(member.name) }
                                )
                            }
                        }
                        
                        if (item.sharedBy.isEmpty()) {
                            Text(
                                text = "Please select at least one person",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
