package org.isoron.uhabits.homework

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.kaspersky.components.alluresupport.interceptors.testrun.DumpLogcatTestInterceptor
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.activities.habits.list.ListHabitsActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class HabitFlowsTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withForcedAllureSupport(shouldRecordVideo = false).apply {
        testRunWatcherInterceptors.removeAll { it is DumpLogcatTestInterceptor }
    }
) {
    private val app by lazy { ApplicationProvider.getApplicationContext<HabitsApplication>() }
    private val habitName = "Reading ${UUID.randomUUID().toString().take(8)}"
    private val editedName = "$habitName daily"
    private val initialQuestion = "Did I read today?"
    private var wasFirstRun = false

    @Before
    fun preparePreferences() {
        val preferences = app.component.preferences
        wasFirstRun = preferences.isFirstRun
        preferences.isFirstRun = false
    }

    @After
    fun removeTestData() {
        val habits = app.component.habitList
        habits.filter { it.name == habitName || it.name == editedName }.forEach(habits::remove)
        app.component.preferences.isFirstRun = wasFirstRun
    }

    @Test
    fun createHabitAndOpenDetails() = run {
        step("Создать привычку с названием и вопросом") {
            launchHabitList()
            createHabit()
        }
        step("Открыть карточку из списка и проверить данные") {
            HabitListScreen { habit(habitName).click() }
            HabitDetailsScreen {
                title(habitName).isDisplayed()
                question.hasText(initialQuestion)
            }
        }
    }

    @Test
    fun editHabitFromDetails() = run {
        step("Открыть карточку тестовой привычки") {
            addHabitFixture()
            launchHabitList()
            HabitListScreen { habit(habitName).click() }
        }
        val editedQuestion = "Did I read ten pages today?"
        step("Изменить название и вопрос") {
            HabitDetailsScreen { edit.click() }
            HabitEditorScreen {
                name.hasText(habitName)
                question.hasText(initialQuestion)
                name.replaceText(editedName)
                question.replaceText(editedQuestion)
                save.click()
            }
        }
        step("Проверить новые данные в карточке и списке") {
            HabitDetailsScreen {
                title(editedName).isDisplayed()
                question.hasText(editedQuestion)
            }
            pressBack()
            HabitListScreen {
                habit(editedName).isDisplayed()
                habit(habitName).doesNotExist()
            }
        }
    }

    @Test
    fun deleteHabitOnlyAfterConfirmation() = run {
        step("Выбрать тестовую привычку долгим нажатием") {
            addHabitFixture()
            launchHabitList()
            HabitListScreen { habit(habitName).longClick() }
        }
        step("Отменить удаление и проверить, что привычка осталась") {
            HabitListScreen { openDeleteDialog() }
            DeleteHabitDialog { cancel.click() }
            HabitListScreen { habit(habitName).isDisplayed() }
        }
        step("Подтвердить удаление и проверить, что привычка исчезла") {
            HabitListScreen { openDeleteDialog() }
            DeleteHabitDialog { confirm.click() }
            HabitListScreen { habit(habitName).doesNotExist() }
        }
    }

    private fun launchHabitList() {
        app.startActivity(
            Intent(app, ListHabitsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        UiDevice.getInstance(instrumentation).waitForIdle()
    }

    private fun addHabitFixture() {
        val component = app.component
        val habit = component.modelFactory.buildHabit().apply {
            name = habitName
            question = initialQuestion
        }
        component.habitList.add(habit)
    }

    private fun createHabit() {
        HabitListScreen { addHabit.click() }
        HabitTypeDialog { yesOrNo.click() }
        HabitEditorScreen {
            name.replaceText(habitName)
            question.replaceText(initialQuestion)
            save.click()
        }
        HabitListScreen { habit(habitName).isDisplayed() }
    }
}
