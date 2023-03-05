import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

@ExperimentalComposeUiApi
@Composable
fun WinButton(
    text: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: (@Composable () -> Unit)? = null
) {
    val borderColor = remember {
        mutableStateOf(
            Color.Gray.copy(alpha = 0.5f)
        )
    }
    val backgroundColor = remember {
        mutableStateOf(
            Color.LightGray.copy(alpha = 0.35f)
        )
    }
    Button(
        modifier = modifier
            .height(25.dp)
            .onPointerEvent(
                eventType = PointerEventType.Enter,
                onEvent = {
                    if (enabled) {
                        borderColor.value = Color.Blue.copy(alpha = 0.5f)
                        backgroundColor.value = Color.Blue.copy(alpha = 0.15f)
                    }
                }
            )
            .onPointerEvent(
                eventType = PointerEventType.Exit,
                onEvent = {
                    borderColor.value = Color.Gray.copy(alpha = 0.5f)
                    backgroundColor.value = Color.LightGray.copy(alpha = 0.35f)
                }
            ),
        enabled = enabled,
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor.value
        ),
        contentPadding = PaddingValues(3.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        ),
        shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor.value,
            contentColor = Color.Black,
            disabledBackgroundColor = Color.Gray.copy(alpha = 0.45f),
            disabledContentColor = Color.Gray.copy(alpha = 0.7f)
        ),
    ) {
        if (content == null) {
            text?.let {
                Text(text)
            }
        } else {
            content()
        }
    }
}