package com.tenshin.app.data.remote

import com.tenshin.app.data.model.Post
import retrofit2.http.GET

// ══════════════════════════════════════════
//  Retrofit API Interface
// ══════════════════════════════════════════
interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
