/*
 * Created by Xilin Jia on 9/11/20, 8:51 PM
 * https://github.com/XilinJia
 * Last modified 3/11/22, 8:51 PM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.general

import ac.mdiq.menslingua.R.*
import ac.mdiq.menslingua.dao.models.Course
import ac.mdiq.menslingua.dao.models.Discipline
import ac.mdiq.menslingua.dao.models.Discipline.Companion.disciplines
import ac.mdiq.menslingua.dao.models.Discipline.Companion.getDiscipline
import ac.mdiq.menslingua.dao.models.Discipline.Companion.isCurrent
import ac.mdiq.menslingua.dao.models.Element
import ac.mdiq.menslingua.dao.models.Language.Companion.languagesByLanguageID
import ac.mdiq.menslingua.dao.models.Pack
import ac.mdiq.menslingua.packs.DisciplineActivity
import ac.mdiq.menslingua.services.ServicesActivity
import ac.mdiq.menslingua.services.SettingsActivity
import ac.mdiq.menslingua.utils.ProgressAlert
import ac.mdiq.menslingua.utils.Speech
import android.R.id
import android.annotation.SuppressLint
import android.graphics.Point
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    protected var title by mutableStateOf("")
    protected var barColor by mutableStateOf(color.discipline)
    protected var showMenu by mutableStateOf(false)

    protected lateinit var pb: ProgressAlert

    private lateinit var runtime: Runtime
    private var maxHeapSizeInMB: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pb = ProgressAlert(this)
        if (toneGenerator == null) toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 200)
        if (appDisplay == null) {
            appDisplay = if (VERSION.SDK_INT >= VERSION_CODES.R)
                applicationContext.display
             else
                windowManager.defaultDisplay

            val size = Point()
            appDisplay!!.getRealSize(size)
            screenWidth = size.x
            screenHeight = size.y
        }
        runtime = Runtime.getRuntime()
        maxHeapSizeInMB = runtime.maxMemory() / 1048576L
    }

    open fun setTitleCount(count: Int) {}

    protected fun memoryAvail(): Long {
        return maxHeapSizeInMB - (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
    }

    protected fun getCTBuilder(@ColorRes id: Int): CustomTabsIntent.Builder {
        val ctBuilder = CustomTabsIntent.Builder()
        val params =
            CustomTabColorSchemeParams.Builder() 
                .setToolbarColor(
                    resources.getColor(
                        id,
                        null
                    )
                ) 
                .build()
        ctBuilder.setDefaultColorSchemeParams(params)
        return ctBuilder
    }

    fun refreshDiscipline(disciplineId: String, force: Boolean): Discipline? {
        if (force || discipline == null || !isCurrent(disciplineId)) {
            discipline = getDiscipline(disciplineId)
            if (discipline != null) {
                discipline!!.setCurrent()
                speech = Speech(applicationContext, discipline!!.targetLocale) // better be in onResume()
            }
        } else discipline = Pack.curDiscipline!!
        if (discipline != null) discipline!!.calcPhraseSentenceRatio()
        return discipline
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    @Composable
    protected open fun BarMenuItems() {}
    @Composable
    protected open fun BarActions() {}

    @Composable
    private fun TopbarActions() {
        BarActions()

        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(Icons.Default.MoreVert, "|")
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            BarMenuItems()
        }
    }

    protected fun playAudio(el: Element<*>, flush: Boolean, slower: Boolean, tag: String) {
        if (el.isHasAudio) {
            val dstName = getExternalFilesDir(null).toString() + "/Audios/" + el.stringId + ".3gp"
            speech!!.play(dstName, slower, tag)
        } else speech!!.speech(el.term, flush, slower, tag)
    }

    fun toastCenter(text: CharSequence?) {
        val toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun toastCenter(@StringRes resId: Int) {
        toastCenter(resources.getText(resId))
    }

    fun toastLow(@StringRes resId: Int, @ColorRes color: Int) {
        val toast = Toast.makeText(applicationContext, resId, Toast.LENGTH_SHORT)
        toast.view!!.setBackgroundResource(color)
        val text = toast.view!!.findViewById<TextView>(id.message)
        text.setTextColor(resources.getColor(color, null))
        toast.show()
    }

    @Composable
    fun MyTheme(
        darkTheme: Boolean = true,
        content: @Composable () -> Unit
    ) {
        MaterialTheme(
            colors = if (darkTheme) DarkColorPalette else LightColorPalette,
            content = content
        )
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    protected fun conformToBaseNew(needDrawer: Boolean, darkTheme: Boolean = true, content: @Composable () -> Unit) {
        setContent {
            val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
            val scope = rememberCoroutineScope()
            MyTheme(darkTheme) {
                Scaffold(
                    scaffoldState = scaffoldState,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = title,
                                    textAlign = TextAlign.Start,
                                    fontSize = 18.sp
                                )
                            },
                            actions = { TopbarActions() },
                            navigationIcon = if (needDrawer) {
                                @Composable {
                                    IconButton(onClick = {
                                        scope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    }) {
                                        Icon(Icons.Filled.Menu, "")
                                    }
                                }
                            } else null,
                            backgroundColor = colorResource(id = barColor),
                            contentColor = Color.White
                        )
                    },
                    drawerGesturesEnabled = needDrawer,
                    drawerBackgroundColor = Color(0xFF7C7B6A),
                    drawerContent = {
                        DrawerContent()
                    }
                ) { content() }
            }
        }
    }

    @Composable
    protected fun DrawerContent() {
        Column {
            Row (modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(bottom = 20.dp)
                .background(color = Color(0xFF4C6F22)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 20.dp),
                    painter = painterResource(id = mipmap.ic_launcher),
                    contentDescription = null)
                Text(text = stringResource(string.app_name),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF44336),
                    fontSize = dimensionResource(dimen.large_size).value.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
            Row (modifier = Modifier
                .padding(start = 20.dp, 10.dp)
                .clickable {
                    SettingsActivity.start(this@BaseActivity)
                }) {
                Icon(
                    painter = painterResource(id = drawable.ic_baseline_settings_24),
                    contentDescription = null)
                Text(text = stringResource(string.settings),
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                    )
            }
            Row (modifier = Modifier
                .padding(start = 20.dp, 10.dp)
                .clickable {
                    ServicesActivity.start(this@BaseActivity)
                }) {
                Icon(
                    painter = painterResource(id = drawable.ic_baseline_home_repair_service_24),
                    contentDescription = null
                )
                Text(text = stringResource(string.services),
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
            Row (modifier = Modifier
                .padding(start = 20.dp, 10.dp)
                .clickable {
                    val info = packageManager.getPackageInfo(packageName, 0)
                    HtmlActivity.start(
                        this@BaseActivity, getString(string.about),
                        getString(string.info_contents, info.versionName, info.versionCode.toString())
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = drawable.ic_outline_info_24),
                    contentDescription = null
                )
                Text(text = stringResource(string.about),
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
            Row (modifier = Modifier
                .padding(start = 20.dp, 10.dp)
                .clickable {
                    HelpActivity.start(this@BaseActivity)
                }) {
                Icon(
                    painter = painterResource(id = drawable.ic_outline_help_outline_24),
                    contentDescription = null
                )
                Text(text = stringResource(string.help),
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Divider()
            Text(
                text = "All disciplines",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensionResource(dimen.title_size).value.sp,
                ),
                modifier = Modifier.padding(start = 20.dp, 20.dp)
            )
            val languages = languagesByLanguageID()
            for (d in disciplines()) {
                val languageT = languages[d.targetLangId()]
                if (languageT == null || d.updated == 0L) {
                    continue
                }
                val languageN = languages[d.knownLangId()]
                if (languageN == null || d.updated == 0L) {
                    continue
                }
                Row (modifier = Modifier
                    .padding(start = 20.dp, 10.dp)
                    .clickable {
                        DisciplineActivity.start(this@BaseActivity, d.discId())
                    }) {
                    Icon(
                        painter = painterResource(id = drawable.ic_outline_menu_book_24),
                        contentDescription = null
                    )
                    Text(text = String.format("%s:%s", languageT.name, languageN.name),
                        fontSize = dimensionResource(dimen.title_size).value.sp,
                        color = colorResource(id = color.discipline),
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
        var toneGenerator: ToneGenerator? = null
        @JvmField
        var discipline: Discipline? = null
        @JvmField
        var course: Course? = null
        
        var appDisplay: Display? = null
        @JvmField
        var screenWidth = 0
        var screenHeight = 0

        @JvmField
        @SuppressLint("StaticFieldLeak")
         var speech: Speech? = null

        @JvmStatic
        fun refreshCourse(id: String, force: Boolean): Course? {
            if (force || course == null || Pack.curCourse == null || !course!!.isSame(id)) {
                course = discipline!!.getCourse(id)
                if (course != null) course!!.setCurrent()
            } else course = Pack.curCourse
            return course
        }
    }
}