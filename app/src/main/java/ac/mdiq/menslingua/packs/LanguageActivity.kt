/*
 * Created by Xilin Jia on 9/24/20, 11:02 AM
 * https://github.com/XilinJia
 * Last modified 12/24/21, 11:02 AM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.packs

import ac.mdiq.menslingua.R
import ac.mdiq.menslingua.dao.models.Language
import ac.mdiq.menslingua.dao.models.Language.Companion.getLanguage
import ac.mdiq.menslingua.general.BaseActivity
import ac.mdiq.menslingua.general.fgPrimaryDark
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LanguageActivity : BaseActivity() {

    private var language : Language? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val langId = intent.getStringExtra(ARG_LANGUAGE_ID) ?: return
        language = getLanguage(langId)
        if (language == null) onBackPressed()

        conformToBaseNew(true) {
            ComposeUI()
        }
    }

    @Composable
    private fun ComposeUI() {
        Column(
            modifier = Modifier
                .padding(vertical = 6.dp, horizontal = 8.dp)
        ) {
            Text(
                text = language!!.name + ": " + language!!.formatNativeName("\n"),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.title_size).value.sp,
                color = fgPrimaryDark,
            )
            Row {
                Text(
                    text = language!!.family,
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(R.dimen.title_size).value.sp,
                    color = fgPrimaryDark,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(if (language!!.isRightToLeft) R.string.right_to_left else R.string.left_to_right),
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.articles),
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
            )
            Text(
                text = language!!.articleTokens,
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(R.string.tokens_ignored),
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
            )
            Text(
                text = language!!.tokensIgnored,
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(R.string.phrase_tokens),
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
            )
            Text(
                text = language!!.phraseTokensIgnored,
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(R.string.prefixes),
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
            )
            Text(
                text = language!!.prefixes,
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.normal_text).value.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = stringResource(R.string.alphabets),
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
            )
            Text(
                text = language!!.alphabets,
                textAlign = TextAlign.Start,
                fontSize = dimensionResource(R.dimen.normal_text).value.sp,
            )
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
        private const val ARG_LANGUAGE_ID = "arg_language_id"

        @JvmStatic
        fun <T : BaseActivity> start(activity: T, languageId: String?) {
            val intent = Intent(activity, LanguageActivity::class.java)
            intent.putExtra(ARG_LANGUAGE_ID, languageId)
            activity.startActivity(intent)
        }
    }
}