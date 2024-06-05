package service

import entity.*
import kotlin.test.*

/**
 * Class that provides tests for [GameService], [MainMenuService] and [ActionService] (at the same time,
 * as their functionality is not easily separable) by basically playing through some sample games.
 * [TestRefreshable] is used to validate correct refreshing behavior even though no GUI
 * is present.
 */
class ServiceTest {

    /**
     * Check if parameters are accurately converted to list.
     */
    @Test
    fun testListConversion() {
        val gs = GameService()
        gs.menuService.startGame("s1", "s2", null, null)
        assertEquals(2, gs.currentGame.players.size)
        assertTrue { gs.currentGame.players.any { it.name == "s1" } }
        assertTrue { gs.currentGame.players.any { it.name == "s2" } }
        gs.menuService.startGame("s1", "s2", "s3", null)
        assertEquals(3, gs.currentGame.players.size)
        assertTrue { gs.currentGame.players.any { it.name == "s1" } }
        assertTrue { gs.currentGame.players.any { it.name == "s2" } }
        assertTrue { gs.currentGame.players.any { it.name == "s3" } }
        gs.menuService.startGame("s1", "s2", "s3", "s4")
        assertEquals(4, gs.currentGame.players.size)
        assertTrue { gs.currentGame.players.any { it.name == "s1" } }
        assertTrue { gs.currentGame.players.any { it.name == "s2" } }
        assertTrue { gs.currentGame.players.any { it.name == "s3" } }
        assertTrue { gs.currentGame.players.any { it.name == "s4" } }
    }

    /**
     * Test if cards are handed out to every player at game start.
     */
    @Test
    fun testCardsHandedOut() {
        val gs = GameService()
        gs.menuService.startGame("s1", "s2", "s3", null)
        for (player in gs.currentGame.players) {
            assertNotEquals(0, player.hand.size)
        }
        // 3*3 player hand cards and 3 center cards
        assertEquals(32-4*3, gs.currentGame.drawPile.size)
    }

    /**
     * Test if refresh is called after starting new game.
     */
    @Test
    fun testNewGameRefresh() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterStartGameCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)
        assertTrue(testRefreshable.refreshAfterStartGameCalled)
    }

    /**
     * Test if endGame() refreshes when called.
     */
    @Test
    fun testEndGame() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterEndGameCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)
        gs.endGame()
        assertTrue(testRefreshable.refreshAfterEndGameCalled)
    }

    /**
     * Test if nextRound() correctly sets the current player
     */
    @Test
    fun testNextRound() {
        val gs = GameService()
        gs.menuService.startGame("s1", "s2", "s3", null)
        var currentPlayer = gs.currentGame.currentPlayer
        var expectedPlayer: Player?
        repeat(3) {
            expectedPlayer =
                if(gs.currentGame.players.indexOf(currentPlayer) != gs.currentGame.players.size-1) {
                    gs.currentGame.players[gs.currentGame.players.indexOf(currentPlayer)+1]
                } else {
                    gs.currentGame.players[0]
                }
            gs.nextRound()
            currentPlayer = gs.currentGame.currentPlayer
            assertEquals(expectedPlayer, currentPlayer)
        }
    }

    /**
     * Check if endGameIfNecessary() ends game exactly, when the next player has knocked
     */
    @Test
    fun testEndGameIfNecessary() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterEndGameCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)
        gs.currentGame.currentPlayer.hasKnocked = true
        repeat(2) {
            gs.endGameIfNecessary()
            assertFalse(testRefreshable.refreshAfterEndGameCalled)
            gs.nextRound()
        }
        gs.endGameIfNecessary()
        assertTrue(testRefreshable.refreshAfterEndGameCalled)
    }

    /**
     * Test if Player.handoutCards() correctly reassigns player hand to top three cards and refreshes.
     */
    @Test
    fun testHandOutCards() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterPlayerStateChangeCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)

        val currentPlayer = gs.currentGame.currentPlayer
        val handBefore = currentPlayer.hand
        val sizeBefore = gs.currentGame.drawPile.size
        assertEquals(handBefore, currentPlayer.hand)
        with(gs) {
            gs.currentGame.currentPlayer.handOutCards()
        }
        assertNotEquals(handBefore, currentPlayer.hand)
        assertEquals(sizeBefore-3, gs.currentGame.drawPile.size)
        assertTrue(testRefreshable.refreshAfterPlayerStateChangeCalled)
    }

    /**
     * Test if resetPlacedCards() correctly reassigns placed cards and refreshes.
     */
    @Test
    fun testResetPlacedCards() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)

        val centerBefore = gs.currentGame.placedCards
        val sizeBefore = gs.currentGame.drawPile.size
        assertEquals(centerBefore, gs.currentGame.placedCards)
        gs.resetPlacedCards()
        assertNotEquals(centerBefore, gs.currentGame.placedCards)
        assertEquals(sizeBefore-3, gs.currentGame.drawPile.size)
        assertTrue(testRefreshable.refreshAfterPlacedCardsChangeCalled)
    }

    /**
     * Test if pass works as expected.
     */
    @Test
    fun testPass() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)

        val centerBefore = gs.currentGame.placedCards
        repeat(2) {
            gs.actionService.pass()
            assertEquals(centerBefore, gs.currentGame.placedCards)
            gs.nextRound()
        }
        gs.actionService.pass()
        assertNotEquals(centerBefore, gs.currentGame.placedCards)
        assertTrue(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        assertEquals(0, gs.currentGame.passCount)
    }

    /**
     * Test if pass counter is incremented and reset correctly.
     */
    @Test
    fun testPassCounterReset() {
        val gs = GameService()
        gs.menuService.startGame("s1", "s2", "s3", null)
        for(i in 1..2) {
            gs.actionService.pass()
            assertEquals(i, gs.currentGame.passCount)
            gs.nextRound()
        }
        gs.actionService.knock()
        assertEquals(0, gs.currentGame.passCount)
    }

    /**
     * Test if trade one works as expected.
     */
    @Test
    fun testTradeOne() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        assertFalse(testRefreshable.refreshAfterPlayerStateChangeCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)

        val centerBefore = gs.currentGame.placedCards
        val handBefore = gs.currentGame.currentPlayer.hand
        gs.actionService.tradeOne(handBefore[1], centerBefore[2])
        assertEquals(centerBefore[0], gs.currentGame.placedCards[0])
        assertEquals(centerBefore[1], gs.currentGame.placedCards[1])
        assertEquals(centerBefore[2], gs.currentGame.currentPlayer.hand[1])
        assertEquals(handBefore[0], gs.currentGame.currentPlayer.hand[0])
        assertEquals(handBefore[1], gs.currentGame.placedCards[2])
        assertEquals(handBefore[2], gs.currentGame.currentPlayer.hand[2])

        assertTrue(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        assertTrue(testRefreshable.refreshAfterPlayerStateChangeCalled)
    }

    /**
     * Test if takeAll() works as expected.
     */
    @Test
    fun testTakeAll() {
        val gs = GameService()
        val testRefreshable = TestRefreshable()
        gs.addRefreshableToServiceLayer(testRefreshable)
        assertFalse(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        assertFalse(testRefreshable.refreshAfterPlayerStateChangeCalled)
        gs.menuService.startGame("s1", "s2", "s3", null)

        val centerBefore = gs.currentGame.placedCards
        val handBefore = gs.currentGame.currentPlayer.hand
        gs.actionService.takeAll()
        assertEquals(centerBefore, gs.currentGame.currentPlayer.hand)
        assertEquals(handBefore, gs.currentGame.placedCards)

        assertTrue(testRefreshable.refreshAfterPlacedCardsChangeCalled)
        assertTrue(testRefreshable.refreshAfterPlayerStateChangeCalled)
    }

    /**
     * Test if knock() works as expected. (Since endGameIfNecessary has been tested above, only the setting of the
     * variable has to be tested here)
     */
    @Test
    fun testKnock() {
        val gs = GameService()
        gs.menuService.startGame("s1", "s2", "s3", null)
        assertFalse(gs.currentGame.currentPlayer.hasKnocked)
        gs.actionService.knock()
        assertTrue(gs.currentGame.currentPlayer.hasKnocked)
    }
}