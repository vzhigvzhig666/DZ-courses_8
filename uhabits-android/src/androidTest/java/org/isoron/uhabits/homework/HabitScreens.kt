package org.isoron.uhabits.homework

import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.isoron.uhabits.R

object HabitListScreen : KScreen<HabitListScreen>() {
    override val layoutId = null
    override val viewClass = null

    val addHabit = KButton { withId(R.id.actionCreateHabit) }
    val selectionMenu = KButton {
        withContentDescription(androidx.appcompat.R.string.abc_action_menu_overflow_description)
        withParent { withParent { withId(androidx.appcompat.R.id.action_mode_bar) } }
    }
    val delete = KTextView { withText(R.string.delete) }

    fun habit(name: String) = KTextView { withText(name) }

    fun openDeleteDialog() {
        selectionMenu.click()
        delete.click()
    }
}

object HabitTypeDialog : KScreen<HabitTypeDialog>() {
    override val layoutId = R.layout.select_habit_type
    override val viewClass = null

    val yesOrNo = KTextView { withText(R.string.yes_or_no) }
}

object HabitEditorScreen : KScreen<HabitEditorScreen>() {
    override val layoutId = R.layout.activity_edit_habit
    override val viewClass = null

    val name = KEditText { withId(R.id.nameInput) }
    val question = KEditText { withId(R.id.questionInput) }
    val save = KButton { withId(R.id.buttonSave) }
}

object HabitDetailsScreen : KScreen<HabitDetailsScreen>() {
    override val layoutId = R.layout.show_habit
    override val viewClass = null

    val question = KTextView { withId(R.id.questionLabel) }
    val edit = KButton { withId(R.id.action_edit_habit) }

    fun title(name: String) = KTextView { withText(name) }
}

object DeleteHabitDialog : KScreen<DeleteHabitDialog>() {
    override val layoutId = null
    override val viewClass = null

    val cancel = KButton { withId(android.R.id.button2) }
    val confirm = KButton { withId(android.R.id.button1) }
}
