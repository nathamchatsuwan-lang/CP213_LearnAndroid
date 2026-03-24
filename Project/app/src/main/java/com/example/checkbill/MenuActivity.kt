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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.checkbill.model.BillItem
import com.example.checkbill.model.Member
import com.example.checkbill.ui.theme.CheckBillTheme
import java.util.UUID

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val members = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("MEMBERS", Member::class.java) ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("MEMBERS") ?: arrayListOf()
        }

        setContent {
            CheckBillTheme {
                MenuScreen(members)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(members: ArrayList<Member>) {
    val context = LocalContext.current
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<BillItem>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Menu Items") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (items.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = { Text("Next") },
                    icon = { Icon(Icons.Default.ArrowForward, contentDescription = "Next") },
                    onClick = {
                        val intent = Intent(context, SplitActivity::class.java).apply {
                            putParcelableArrayListExtra("MEMBERS", members)
                            putParcelableArrayListExtra("ITEMS", ArrayList(items))
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it },
                    label = { Text("Price") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val price = itemPrice.toDoubleOrNull()
                        if (itemName.isNotBlank() && price != null && price >= 0) {
                            items.add(
                                BillItem(
                                    id = UUID.randomUUID().toString(),
                                    name = itemName.trim(),
                                    price = price
                                )
                            )
                            itemName = ""
                            itemPrice = ""
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No items added yet.\nAdd foods and drinks!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items) { item ->
                        ItemCard(
                            item = item,
                            onRemove = { items.remove(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(item: BillItem, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "฿${String.format("%.2f", item.price)}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
