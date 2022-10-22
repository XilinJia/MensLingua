/*
 * Created by Xilin Jia on 8/11/20, 8:50 PM
 * https://github.com/XilinJia
 * Last modified 3/11/22, 8:50 PM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet

open class HtmlActivity : BaseActivity() {
    
    var html: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildText()

        conformToBaseNew(false) {
            AndroidView(factory = {
                WebView(this@HtmlActivity).apply {
                    webViewClient = WebViewClient()
                    this.loadData(html, "text/html; charset=UTF-8", "UTF-8")
                }
            })
        }
    }

    protected open fun buildText() {
        title = intent.getStringExtra(ARG_TITLE)!!
        html = intent.getStringExtra(ARG_HTML)!!

        val document: Node = parser.parse(html)
        html = renderer.render(document)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private val options = MutableDataSet()
        @JvmStatic protected val parser = Parser.builder(options).build()
        @JvmStatic protected val renderer = HtmlRenderer.builder(options).build()
        private val TAG = HtmlActivity::class.java.simpleName
        private const val ARG_TITLE = "argtitle"
        private const val ARG_HTML = "arghtml"

        fun start(activity: BaseActivity, title: String?, html: String?) {
            val intent = Intent(activity, HtmlActivity::class.java)
            intent.putExtra(ARG_TITLE, title)
            intent.putExtra(ARG_HTML, html)
            activity.startActivity(intent)
        }
    }
}