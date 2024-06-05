package entity

import kotlin.test.*

/**
 * Test cases for [DrawPile]
 */
class DrawPileTest {

    /**
     * Ensure draw pile contains every card exactly ones after initialization
     */
    @Test
    fun testContainsCorrectCards() {
        val drawPile = DrawPile()
        assertEquals(32, drawPile.remainingCards.size)
        for (suit in CardSuit.values()){
            for (value in CardValue.shortDeck()) {
                assertTrue { drawPile.remainingCards.contains(Card(suit, value)) }
            }
        }
    }

    /**
     * Test weather size gets calculated correctly
     */
    @Test
    fun testSize() {
        val drawPile = DrawPile()
        assertEquals(drawPile.remainingCards.size, drawPile.size)
    }

    /**
     * Test weather draw one removes the top card and drawing from empty stack throws an exception
     */
    @Test
    fun testDrawOne(){
        val drawPile = DrawPile()

        // test weather size actually decreased
        assertEquals(32, drawPile.remainingCards.size)
        val topCard = drawPile.drawOne()
        assertEquals(31, drawPile.remainingCards.size)

        // test weather other card object is on top
        assertNotEquals(topCard.hashCode(), drawPile.drawOne().hashCode())

        //test weather drawing from empty pile throws an exception
        while (drawPile.size > 0) {
            drawPile.drawOne()
        }
        assertFails { drawPile.drawOne() }
    }

    /**
     * Test weather draw three removes the top three cards and drawing from stack with less than three cards throws
     * an exception
     */
    @Test
    fun testDrawThree() {
        val drawPile = DrawPile()

        //test weather the method actually removes three cards
        assertEquals(32, drawPile.remainingCards.size)
        val topCards = drawPile.drawThree()
        assertEquals(29, drawPile.remainingCards.size)

        // test weather other card object is on top
        assertTrue { !topCards.contains(drawPile.drawOne()) }

        //test weather drawing from pile with less than three cards throws an exception
        while (drawPile.size > 0) {
            drawPile.drawOne()
            if(drawPile.size < 2) {
                assertFails{ drawPile.drawThree() }
            }
        }
    }
}