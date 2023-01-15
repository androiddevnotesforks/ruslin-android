package org.dianqk.ruslin.ui.component

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import org.dianqk.mdrender.MarkdownVisualTransformation
import org.dianqk.ruslin.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val visualTransformation = remember(colorScheme) { MarkdownVisualTransformation(colorScheme) }

    // See BasicTextField

    // Holds the latest internal TextFieldValue state. We need to keep it to have the correct value
    // of the composition.
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    // Holds the latest TextFieldValue that BasicTextField was recomposed with. We couldn't simply
    // pass `TextFieldValue(text = value)` to the CoreTextField because we need to preserve the
    // composition.
    val textFieldValue = textFieldValueState.copy(text = value)

    SideEffect {
        if (textFieldValue.selection != textFieldValueState.selection ||
            textFieldValue.composition != textFieldValueState.composition
        ) {
            textFieldValueState = textFieldValue
        }
    }
    // Last String value that either text field was recomposed with or updated in the onValueChange
    // callback. We keep track of it to prevent calling onValueChange(String) for same String when
    // CoreTextField's onValueChange is called multiple times without recomposition in between.
    var lastTextValue by remember(value) { mutableStateOf(value) }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .fillMaxHeight(),
        value = textFieldValue,
        onValueChange = { newTextFieldValueState ->
            val stringChangedSinceLastInvocation = lastTextValue != newTextFieldValueState.text
            var fixedNewTextFieldValueState = newTextFieldValueState

            if (stringChangedSinceLastInvocation) {
                var text = newTextFieldValueState.text
                val enterNewLineAfterContent =
                    (textFieldValueState.selection.start == textFieldValueState.selection.end)
                            && (newTextFieldValueState.selection.start == newTextFieldValueState.selection.end)
                            && (textFieldValueState.selection.start + 1 == newTextFieldValueState.selection.start)
                            && (newTextFieldValueState.text[newTextFieldValueState.selection.end - 1] == '\n')
                if (enterNewLineAfterContent) {
                    val newLineIndex = newTextFieldValueState.selection.end - 1
                    visualTransformation.matchTag(
                        index = newLineIndex,
                        onMatchUnOrderList = {
                            if (newLineIndex < 1) {
                                return@matchTag
                            }
                            // ignore
                            if (text[newLineIndex - 1] == '\n') {
                                return@matchTag
                            }
                            val builder = StringBuilder(text)
                            val selectOffset: Int
                            // delete
                            if (newLineIndex >= 2 && text[newLineIndex - 1] == ' ' && text[newLineIndex - 2] == '-') {
                                selectOffset = -3
                                builder.delete(newLineIndex - 3, newLineIndex)
                            } else { // insert
                                selectOffset = 2
                                builder.insert(newTextFieldValueState.selection.end, "- ")
                            }
                            text = builder.toString()
                            fixedNewTextFieldValueState = newTextFieldValueState.copy(
                                text = text,
                                selection = TextRange(
                                    start = newTextFieldValueState.selection.start + selectOffset,
                                    newTextFieldValueState.selection.end + selectOffset
                                )
                            )
                        },
                        onMatchOrderList = {
                            if (newLineIndex < 1) {
                                return@matchTag
                            }
                            // ignore
                            if (text[newLineIndex - 1] == '\n') {
                                return@matchTag
                            }
                            val builder = StringBuilder(text)
                            val selectOffset: Int
                            // delete
                            if (newLineIndex >= 2 && text[newLineIndex - 1] == ' ' && text[newLineIndex - 2] == '.') {
                                selectOffset = -4
                                builder.delete(newLineIndex - 4, newLineIndex)
                            } else { // insert
                                selectOffset = 3
                                builder.insert(newTextFieldValueState.selection.end, "1. ")
                            }
                            text = builder.toString()
                            fixedNewTextFieldValueState = newTextFieldValueState.copy(
                                text = text,
                                selection = TextRange(
                                    start = newTextFieldValueState.selection.start + selectOffset,
                                    newTextFieldValueState.selection.end + selectOffset
                                )
                            )
                        }
                    )
                }
            }
            textFieldValueState = fixedNewTextFieldValueState

            lastTextValue = textFieldValueState.text

            if (stringChangedSinceLastInvocation) {
                onValueChange(textFieldValueState.text)
                visualTransformation.invalid()
            }
        },
        placeholder = {
            Text(text = stringResource(id = R.string.content))
        },
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Transparent
        ),
        visualTransformation = visualTransformation
    )
}