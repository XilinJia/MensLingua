/*
 * Created by Xilin Jia onm9/12/20, 9:05 PM
 * https://github.com/XilinJia
 * Last modified 11/30/21, 8:23 AM
 * Copyright (c) 2020.
 * All rights reserved.
 */
package ac.mdiq.menslingua.packs

import ac.mdiq.menslingua.R
import ac.mdiq.menslingua.dao.OB
import ac.mdiq.menslingua.dao.models.Course
import ac.mdiq.menslingua.dao.models.PackSummary
import ac.mdiq.menslingua.dao.models.PackSummary_
import ac.mdiq.menslingua.general.BaseActivity
import ac.mdiq.menslingua.general.fgFull
import ac.mdiq.menslingua.general.fgPrimary
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.objectbox.query.QueryBuilder

class CoursesActivity : BaseActivity() {
    private var courses: List<Course>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reset()

        if (courses == null || courses!!.isEmpty()) {
            toastCenter("No course exists")
            finish()
            return
        }

        conformToBaseNew(false) {
            ComposeUI()
        }

        reset()

        if (courses == null || courses!!.isEmpty()) {
            toastCenter("No course exists")
            finish()
            return
        }
    }

    @Composable
    private fun ComposeUI() {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = discipline!!.targetLanguage!!.nativeName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.title_size).value.sp,
                color = fgPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = discipline!!.nativeLanguage!!.nativeName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                fontSize = dimensionResource(R.dimen.subtitle_size).value.sp,
                textAlign = TextAlign.Center,
                color = fgPrimary,
            )
            SetAdapter()
        }
    }

    @Composable
    private fun SetAdapter() {
        LazyColumn {
            itemsIndexed(courses!!) { _, course ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            CourseActivity.start(this@CoursesActivity, course.courseId())
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val counters = course.counters!!
                    Column {
                        Row {
                            Text(
                                text = course.name(),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 5.dp),
                                fontSize = dimensionResource(R.dimen.title_size).value.sp,
                                color = fgPrimary,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = counters.count.toString(),
                                color = fgPrimary,
                                modifier = Modifier.weight(0.25f)
                            )
                        }
                        Row {
                            Column(
                                modifier = Modifier.padding(horizontal = 5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_list_alt_24),
                                    tint = fgFull,
                                    contentDescription = null // decorative element
                                )
                                Text(
                                    text = counters.todoCount.toString(),
                                    color = fgFull,
                                    fontSize = dimensionResource(R.dimen.smaller_text).value.sp,
                                )
                            }
                            Column(
                                modifier = Modifier.padding(horizontal = 5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_thumb_down_24),
                                    tint = colorResource(R.color.hard),
                                    contentDescription = null // decorative element
                                )
                                Text(
                                    text = counters.hardCount.toString(),
                                    color = colorResource(R.color.hard),
                                    fontSize = dimensionResource(R.dimen.smaller_text).value.sp,
                                )
                            }
                            Column(
                                modifier = Modifier.padding(horizontal = 5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_thumb_up_24),
                                    tint = colorResource(R.color.easy),
                                    contentDescription = null // decorative element
                                )
                                Text(
                                    text = counters.easyCount.toString(),
                                    color = colorResource(R.color.easy),
                                    fontSize = dimensionResource(R.dimen.smaller_text).value.sp,
                                )
                            }
                            Column(
                                modifier = Modifier.padding(horizontal = 5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_check_circle_outline_24),
                                    tint = colorResource(R.color.done),
                                    contentDescription = null // decorative element
                                )
                                Text(
                                    text = counters.doneCount.toString(),
                                    color = colorResource(R.color.done),
                                    fontSize = dimensionResource(R.dimen.smaller_text).value.sp,
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    fun reset() {
        if (discipline!!.hasCourses()) {
            courses = discipline!!.courses()
            if (courses!!.isEmpty()) return

            val psList = OB.psBox.query()
                .startsWith(
                    PackSummary_.packId,
                    discipline!!.packId,
                    QueryBuilder.StringOrder.CASE_INSENSITIVE
                )
                .build().find()
            if (psList.isNotEmpty()) {
                val psMap: MutableMap<String?, PackSummary> = HashMap(50)
                for (ps in psList) {
                    psMap[ps.packId] = ps
                }
                for (c in courses!!) {
                    c.counters = psMap[c.packId] ?: PackSummary()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        barColor = R.color.discipline
        title = "Courses"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        courses = null
    }

    companion object {
        private val TAG = CoursesActivity::class.java.simpleName

        @JvmStatic
        fun <T : BaseActivity> start(activity: T) {
            val intent = Intent(activity, CoursesActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity.startActivity(intent)
        }
    }
}