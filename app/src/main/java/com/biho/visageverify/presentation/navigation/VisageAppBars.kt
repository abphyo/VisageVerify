package com.biho.visageverify.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.biho.visageverify.R
import com.biho.visageverify.presentation.ui.AppBarType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisageAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior?,
    type: AppBarType = AppBarType.CenterAligned,
    text: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    navigationIcon: @Composable () -> Unit,
) {
    when (type) {
        AppBarType.CenterAligned ->
            CenterAlignedTopAppBar(
                title = text,
                actions = actions,
                navigationIcon = navigationIcon,
                modifier = modifier,
                scrollBehavior = scrollBehavior
            )
        // others are not required yet
        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackOnlyTopAppBar(
    isRouteFirstEntry: Boolean,
    onNavigateBack: () -> Unit,
    text: String,
) {
    VisageAppBar(
        scrollBehavior = null,
        text = { Text(text = text) },
        actions = { }
    ) {
        if (!isRouteFirstEntry)
            Icon(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = "navigation_icon",
                modifier = Modifier.clickable {
                    onNavigateBack()
                }
            )
    }
}