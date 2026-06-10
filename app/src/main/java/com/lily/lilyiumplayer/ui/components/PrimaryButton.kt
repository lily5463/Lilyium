package com.lily.lilyiumplayer.ui.components



import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PrimaryButton(
    label: String,
    loading: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        enabled = !loading,
        colors = ButtonDefaults.buttonColors(
        ),
        onClick = onClick,
        modifier = Modifier.padding(16.dp)
    ) {
        if (!loading) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(12.dp, 6.dp)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.width(24.dp),
                strokeWidth = 2.dp,
            )
        }
    }
}
