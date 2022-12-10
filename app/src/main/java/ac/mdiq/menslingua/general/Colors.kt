/*
 * Created by Xilin Jia on 9/11/20, 8:51 PM
 * https://github.com/XilinJia
 * Last modified 3/11/22, 8:51 PM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val bgPrimary = Color(0xFF203010)
val bgPrimary1 = Color(0xFF405015)
val fgPrimary = Color(0xFF8FC185)
val fgPrimaryDark = Color(0xFF609153)
val fgFullBright = Color(0xffffffff)
val fgFull = Color(0xffcccccc)
val fgFullPale = Color(0xff999999)

val LightColorPalette = lightColors(
    surface = Color(0xFFEEEECC),
    background = Color(0xFFCCCCAA)
)

val DarkColorPalette = darkColors(
    primary = fgPrimary,
    primaryVariant = fgPrimaryDark,
    secondary = fgFull,
    secondaryVariant = fgFullPale,
    surface = bgPrimary1,
    background = bgPrimary
)
