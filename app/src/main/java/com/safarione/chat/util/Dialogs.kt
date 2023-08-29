package com.safarione.chat.util

import android.app.AlertDialog
import android.content.Context
import com.safarione.chat.R

fun showSingleChoiceDialog(
    context: Context,
    title: Int? = null,
    choices: List<Int>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    showSingleChoiceDialog(
        context = context,
        title = title?.let { context.getString(it) },
        choices = choices.map { context.getString(it) },
        selected = selected,
        onSelect = onSelect
    )
}

fun showSingleChoiceDialog(
    context: Context,
    title: String? = null,
    choices: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    AlertDialog.Builder(context)
        .apply {
            if (title != null)
                setTitle(title)
        }
        .setSingleChoiceItems(choices.toTypedArray(), selected) { dialog, which ->
            onSelect(which)
            dialog.dismiss()
        }
        .setNegativeButton(R.string.action_cancel) { dialog, which -> dialog.dismiss() }
        .show()
}
