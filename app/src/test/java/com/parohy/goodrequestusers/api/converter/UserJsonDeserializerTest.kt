package com.parohy.goodrequestusers.api.converter

import com.google.gson.JsonObject
import com.parohy.goodrequestusers.api.model.User
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserJsonDeserializerTest {

    private lateinit var expectedUser: User
    private lateinit var deserializer: UserJsonDeserializer

    @Before
    fun setup() {
        expectedUser = User(
            1,
            "Joe",
            "Ananas",
            "joe.ananas@gmail.com",
            "https://s3.amazonaws.com/uifaces/faces/twitter/follettkyle/128.jpg"
        )
        deserializer = UserJsonDeserializer()
    }

    @Test
    fun `when get single user, deserialize json object`() {
        val json = JsonObject()
            .apply {
                add("data", JsonObject().apply {
                    addProperty("id", 1)
                    addProperty("email", "joe.ananas@gmail.com")
                    addProperty("first_name", "Joe")
                    addProperty("last_name", "Ananas")
                    addProperty(
                        "avatar",
                        "https://s3.amazonaws.com/uifaces/faces/twitter/follettkyle/128.jpg"
                    )
                })
            }
        assertEquals(expectedUser, deserializer.deserialize(json, null, null))
    }

    @Test
    fun `when getUser page, deserialize json object`() {
        val json = JsonObject()
            .apply {
                addProperty("id", 1)
                addProperty("email", "joe.ananas@gmail.com")
                addProperty("first_name", "Joe")
                addProperty("last_name", "Ananas")
                addProperty(
                    "avatar",
                    "https://s3.amazonaws.com/uifaces/faces/twitter/follettkyle/128.jpg"
                )
            }
        assertEquals(expectedUser, deserializer.deserialize(json, null, null))
    }
}