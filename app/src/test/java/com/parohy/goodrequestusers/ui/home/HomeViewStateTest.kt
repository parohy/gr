package com.parohy.goodrequestusers.ui.home

import com.parohy.goodrequestusers.api.model.User
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeViewStateTest {
    @Test
    fun `when copyData, and previous state exists, then concat data`() {
        val prevState =
            HomeViewState(data = listOf(User(1, "Joe", "Ananas", "email@gmail.com", "")))
        val result = prevState.copyData(listOf(User(2, "Karl", "Goodman", "karel@velky.cz", "")))

        assertEquals(
            HomeViewState(
                data = listOf(
                    User(1, "Joe", "Ananas", "email@gmail.com", ""),
                    User(2, "Karl", "Goodman", "karel@velky.cz", "")
                )
            ),
            result
        )

        assertNull(result.error)
        assertNull(result.silentError)
        assertFalse(result.loading)
        assertFalse(result.refresh)
    }

    @Test
    fun `when copyData, and silentError given, then copy with silentError`() {
        val prevState =
            HomeViewState(data = listOf(User(1, "Joe", "Ananas", "email@gmail.com", "")))
        val result = prevState.copyData(silentError = RuntimeException("Test"))

        assertThat(result.silentError, CoreMatchers.instanceOf(RuntimeException::class.java))
        assertEquals(result.data, listOf(User(1, "Joe", "Ananas", "email@gmail.com", "")))

        assertFalse(result.loading)
        assertFalse(result.refresh)
        assertNull(result.error)
    }

    @Test
    fun `when copyData, and previous state is null, then create new state with data`() {
        val prevState: HomeViewState? = null
        val result = prevState.copyData(listOf(User(2, "Karl", "Goodman", "karel@velky.cz", "")))

        assertEquals(
            HomeViewState(data = listOf(User(2, "Karl", "Goodman", "karel@velky.cz", ""))),
            result
        )

        assertNull(result.silentError)
        assertNull(result.error)
        assertFalse(result.loading)
        assertFalse(result.refresh)
    }

    @Test
    fun `when copyData, and previous state is null, then create new state with silentError`() {
        val prevState: HomeViewState? = null
        val result = prevState.copyData(silentError = IllegalArgumentException())

        assertThat(
            result.silentError,
            CoreMatchers.instanceOf(IllegalArgumentException::class.java)
        )

        assertTrue(result.data.isEmpty())
        assertFalse(result.loading)
        assertFalse(result.refresh)
        assertNull(result.error)
    }
}