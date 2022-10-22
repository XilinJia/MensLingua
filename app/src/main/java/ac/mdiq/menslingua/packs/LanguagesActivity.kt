/*
 * Created by Xilin Jia on 9/12/20, 9:05 PM
 * https://github.com/XilinJia
 * Last modified 11/5/21, 10:50 AM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.packs

import ac.mdiq.menslingua.R
import ac.mdiq.menslingua.dao.models.Language
import ac.mdiq.menslingua.dao.models.Language.Companion.languages
import ac.mdiq.menslingua.general.BaseActivity
import ac.mdiq.menslingua.general.fgPrimary
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class LanguagesActivity : BaseActivity() {

    private val languages: List<Language> = languages()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (languages.isEmpty()) onBackPressed()

        conformToBaseNew(true) {
            ComposeUI()
        }
        title = resources.getString(R.string.languages)
    }

    @Composable
    private fun ComposeUI() {
        LazyColumn {
            itemsIndexed(languages) { _, row ->
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(vertical = 6.dp, horizontal = 8.dp)
                        .clickable {
                            LanguageActivity.start(
                                this@LanguagesActivity,
                                row.languageId
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = row.formatName("\n"),
                            fontSize = dimensionResource(R.dimen.title_size).value.sp,
                            color = fgPrimary,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = row.formatNativeName("\n"),
                            fontSize = dimensionResource(R.dimen.title_size).value.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = row.family,
                            fontSize = dimensionResource(R.dimen.title_size).value.sp,
                            color = fgPrimary,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(Modifier.weight(1f))
                        val lr =
                            if (row.isRightToLeft) getString(R.string.right_to_left)
                            else getString(R.string.left_to_right)
                        Text(
                            text = lr,
                            fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        barColor = R.color.language
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        fun <T : BaseActivity> start(activity: T) {
            val intent = Intent(activity, LanguagesActivity::class.java)
            activity.startActivity(intent)
        }
    }
}