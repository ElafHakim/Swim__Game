package entity

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 * Test cases for [Player]
 */
class PlayerTest {

    /**
     * Test if only creating a player with empty name throws an exception
     */
    @Test
    fun testName() {
        assertDoesNotThrow { Player("Spieler") }
        assertFails{ Player("") }
    }

    /**
     * Test if points for a player are calculated correctly
     */
    @Test
    fun testPoints() {
        val player = Player("player")
        player.hand = listOf(Card(CardSuit.CLUBS, CardValue.NINE), Card(CardSuit.HEARTS, CardValue.NINE),
                             Card(CardSuit.SPADES, CardValue.NINE))
        assertEquals(30.5, player.points)
        player.hand = listOf(Card(CardSuit.CLUBS, CardValue.SEVEN), Card(CardSuit.CLUBS, CardValue.QUEEN),
                             Card(CardSuit.CLUBS, CardValue.ACE))
        assertEquals(28.0, player.points)
    }
}