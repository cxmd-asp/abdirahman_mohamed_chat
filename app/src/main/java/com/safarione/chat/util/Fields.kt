package com.safarione.chat.util

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.safarione.chat.R

open class Field<T>(initialValue: T) {

    private val _value = mutableStateOf(initialValue)

    var value: T
        get() = _value.value
        set(value) {
            _value.value = value
            error = null
        }

    var error: Int? by mutableStateOf(null)
}

class PasswordField(initialValue: String = ""): Field<String>(initialValue) {

    var visible by mutableStateOf(false)

    val visualTransformation: VisualTransformation
        get() = if (visible) VisualTransformation.None else PasswordVisualTransformation()

    fun toggleVisibility() {
        visible = !visible
    }
}

@Composable
fun CustomTextField(
    field: Field<String>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    CustomTextField(
        value = field.value,
        error = field.error,
        onValueChange = { field.value = it },
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    error: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = { FieldTrailingIcon(error, trailingIcon) },
        isError = error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: TextFieldValue,
    error: Int?,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = { FieldTrailingIcon(error, trailingIcon) },
        isError = error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@Composable
fun CustomOutlinedTextField(
    field: Field<String>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    CustomOutlinedTextField(
        value = field.value,
        error = field.error,
        onValueChange = { field.value = it },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    error: Int?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = { FieldTrailingIcon(error, trailingIcon) },
        isError = error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: TextFieldValue,
    error: Int?,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = textStyle,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = { FieldTrailingIcon(error, trailingIcon) },
        isError = error != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource
    )
}

@Composable
private fun FieldTrailingIcon(error: Int?, trailingIcon: @Composable (() -> Unit)?) {
    val context = LocalContext.current

    if (error != null) {
        IconButton(onClick = { toast(context, error) }) {
            Icon(Icons.Filled.Error, contentDescription = stringResource(R.string.label_error), tint = Color.Red)
        }
    }
    else {
        trailingIcon?.invoke()
    }
}

@Composable
fun Code(
    code: Field<String>,
    length: Int,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    require(code.value.length <= length) { "The code '$code' can't exceed the length '$length'" }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicTextField(
            modifier = modifier,
            value = TextFieldValue(code.value, selection = TextRange(code.value.length)),
            onValueChange = {
                if (it.text.length <= length && !it.text.contains('\n'))
                    code.value = it.text
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = {
                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 0 until length) {
                        val text = code.value.getOrNull(i)?.toString() ?: ""
                        val selected = i == code.value.length
                        Text(
                            modifier = Modifier
                                .widthIn(min = 40.dp)
                                .border(
                                    if (selected) 2.dp else 1.dp,
                                    when {
                                        code.error != null -> MaterialTheme.colorScheme.error
                                        selected -> MaterialTheme.colorScheme.primary
                                        else -> LocalContentColor.current
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(2.dp),
                            text = text,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center
                        )

                        if (i < length - 1)
                            Spacer(Modifier.width(8.dp))
                    }
                }
            }
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = code.error?.let { stringResource(it) } ?: "",
            color = MaterialTheme.colorScheme.error,
            fontSize = 20.sp
        )
    }
}

data class Check<T>(val field: Field<T>, val errorMessage: Int, val check: (T) -> Boolean) {
    operator fun invoke(): Boolean {
        if (field.error != null)
            return true

        if (!check(field.value)) {
            field.error = errorMessage
            return true
        }

        return false
    }
}

/**
 * Performs all the specified checks.
 * @return true if one of the checks failed otherwise returns false
 */
fun check(context: Context, vararg checks: Check<*>): Boolean {
    //clear all the previous errors
    checks.forEach { it.field.error = null }

    var error = false
    for (check in checks) {
        error = error or check()
    }

    if (error) {
        val singleError = checks
            .map { it.field }
            .distinct()
            .singleOrNull { it.error != null }
            ?.error

        if (singleError != null)
            toast(context, singleError)
    }

    return error
}

/**
 * Performs all the specified checks.
 * @param onFailed called if any one of the checks fails
 */
inline fun check(context: Context, vararg checks: Check<*>, onFailed: () -> Unit) {
    val error = check(context, *checks)
    if (error)
        onFailed()
}