package com.coolgirl.majko.data.remote.dto.GroupData

import com.google.gson.annotations.SerializedName

data class GroupById(
    @SerializedName("groupId") val groupId: String
)

data class GroupByIdUnderscore(
    @SerializedName("group_id") val groupId: String
)
