package com.example.myapp.ui.screens.licenses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private data class License(val name: String, val version: String, val spdx: String, val url: String)

private val LICENSES = listOf(
    License("Kotlin",                    "2.0.21",  "Apache-2.0", "https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt"),
    License("Jetpack Compose BOM",       "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("Material3 for Compose",     "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("AndroidX Navigation Compose","",       "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("Hilt",                      "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("Hilt Navigation Compose",   "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("WorkManager",               "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("AndroidX Room",             "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("DataStore Preferences",     "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("Google Fonts for Compose",  "",        "Apache-2.0", "https://www.apache.org/licenses/LICENSE-2.0"),
    License("kotlinx.coroutines",        "",        "Apache-2.0", "https://github.com/Kotlin/kotlinx.coroutines/blob/master/LICENSE.txt"),
    // TODO: Add any additional libraries your app depends on.
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open-source licenses") },
                navigationIcon = {
                    IconButton(
                        onClick  = onBack,
                        modifier = Modifier.semantics { role = Role.Button },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val uriHandler = LocalUriHandler.current
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            items(LICENSES) { lic ->
                ListItem(
                    headlineContent   = { Text(lic.name) },
                    supportingContent = if (lic.version.isNotEmpty()) {
                        { Text(lic.version, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    } else null,
                    trailingContent = {
                        Text(
                            text     = lic.spdx,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .semantics { role = Role.Button }
                                .clickable { uriHandler.openUri(lic.url) }
                        )
                    },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
