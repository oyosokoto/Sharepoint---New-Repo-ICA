package com.tees.mad.e4089074.sharepoint.ui.components.dashboard

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tees.mad.e4089074.sharepoint.R
import com.tees.mad.e4089074.sharepoint.ui.theme.Black
import com.tees.mad.e4089074.sharepoint.ui.theme.ErrorRed
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.Purple0
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleRoyal
import com.tees.mad.e4089074.sharepoint.util.TransactionData

@SuppressLint("DefaultLocale")
@Composable
fun TransactionListItem(transaction: TransactionData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = "Show Balance",
            tint = PurpleRoyal,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Purple0)
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Transaction details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.businessName,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Gray
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${transaction.totalPodders} podders",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "•",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Gray
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = transaction.createdAt,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray
                    )
                )
            }
        }

        // Amount
        Text(
            text = "£${String.format("%.2f", transaction.amount)}",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ErrorRed
            )
        )
    }
}
