package com.biho.visageverify.presentation.composables

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.biho.visageverify.presentation.screens.LoadingState

@Composable
fun RememberCard(
    rememberButtonEnabled: Boolean,
    onRememberImage: (String) -> Unit,
    loadingState: LoadingState,
    croppedFace: Bitmap?,
    name: TextFieldValue,
    nameTextFieldEmpty: Boolean,
    onNameChanged: (TextFieldValue) -> Unit,
    validateTextField: (String) -> Unit,
    onClearClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .wrapContentHeight()
            .padding(
                horizontal = 48.dp,
                vertical = 48.dp
            ),
        shape = ShapeDefaults.Small,
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .clickable { onClearClick() }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 32.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (croppedFace != null)
                Image(
                    bitmap = croppedFace.asImageBitmap(),
                    contentDescription = "cropped bitmap",
                    modifier = Modifier
                        .width(300.dp)
                        .clip(shape = ShapeDefaults.Small),
                    contentScale = ContentScale.FillWidth
                )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = MaterialTheme.shapes.medium,
                placeholder = {
                    Text(text = "Fill person's name", color = Color.Gray)
                },
                value = name,
                onValueChange = {
                    onNameChanged(it)
                    validateTextField(it.text)
                },
                singleLine = true,
                supportingText = {
                    if (nameTextFieldEmpty)
                        Text(
                            text = "name can't be empty",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth()
                        )
                },
            )
            AnimatedContent(targetState = loadingState, label = "progress button") { state ->
                when (state) {
                    LoadingState.Loading -> CircularProgressIndicator()
                    LoadingState.Idle -> {
                        Button(
                            enabled = rememberButtonEnabled,
                            onClick = { onRememberImage(name.text) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = ShapeDefaults.ExtraLarge
                        ) {
                            Text(text = "Remember")
                        }
                    }
                }
            }
        }
    }
}