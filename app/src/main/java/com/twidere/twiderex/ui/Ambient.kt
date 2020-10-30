package com.twidere.twiderex.ui

import android.view.Window
import androidx.compose.runtime.ambientOf
import androidx.navigation.NavController


val AmbientWindow = ambientOf<Window> { error("No Window") }
val AmbientNavController = ambientOf<NavController> { error("No NavController") }