package com.safarione.chat.ui.startmuc

import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.safarione.chat.ui.base.BaseActivity
import com.safarione.chat.util.NavigationActivity

class StartMucActivity : BaseActivity() {

    private val viewModel: StartMucViewModel by viewModels()

    @Composable
    override fun Content() {
        NavigationActivity(
            viewModel = viewModel,
            screens = listOf(MembersScreen, NameScreen, CreationScreen),
            hasBottomNavigation = false,
            onFinish = { finish() },
            onCancel = { finish() }
        )
    }
}