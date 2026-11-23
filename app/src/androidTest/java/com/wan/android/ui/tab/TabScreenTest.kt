package com.wan.android.ui.tab

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.wan.android.bean.TabEntity
import org.junit.Rule
import org.junit.Test

class TabScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val tabs = listOf("完整项目", "开发工具").mapIndexed { index, title ->
        TabEntity().apply {
            id = index
            name = title
        }
    }

    @Test
    fun clickingTabChangesPage() {
        setContent()
        composeRule.onNodeWithText("完整项目").assertIsSelected()
        composeRule.onNodeWithText("开发工具").performClick()
        composeRule.onNodeWithText("开发工具").assertIsSelected()
        composeRule.onNodeWithText("内容 1").assertIsDisplayed()
    }

    @Test
    fun swipingPageSelectsCorrespondingTab() {
        setContent()
        composeRule.onRoot().performTouchInput { swipeLeft() }
        composeRule.onNodeWithText("开发工具").assertIsSelected()
        composeRule.onNodeWithText("内容 1").assertIsDisplayed()
    }

    @Test
    fun selectedPageSurvivesStateRestoration() {
        val restorationTester = StateRestorationTester(composeRule)
        restorationTester.setContent {
            TabScreenTheme(Color.Blue) {
                TabScreen(tabs) { tab, selected ->
                    if (selected) Text("内容 ${tab.id}")
                }
            }
        }
        composeRule.onNodeWithText("开发工具").performClick()
        composeRule.waitForIdle()
        restorationTester.emulateSavedInstanceStateRestore()
        composeRule.onNodeWithText("开发工具").assertIsSelected()
        composeRule.onNodeWithText("内容 1").assertIsDisplayed()
    }

    @Test
    fun emptyTabsDoNotCreateAnyPages() {
        composeRule.setContent {
            TabScreenTheme(Color.Blue) {
                TabScreen(emptyList()) { _, _ -> error("Empty tabs must not create a page") }
            }
        }
        composeRule.onRoot().assertIsDisplayed()
    }

    private fun setContent() {
        composeRule.setContent {
            TabScreenTheme(Color.Blue) {
                TabScreen(tabs) { tab, selected ->
                    if (selected) Text("内容 ${tab.id}")
                }
            }
        }
    }
}
