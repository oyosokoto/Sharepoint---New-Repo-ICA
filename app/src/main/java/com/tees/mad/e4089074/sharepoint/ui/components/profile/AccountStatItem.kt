package com.tees.mad.e4089074.sharepoint.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tees.mad.e4089074.sharepoint.ui.theme.Black
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep

@Composable
fun AccountStatItem(
    count: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PurpleDeep,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = count,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black
            )
        )

        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Gray
            )
        )
    }
}