package entity

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * Test cases for [Game]
 */
class GameTest {

    /**
     * Test weather incorrect player lists are caught, but valid list accepted and all properties are initialized
     * correctly
     */
    @Test
    fun testInitialisation() {
        // test constructor values
        assertFails { Game(listOf()) }
        assertFails { Game(listOf(Player("s1"))) }
        assertDoesNotThrow { Game(listOf(Player("s1"), Player("s2"))) }
        assertDoesNotThrow { Game(listOf(Player("s1"), Player("s2"), Player("s3"))) }
        assertDoesNotThrow { Game(
            listOf(Player("s1"), Player("s2"), Player("s3"), Player("s4"))) }
        assertFails { Game(
            listOf(Player("s1"), Player("s2"),Player("s3"), Player("s4"), Player("s5"))) }

        // test properties
        val players = listOf(Player("s1"), Player("s2"), Player("s3"))
        val game = Game(players)
        assertEquals(players.first(), game.currentPlayer)
        assertEquals(0, game.passCount)
        assertEquals(3, game.placedCards.size)
    }
}