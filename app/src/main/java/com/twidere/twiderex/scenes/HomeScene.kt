/*
 *  Twidere X
 *
 *  Copyright (C) 2020 Tlaster <tlaster@outlook.com>
 * 
 *  This file is part of Twidere X.
 * 
 *  Twidere X is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Twidere X is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
 */
package com.twidere.twiderex.scenes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.twidere.twiderex.R
import com.twidere.twiderex.component.foundation.AppBar
import com.twidere.twiderex.component.foundation.IconTabsComponent
import com.twidere.twiderex.component.foundation.TopAppBarElevation
import com.twidere.twiderex.component.status.UserAvatar
import com.twidere.twiderex.extensions.withElevation
import com.twidere.twiderex.model.ui.UiUser
import com.twidere.twiderex.model.ui.UiUser.Companion.toUi
import com.twidere.twiderex.navigation.Route
import com.twidere.twiderex.preferences.AmbientAppearancePreferences
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.scenes.home.HomeNavigationItem
import com.twidere.twiderex.scenes.home.HomeTimelineItem
import com.twidere.twiderex.scenes.home.MeItem
import com.twidere.twiderex.scenes.home.MentionItem
import com.twidere.twiderex.scenes.home.SearchItem
import com.twidere.twiderex.ui.AmbientActiveAccount
import com.twidere.twiderex.ui.AmbientNavController
import com.twidere.twiderex.ui.TwidereXTheme
import com.twidere.twiderex.ui.mediumEmphasisContentContentColor

@Composable
fun HomeScene() {
    val navController = rememberNavController()
    var selectedItem by savedInstanceState { 0 }
    val tabPosition = AmbientAppearancePreferences.current.tapPosition
    val menus = listOf(
        HomeTimelineItem(),
        MentionItem(),
        SearchItem(),
        MeItem(),
    )
    val scaffoldState = rememberScaffoldState()
    TwidereXTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                    if (menus[selectedItem].withAppBar) {
                        AppBar(
                            backgroundColor = MaterialTheme.colors.surface.withElevation(),
                            title = {
                                Text(text = menus[selectedItem].name)
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        if (scaffoldState.drawerState.isOpen) {
                                            scaffoldState.drawerState.close()
                                        } else {
                                            scaffoldState.drawerState.open()
                                        }
                                    }
                                ) {
                                    Icon(asset = Icons.Default.Menu)
                                }
                            },
                            elevation = if (menus[selectedItem].withAppBar) {
                                TopAppBarElevation
                            } else {
                                0.dp
                            }
                        )
                    }
                } else {
                    Surface(
                        elevation = if (menus[selectedItem].withAppBar) {
                            TopAppBarElevation
                        } else {
                            0.dp
                        }
                    ) {
                        IconTabsComponent(
                            items = menus.map { it.icon },
                            selectedItem = selectedItem,
                            onItemSelected = {
                                selectedItem = it

                                navController.navigate(menus[selectedItem].route) {
                                    popUpTo(0) {
                                        inclusive = true
                                    }
                                }
                            },
                        )
                    }
                }
            },
            bottomBar = {
                if (tabPosition == AppearancePreferences.TabPosition.Bottom) {
                    HomeBottomNavigation(menus, selectedItem) {
                        selectedItem = it

                        navController.navigate(menus[selectedItem].route) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                }
            },
            drawerContent = {
                HomeDrawer(scaffoldState)
            }
        ) {
            Box(
                modifier = Modifier.padding(
                    start = it.start,
                    bottom = it.bottom,
                    end = it.end,
                    top = it.top,
                )
            ) {
                NavHost(navController = navController, startDestination = menus.first().route) {
                    menus.forEach { item ->
                        composable(item.route) {
                            item.onCompose()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNavigation(
    items: List<HomeNavigationItem>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.background
    ) {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = mediumEmphasisContentContentColor,
                icon = { Icon(item.icon) },
                selected = selectedItem == index,
                onClick = { onItemSelected.invoke(index) }
            )
        }
    }
}
@Composable
private fun HomeDrawer(scaffoldState: ScaffoldState) {

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        val account = AmbientActiveAccount.current
        val user = account?.user?.toUi()
        val navController = AmbientNavController.current
        DrawerUserHeader(user)

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.friendsCount.toString())
                Text(text = "Following")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.followersCount.toString())
                Text(text = "Followers")
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = user?.listedCount.toString())
                Text(text = "Listed")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            for (it in (0 until 10)) {
                item {
                    ListItem(
                        icon = {
                            Icon(asset = vectorResource(id = R.drawable.ic_adjustments_horizontal))
                        },
                        text = {
                            Text(text = "Settings")
                        }
                    )
                }
            }
        }

        Divider()
        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    scaffoldState.drawerState.close {
                        navController.navigate(Route.Settings.Home)
                    }
                }
            ),
            icon = {
                Icon(asset = vectorResource(id = R.drawable.ic_adjustments_horizontal))
            },
            text = {
                Text(text = "Settings")
            }
        )
    }
}

@Composable
private fun DrawerUserHeader(user: UiUser?) {
    ListItem(
        icon = {
            user?.let {
                UserAvatar(
                    user = it,
                )
            }
        },
        text = {
            Text(
                text = user?.name ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryText = {
            Text(
                text = "@${user?.screenName}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailing = {
            IconButton(
                onClick = {
                }
            ) {
                Icon(asset = Icons.Default.ArrowDropDown)
            }
        }
    )
}
