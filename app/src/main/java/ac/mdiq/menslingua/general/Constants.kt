/*
 * Created by Xilin Jia on 9/12/20, 9:05 PM
 * https://github.com/XilinJia
 * Last modified 4/14/21, 12:17 PM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import java.lang.Exception

class Constants {
    companion object {
        /**
         * Make Arabic fonts slightly bigger (because their fonts are tiny and difficult to read for a
         * nonarabic reader).
         */
        //    public static final String[] LANGS_WITH_LARGER_FONTS = new String[]{"ar"};
        fun langsWithLargerFonts(): Array<String> {
            return arrayOf("ar")
        }
    }

    init {
        throw Exception()
    }
}