package components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp



@Composable
fun ButtonLoading (
    text : String,
    isLoading : Boolean,
    onClick : () -> Unit,
    modifier: Modifier,
    shape : Shape = RoundedCornerShape(23.dp),


) {


    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        onClick = { onClick() },
        enabled = !isLoading,
        modifier = modifier,
        shape = shape,
    ) {
        if (isLoading){
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp)
            )
        }else{
            Text(
                text = text,
                //modifier = Modifier.padding(horizontal = 55.dp, vertical = 1.dp)
            )
        }
    }

}

