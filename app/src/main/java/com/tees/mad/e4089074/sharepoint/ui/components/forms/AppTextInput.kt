package com.tees.mad.e4089074.sharepoint.ui.components.forms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tees.mad.e4089074.sharepoint.ui.theme.ErrorRed
import com.tees.mad.e4089074.sharepoint.ui.theme.Gray
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleDeep
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleGrey40
import com.tees.mad.e4089074.sharepoint.ui.theme.PurpleSoft
import com.tees.mad.e4089074.sharepoint.ui.theme.White

@Composable
fun AppTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    error: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    onTrailingIconClick: () -> Unit = {},
    fieldVisibility: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        label = { Text(label) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = "$label Icon",
                    tint = if (error) ErrorRed else PurpleDeep
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                IconButton(onClick = { onTrailingIconClick() }) {
                    Icon(
                        imageVector = it,
                        contentDescription = "$label Trailing Icon",
                        tint = if (error) ErrorRed else PurpleDeep
                    )
                }
            }
        },
        isError = error,
        supportingText = if (error && !errorMessage.isNullOrEmpty()) {
            { Text(text = errorMessage, color = ErrorRed) }
        } else null,
        shape = RoundedCornerShape(5.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (error) ErrorRed else PurpleDeep,
            unfocusedBorderColor = if (error) ErrorRed else PurpleGrey40.copy(alpha = 0.5f),
            focusedLabelColor = if (error) ErrorRed else PurpleSoft,
            unfocusedLabelColor = if (error) ErrorRed else Gray,
            cursorColor = if (error) ErrorRed else PurpleSoft,
            focusedContainerColor = White.copy(alpha = 0.8f),
            unfocusedContainerColor = White.copy(alpha = 0.6f),
            errorBorderColor = ErrorRed,
            errorLabelColor = ErrorRed,
            errorCursorColor = ErrorRed
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            imeAction = imeAction
        ),
        visualTransformation = if (isPassword && fieldVisibility) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        singleLine = singleLine,
        maxLines = maxLines
    )
}