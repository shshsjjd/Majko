package com.coolgirl.majko.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: Int,
) {
    Row(Modifier.fillMaxWidth(0.7f), verticalAlignment = Alignment.CenterVertically){
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            textStyle = TextStyle.Default.copy(fontSize = 17.sp, color = MaterialTheme.colorScheme.background),
            maxLines = 1,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    innerTextField()
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(placeholder),
                            fontSize = 17.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.width(5.dp))
    }

}