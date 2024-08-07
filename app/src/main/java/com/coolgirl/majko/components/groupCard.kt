package com.coolgirl.majko.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.coolgirl.majko.R
import com.coolgirl.majko.commons.Constantas
import com.coolgirl.majko.data.dataUi.GroupData.GroupResponseUi
import com.coolgirl.majko.data.remote.dto.GroupData.GroupResponse
import com.coolgirl.majko.navigation.Screen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupCard(navHostController: NavHostController,
              priorityColor : Color = MaterialTheme.colorScheme.background,
              groupData: GroupResponseUi,
              onLongTap: (String) -> Unit = {},
              onLongTapRelease: (String) -> Unit = {},
              isSelected: Boolean = false){

    val borderColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(start = 10.dp, top = 10.dp, end = 10.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(color = priorityColor)
        .border(3.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
        .combinedClickable(
            onClick = { navHostController.navigate(Screen.GroupEditor.createRoute(groupData.id)) },
            onLongClick = {
                if (isSelected) {
                    onLongTapRelease(groupData.id)
                } else {
                    onLongTap(groupData.id)
                }
            },
        ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {
        Row(
            Modifier
                .padding(start = 15.dp, top = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.27f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            if(!groupData.author.image.isNullOrEmpty()){
                Image(painter = rememberImagePainter(Constantas.BASE_URI + groupData.author.image),
                    contentDescription = "",
                    Modifier
                        .size(25.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop)
            }else{
                Box(
                    Modifier
                        .fillMaxHeight(0.8f)
                        .size(25.dp)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape))
            }

            Spacer(Modifier.width(15.dp))
            Text(text= groupData.title, modifier = Modifier.fillMaxWidth(0.7f), fontSize = 14.sp, fontWeight = FontWeight.Medium, softWrap = true, maxLines = 2)

        }
        Row(
            Modifier
                .padding(start = 15.dp, top = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top){
            Text(text= groupData.description, fontSize = 13.sp, fontWeight = FontWeight.Light, softWrap = true, maxLines = 9)
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(end = 15.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End) {
            if (!groupData.isPersonal){
                Image(painter = painterResource(R.drawable.icon_members),
                    contentDescription = "")
                Text(text = groupData.members.size.toString())
            }
        }
    }
}