package com.tenshin.app.data.model

import com.google.gson.annotations.SerializedName

enum class ItemCategory(val displayName: String) {
    WARFRAME("Warframes"),
    PRIMARY("Primarias"),
    SECONDARY("Secundarias"),
    MELEE("Cuerpo a Cuerpo"),
    MODS("Mods"),
    RELICS("Reliquias"),
    OTHER("Otros")
}

data class Inventory(
    @SerializedName("items") val items: List<InventoryItem>,
    @SerializedName("last_updated") val lastUpdated: String? = null
)

data class InventoryItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("rank") val rank: Int? = null,
    @SerializedName("is_riven") val isRiven: Boolean = false,
    @SerializedName("avg_price") val avgPrice: Double? = null,
    @SerializedName("category") val category: ItemCategory = ItemCategory.OTHER,
    @SerializedName("riven_stats") val rivenStats: List<RivenStat>? = null,
    @SerializedName("weapon_name") val weaponName: String? = null,
    @SerializedName("mastered") val mastered: Boolean = false // Nuevo campo para MR
)

data class RivenStat(
    @SerializedName("name") val name: String,
    @SerializedName("value") val value: Float,
    @SerializedName("positive") val positive: Boolean
)
