package com.sdk.qrscannergenerator.ui.screen.history.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sdk.qrscannergenerator.data.local.entity.QREntity
import com.sdk.qrscannergenerator.presentation.event.QREvent
import com.sdk.qrscannergenerator.presentation.viewmodel.QRViewModel
import com.sdk.qrscannergenerator.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryItemCard(
    qr: QREntity,
    viewModel: QRViewModel
) {
    var showActions by remember { mutableStateOf(false) }

    Card(
        onClick = { showActions = !showActions },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Type icon
                    Icon(
                        if (qr.isGenerated) painterResource(R.drawable.ic_qr) else painterResource(R.drawable.ic_scan),
                        contentDescription = null,
                        tint = if (qr.isGenerated)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )

                    // Type badges
                    AssistChip(
                        onClick = { },
                        label = { Text(viewModel.getQRTypeDescription(qr.qrType)) }
                    )

                    AssistChip(
                        onClick = { },
                        label = { Text(viewModel.getContentTypeDescription(qr.type)) }
                    )
                }

                // Date
                Text(
                    text = viewModel.formatDate(qr.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            Text(
                text = qr.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (showActions) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            // Agar showActions true bo'lsa QR bitmapni chiqaramiz
            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))

                qr.imagePath?.let { path ->
                    QRImageFromPath(path)
                }
            }


            // Action buttons (when expanded)
            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy button
                    IconButton(
                        onClick = {
                            viewModel.onEvent(QREvent.CopyToClipboard(qr.content))
                        }
                    ) {
                        Icon(painterResource(R.drawable.ic_copy), "Copy")
                    }

                    // Share button
                    IconButton(
                        onClick = {
                            viewModel.onEvent(QREvent.ShareContent(qr.content))
                        }
                    ) {
                        Icon(Icons.Default.Share, "Share")
                    }

                    // Open URL button (if URL)
                    if (viewModel.isUrl(qr.content)) {
                        IconButton(
                            onClick = {
                                // TODO: Open URL in browser
                            }
                        ) {
                            Icon(painterResource(R.drawable.ic_chrome), "Open")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Delete button
                    IconButton(
                        onClick = {
                            viewModel.onEvent(QREvent.ShowDeleteDialog(qr))
                        }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
