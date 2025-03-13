package de.malteans.recipes.presentation.main.components

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.malteans.recipes.app.Route
import de.malteans.recipes.presentation.add.AddScreenRoot
import de.malteans.recipes.presentation.add.EditScreenRoot
import de.malteans.recipes.presentation.details.DetailsScreenRoot
import de.malteans.recipes.presentation.plan.PlanScreen
import de.malteans.recipes.presentation.search.SearchScreenRoot

@Composable
fun NavGraph(
    navController: NavHostController,
    setCurScreen: (Screen) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Route.SearchScreen,
        // Set default transitions to None (individual composables below override)
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        // Plan Screen: slide in/out from left
        composable<Route.PlanScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) }
        ) {
            PlanScreen()
            setCurScreen(Screen.Plan)
        }
        // Search Screen: fade in/out
        composable<Route.SearchScreen>(
            enterTransition = { fadeIn(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            exitTransition = { fadeOut(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popEnterTransition = { fadeIn(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popExitTransition = { fadeOut(animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
        ) {
            SearchScreenRoot(
                onRecipeClick = { recipeId ->
                    navController.navigate(Route.DetailScreen(recipeId))
                }
            )
            setCurScreen(Screen.Search)
        }
        // Add Screen: slide in/out from right
        composable<Route.AddScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
        ) {
            AddScreenRoot(
                onRecipeShow = { recipeId ->
                    navController.navigate(Route.DetailScreen(recipeId))
                }
            )
            setCurScreen(Screen.Add)
        }
        // Detail Screen: slide in/out vertically from bottom
        composable<Route.DetailScreen>(
            enterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popEnterTransition = { slideInVertically(initialOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popExitTransition = { slideOutVertically(targetOffsetY = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
        ) {
            val args = it.toRoute<Route.DetailScreen>()
            DetailsScreenRoot(
                onBack = { navController.navigate(Route.SearchScreen) },
                onEdit = { recipeId ->
                    navController.navigate(Route.EditScreen(recipeId))
                },
                recipeId = args.id,
            )
            setCurScreen(Screen.Other)
        }
        composable<Route.EditScreen>(
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(durationMillis = 300, easing = EaseInOut)) }
        ) {
            val args = it.toRoute<Route.EditScreen>()
            EditScreenRoot(
                recipeId = args.id,
                onFinished = { navController.popBackStack() }
            )
            setCurScreen(Screen.Edit)
        }
    }
}
