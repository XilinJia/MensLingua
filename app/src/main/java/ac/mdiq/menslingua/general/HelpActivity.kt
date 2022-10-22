/*
 * Created by Xilin Jia on 8/11/20, 8:50 PM
 * https://github.com/XilinJia
 * Last modified 3/11/22, 8:48 PM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import ac.mdiq.menslingua.R
import android.content.Intent
import com.vladsch.flexmark.util.ast.Node

class HelpActivity : HtmlActivity() {

    override fun buildText() {
        title = getString(R.string.help)
        val document: Node = parser.parse(getString(R.string.help_contents))
        html = renderer.render(document)
    }

    companion object {
        private val TAG = HelpActivity::class.java.simpleName
        fun start(activity: BaseActivity) {
            val intent = Intent(activity, HelpActivity::class.java)
            activity.startActivity(intent)
        }
    }
}