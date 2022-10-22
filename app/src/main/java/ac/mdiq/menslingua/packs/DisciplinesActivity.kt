/*
 * Created by Xilin Jia on 9/12/20, 9:05 PM
 * https://github.com/XilinJia
 * Last modified 11/30/21, 8:19 AM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.packs

import ac.mdiq.menslingua.R.*
import ac.mdiq.menslingua.dao.models.Discipline
import ac.mdiq.menslingua.dao.models.Language
import ac.mdiq.menslingua.dao.models.Language.Companion.languages
import ac.mdiq.menslingua.general.BaseActivity
import ac.mdiq.menslingua.general.fgPrimary
import ac.mdiq.menslingua.packs.NewDisciplineDialog.NewDisciplineDialogListener
import ac.mdiq.menslingua.utils.FileUtils
import ac.mdiq.menslingua.utils.OnInputDialogClickListener
import ac.mdiq.menslingua.utils.showInputDialog
import ac.mdiq.menslingua.utils.showNotifyDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DisciplinesActivity : BaseActivity(), NewDisciplineDialogListener {

    lateinit var disciplines: MutableList<Discipline>

    private var languages: MutableMap<String, Language> = HashMap(20)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FileUtils.getExternalPermission(this, applicationContext)

        if (languages().isEmpty()) {
            reloadLanguages()
        }

        conformToBaseNew(true) {
            ComposeUI()
        }

        title = resources.getString(string.disciplines)
    }

    @Composable
    private fun ComposeUI() {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (disciplines.isEmpty()) {
                Text(
                    text = "What do you want to study?",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(dimen.subtitle_size).value.sp,
                    color = fgPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(string.new_discipline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            val newFragment: DialogFragment =
                                NewDisciplineDialog.newInstance(1)
                            newFragment.show(supportFragmentManager, "dialog")
                        },
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                    textAlign = TextAlign.Center,
                    color = fgPrimary,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                LazyColumn {
                    itemsIndexed(disciplines) { _, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            DisciplineActivity.start(
                                                this@DisciplinesActivity, row.discId()
                                            )
                                        },
                                        onLongPress = {
                                            removeDiscipline(row)
                                        }
                                    )
                                },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val knownLanguage = languages[row.knownLangId()]!!
                            val targetLanguage = languages[row.targetLangId()]!!

                            Text(
                                text = knownLanguage.name,
                                fontSize = dimensionResource(dimen.title_size).value.sp,
                                color = fgPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(string.next),
                                fontSize = dimensionResource(dimen.title_size).value.sp,
                                color = fgPrimary,
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                text = targetLanguage.name + " : ",
                                fontSize = dimensionResource(dimen.title_size).value.sp,
                                color = fgPrimary,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                            )
                            Text(
                                text = targetLanguage.formatNativeName("\n"),
                                fontSize = dimensionResource(dimen.title_size).value.sp,
                                color = fgPrimary,
                                fontStyle = FontStyle.Italic,
                            )

                        }
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun initializeDB() {
        var source: InputStream? = null
        var destination: FileOutputStream? = null
        try {
            source = assets.open("data.mdb")
            val dstName =
                filesDir.toString() + File.separator + "objectbox" + File.separator + "objectbox" + File.separator + "data.mdb"
            destination = FileOutputStream(dstName)
            FileUtils.copyFile(source, destination)
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        } finally {
            source?.close()
            destination?.close()
        }
    }

    private fun reloadLanguages() {
        try {
            initializeDB()
        } catch (e: IOException) {
            Log.e(TAG, e.message, e)
        }
        reloadDisciplines()
        showFirstStepsIfNeeded()
    }

    private fun showFirstStepsIfNeeded() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstTimePref = "___first_time___"
        if (!prefs.getBoolean(firstTimePref, false)) {
            val editor = prefs.edit()
            editor.putBoolean(firstTimePref, true)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        barColor = color.discipline
        reloadDisciplines()
    }

    private fun reloadDisciplines() {
        disciplines = Discipline.disciplines().toMutableList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun removeDiscipline(discipline: Discipline) {
        showInputDialog(
            this, getString(string.really_delete_discipline, discipline.discId()),
            object : OnInputDialogClickListener {
                override fun onClick(dialogInterface: DialogInterface?, which: Int, value: String) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        disciplines.remove(discipline)
                        discipline.remove()
                        discipline.packId = ""
                    }
                }
            }
        )
    }

    @Composable
    override fun BarMenuItems() {
        DropdownMenuItem(onClick = {
            showMenu = false
            val newFragment: DialogFragment = NewDisciplineDialog.newInstance(1)
            newFragment.show(supportFragmentManager, "dialog")
        }) {
            Text(stringResource(string.new_discipline))
        }
        DropdownMenuItem(onClick = {
            showMenu = false
            LanguagesActivity.start(this@DisciplinesActivity)
        }) {
            Text(stringResource(string.show_languages))
        }
        DropdownMenuItem(onClick = {
            showMenu = false
            showNotifyDialog(
                this@DisciplinesActivity,
                "",
                getString(string.help_disciplines)
            )
        }) {
            Text(stringResource(string.help))
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        reloadDisciplines()
    }

    companion object {
        private val TAG = DisciplinesActivity::class.java.simpleName

        @JvmStatic
        fun <T : BaseActivity> start(activity: T) {
            val intent = Intent(activity, DisciplinesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            activity.startActivity(intent)
        }
    }

    init {
        for (language in languages()) {
            languages[language.languageId] = language
        }
    }
}