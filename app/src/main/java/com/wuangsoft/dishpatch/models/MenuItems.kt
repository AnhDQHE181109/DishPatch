package com.wuangsoft.dishpatch.models

data class MenuItem(
    val itemId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true,
    val allergens: List<String>? = null,
    val options: List<OptionGroup>? = null // List of OptionGroup objects
)

data class OptionGroup(
    val optionGroupId: String = "",
    val name: String = "",
    val type: String = "", // e.g., "singleSelect", "multiSelect"
    val isRequired: Boolean = false,
    val choices: List<Choice>? = null
)

data class Choice(
    val choiceId: String = "",
    val name: String = "",
    val priceAdjust: Double = 0.0
)
