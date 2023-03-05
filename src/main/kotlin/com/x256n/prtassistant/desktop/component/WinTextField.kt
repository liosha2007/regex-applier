import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WinTextField(
    text: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = Color.Gray
                )
            )
            .padding(2.dp),
        value = text,
        singleLine = singleLine,
        maxLines = maxLines,
        onValueChange = onValueChange
    )
}