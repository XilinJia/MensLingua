/*
 * Created by Xilin Jia on 9/11/20, 9:04 PM
 * https://github.com/XilinJia
 * Last modified 3/11/22, 8:25 AM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import ac.mdiq.menslingua.dao.ObjectBox.init
import android.app.Application
import android.content.Context

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        init(this)
    }

    companion object {
        const val TAG = "MensLingua"
        var appContext // TODO: static context results in memory leaks
                : Context? = null
            private set

        @JvmStatic
        val applicationName: String
            get() = appContext!!.applicationInfo.loadLabel(
                appContext!!.packageManager
            ).toString()
    }
}