package com.tenshin.app.data.model

import com.google.gson.annotations.SerializedName

// ══════════════════════════════════════════
//  Modelo de datos (Gson) — Ejemplo con JSONPlaceholder
// ══════════════════════════════════════════
data class Post(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id")     val id: Int,
    @SerializedName("title")  val title: String,
    @SerializedName("body")   val body: String
)
