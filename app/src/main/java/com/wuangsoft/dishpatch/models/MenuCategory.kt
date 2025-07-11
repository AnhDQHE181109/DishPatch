package com.wuangsoft.dishpatch.models


data class MenuCategory(
    val categoryId: String = "",
    val restaurantId: String = "",
    val name: String = "",
    val description: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val items: List<MenuItem> = emptyList(),
)