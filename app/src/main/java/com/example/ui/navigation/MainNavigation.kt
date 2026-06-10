package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.reviews.ReviewListScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Reviews : Screen("reviews", "Avaliações", Icons.Default.List)
    object ReviewDetail : Screen("review_detail/{reviewId}", "Detalhes", Icons.Default.List) {
        fun createRoute(reviewId: String) = "review_detail/$reviewId"
    }
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Reviews
)

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Reviews.route) {
                ReviewListScreen(
                    onReviewClick = { reviewId ->
                        navController.navigate(Screen.ReviewDetail.createRoute(reviewId))
                    }
                )
            }
            composable(
                route = Screen.ReviewDetail.route,
                deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = "app://review_detail/{reviewId}" })
            ) { navBackStackEntry ->
                val reviewId = navBackStackEntry.arguments?.getString("reviewId") ?: return@composable
                com.example.ui.reviews.detail.ReviewDetailScreen(
                    reviewId = reviewId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
