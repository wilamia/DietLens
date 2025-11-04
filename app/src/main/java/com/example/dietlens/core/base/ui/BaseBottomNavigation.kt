package com.example.dietlens.core.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.Card
import com.example.dietlens.theme.Chip
import com.example.dietlens.theme.Navigation
import com.example.dietlens.theme.OnPrimary

@Composable
fun BaseBottomNavigation(
    selectedIndex: Int,
    items: List<BottomNavItem>
) {
    val barHeight = 70.dp
    val cornerRadius = barHeight / 2

    Box(modifier = Modifier.fillMaxWidth(),) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(barHeight)
                .shadow(4.dp, RoundedCornerShape(cornerRadius))
                .background(Color.White, RoundedCornerShape(cornerRadius))
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val backgroundColor by animateColorAsState(
                    if (selectedIndex == index) Buttons else Color.Transparent,
                    tween(500)
                )
                val iconColor by animateColorAsState(
                    if (selectedIndex == index) Color.White else Buttons,
                    tween(500)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(backgroundColor, CircleShape)
                        .clip(CircleShape)
                        .clickable { item.onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(item.icon, contentDescription = item.title, tint = iconColor, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
