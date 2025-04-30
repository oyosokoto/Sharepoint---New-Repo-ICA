package com.tees.mad.e4089074.sharepoint.ui.components.pods

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyPound
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple20
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple40
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple60
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple80
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleRoyal
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleSoft
import com.tees.mad.e4089074.sharepoint.ui.theme.White
import com.tees.mad.e4089074.sharepoint.util.datamodels.PaymentPod
import com.tees.mad.e4089074.sharepoint.util.datamodels.PodListItem
import com.tees.mad.e4089074.sharepoint.util.enums.SplitType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodCard(
    pod: PodListItem,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onPayNow: () -> Unit,
    modifier: Modifier = Modifier,
    paymentEnabled: Boolean = pod.isActive
) {
    val TAG = "PodCard"
    
    // Animate progress value
    val animatedProgress = animateFloatAsState(
        targetValue = pod.progress,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "Progress Animation"
    )
    
    // Define subtle gradient colors using app's purple theme
    val gradientColors = if (pod.isActive) {
        listOf(
            White,
            Purple0 // Very light purple
        )
    } else {
        listOf(
            White,
            Purple0.copy(alpha = 0.5f) // Even lighter purple
        )
    }
    
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isExpanded) 4.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isExpanded) 2.dp else 1.dp,
            pressedElevation = 4.dp
        ),
        onClick = onExpand
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = gradientColors)
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Status indicator for active pods with Apple-like styling
                if (pod.isActive) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Purple20, // Light purple background
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = PurpleDeep, // Deep purple
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PurpleDeep, // Deep purple
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                
                // Header with business name and expand icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Row(
                        verticalAlignment = CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Business icon in a circle with Apple-like styling
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (pod.isActive)
                                        Purple20 // Light purple
                                    else
                                        Purple0 // Very light purple
                                ),
                            contentAlignment = Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Business,
                                contentDescription = null,
                                tint = if (pod.isActive)
                                    PurpleDeep // Deep purple
                                else
                                    Gray, // Gray
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = pod.businessName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black, // Explicitly set to black for visibility
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.DarkGray, // Dark gray for visibility
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = pod.date,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray, // Dark gray for visibility
                                )
                            }
                        }
                    }

                    // Amount in a highlighted box
                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (pod.isActive)
                                Purple20 // Light purple
                            else
                                Purple0, // Very light purple
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CurrencyPound,
                                    contentDescription = null,
                                    tint = if (pod.isActive)
                                        PurpleDeep // Deep purple
                                    else
                                        Gray, // Gray
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${pod.yourShare}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = if (pod.isActive)
                                        PurpleDeep // Deep purple
                                    else
                                        Gray // Gray
                                )
                            }
                        }

                        // Expand/collapse icon in a circle with Apple-like styling
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    Purple0 // Very light purple
                                ),
                            contentAlignment = Center
                        ) {
                            Icon(
                                imageVector = if (isExpanded) 
                                    Icons.Rounded.KeyboardArrowUp 
                                else 
                                    Icons.Rounded.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = Gray, // Gray
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Progress section
                Spacer(modifier = Modifier.height(20.dp))
                
                // Progress indicator with percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        LinearProgressIndicator(
                            progress = { animatedProgress.value },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (pod.isActive)
                                PurpleDeep // Deep purple
                            else
                                Gray, // Gray
                            trackColor = Purple0, // Very light purple for tracks
                            strokeCap = StrokeCap.Round
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Progress percentage
                    Text(
                        text = "${(pod.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (pod.isActive)
                            PurpleDeep // Deep purple
                        else
                            Gray // Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Participants info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    Row(verticalAlignment = CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Purple0, // Very light purple
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.People,
                                contentDescription = null,
                                tint = if (pod.isActive)
                                    PurpleDeep // Deep purple
                                else
                                    Gray, // Gray
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${pod.joinedCount} Joined",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black // Explicitly set to black for visibility
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (pod.remainingCount > 0)
                            Purple0 // Very light purple
                        else
                            Purple20 // Light purple for "Pod Full"
                    ) {
                        Text(
                            text = if (pod.remainingCount > 0) 
                                "${pod.remainingCount} Remaining" 
                            else 
                                "Pod Full",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (pod.remainingCount > 0)
                                Gray // Gray
                            else
                                PurpleDeep, // Deep purple
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Expanded details section
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column {
                        // Payment details section
                        Spacer(modifier = Modifier.height(20.dp))

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Purple20
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // Payment details header with icon
                        Row(
                            verticalAlignment = CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Purple0,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Payment,
                                    contentDescription = null,
                                    tint = PurpleDeep,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(16.dp)
                                )
                            }
                            
                            Text(
                                text = "Payment Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Purple0,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = CenterVertically
                            ) {
                                Text(
                                    text = "Total Amount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray
                                )
                                
                                Row(verticalAlignment = CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CurrencyPound,
                                        contentDescription = null,
                                        tint = Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${pod.totalAmount}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (pod.splitType.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (pod.splitType) {
                                        SplitType.EQUAL.value -> Purple20.copy(alpha = 0.7f)
                                        SplitType.RANDOM.value -> Purple40.copy(alpha = 0.7f)
                                        SplitType.CUSTOM.value -> Purple60.copy(alpha = 0.7f)
                                        else -> Purple0
                                    }
                                ) {
                                    Text(
                                        text = when (pod.splitType) {
                                            SplitType.EQUAL.value -> "Equal Split"
                                            SplitType.RANDOM.value -> "Random Split"
                                            SplitType.CUSTOM.value -> "Custom Split"
                                            else -> "${pod.splitType} Split"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Purple20, // Light purple
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = CenterVertically
                            ) {
                                Text(
                                    text = "Your Share",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                
                                Row(verticalAlignment = CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CurrencyPound,
                                        contentDescription = null,
                                        tint = PurpleDeep, // Deep purple
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${pod.yourShare}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PurpleDeep // Deep purple
                                    )
                                }
                            }
                        }

                        if (pod.items.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))

                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Purple20
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                verticalAlignment = CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Purple0, // Very light purple
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ShoppingCart,
                                        contentDescription = null,
                                        tint = Gray, // Gray
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(16.dp)
                                    )
                                }
                                
                                Text(
                                    text = "Items",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black // Explicitly set to black for visibility
                                )
                            }

                            // Item list with enhanced styling
                            pod.items.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Purple0, // Very light purple background
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black // Explicitly set to black for visibility
                                            )
                                            Text(
                                                text = "${item.quantity}x @ ${item.price}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray // Dark gray for visibility
                                            )
                                        }
                                        
                                        Row(verticalAlignment = CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.CurrencyPound,
                                                contentDescription = null,
                                                tint = Gray, // Gray
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "${item.subtotal}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black // Explicitly set to black for visibility
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Pay now button with enhanced styling
                        Spacer(modifier = Modifier.height(24.dp))

                        // Pay Now button - Only enabled if payment is allowed and pod is active
                        Button(
                            onClick = {
                                Log.d(TAG, "Pay Now clicked for pod: ${pod.id}")
                                onPayNow()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PurpleDeep, // Deep purple
                                disabledContainerColor = Gray.copy(alpha = 0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp, // Apple buttons typically don't have elevation
                                pressedElevation = 0.dp
                            ),
                            enabled = paymentEnabled || pod.isActive
                        ) {
                            Log.d(TAG, "Pay button enabled: ${paymentEnabled && pod.isActive}")
                            Row(
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CurrencyPound,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = when {
                                        !pod.isActive && !paymentEnabled -> "Pod Closed" // Pod is inactive (closed by admin)
                                        !pod.isActive -> "Already Paid" // Pod is inactive for this user because they paid
                                        else -> "Pay Now" // Default case - active pod that can be paid
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun PodsList(
    activePod: PodListItem?,
    podListItems: List<PodListItem>,
    expandedPodId: String?,
    onExpandPod: (String) -> Unit,
    onPayNow: (String) -> Unit,
    modifier: Modifier = Modifier,
    paymentAllowed: Boolean = true
) {
    val TAG = "PodsList"
    
    // Log the current state
    Log.d(TAG, "Rendering PodsList with activePod: ${activePod?.id}, " +
          "podListItems count: ${podListItems.size}, expandedPodId: $expandedPodId, " +
          "paymentAllowed: $paymentAllowed")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Pod Section
        if (activePod != null) {
            item {
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Purple20, // Light purple
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = PurpleDeep, // Deep purple
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp)
                        )
                    }
                    
                    Text(
                        text = "Active Pod",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PurpleDeep // Deep purple
                    )
                }

                // Display the active pod card
                PodCard(
                    pod = activePod,
                    isExpanded = expandedPodId == activePod.id,
                    onExpand = { 
                        Log.d(TAG, "Active pod expand/collapse clicked: ${activePod.id}")
                        onExpandPod(activePod.id) 
                    },
                    onPayNow = { 
                        Log.d(TAG, "Active pod Pay Now clicked: ${activePod.id}")
                        onPayNow(activePod.id) 
                    },
                    paymentEnabled = paymentAllowed
                )
            }
        }
        
        // Previous Pods Header
        if (podListItems.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Purple0, // Very light purple
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = null,
                            tint = Gray, // Gray
                            modifier = Modifier
                                .padding(8.dp)
                                .size(16.dp)
                        )
                    }
                    
                    Text(
                        text = "Previous Pods",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Gray // Gray
                    )
                }
            }
        }

        items(podListItems.size) { index ->
            val podListItem = podListItems[index]
            
            // Display a previous pod card
            PodCard(
                pod = podListItem,
                isExpanded = expandedPodId == podListItem.id,
                onExpand = { 
                    Log.d(TAG, "Previous pod expand/collapse clicked: ${podListItem.id}")
                    onExpandPod(podListItem.id) 
                },
                onPayNow = { 
                    Log.d(TAG, "Previous pod Pay Now clicked: ${podListItem.id}")
                    onPayNow(podListItem.id) 
                },
                paymentEnabled = paymentAllowed
            )
        }

        // Add bottom space for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
