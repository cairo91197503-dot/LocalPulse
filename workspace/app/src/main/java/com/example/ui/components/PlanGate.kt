package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.domain.model.Plan
import com.example.domain.model.UserPlan

@Composable
fun PlanGate(
    requiredPlan: Plan,
    userPlan: UserPlan,
    onRequiresUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isLocked = requiredPlan == Plan.PRO && userPlan.plan == Plan.FREE

    Box(modifier = modifier) {
        Box(modifier = Modifier.alpha(if (isLocked) 0.5f else 1f)) {
            content()
        }
        
        if (isLocked) {
            // Overlay invisível que intercepta o toque
            Surface(
                color = androidx.compose.ui.graphics.Color.Transparent,
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onRequiresUpgrade() }
            ) {}

            // Icone de cadeado discreto
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Recurso Pro",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}
