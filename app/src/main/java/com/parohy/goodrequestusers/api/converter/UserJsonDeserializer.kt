package com.parohy.goodrequestusers.api.converter

import com.google.gson.*
import com.parohy.goodrequestusers.api.model.User
import java.lang.reflect.Type

class UserJsonDeserializer: JsonDeserializer<User> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): User {
        val gson = GsonBuilder().create()
        if (json!!.asJsonObject.has("data")) {
            return json.asJsonObject.get("data").let {
                gson.fromJson(it, User::class.java)
            }
        }
        return gson.fromJson(json, User::class.java)
    }
}