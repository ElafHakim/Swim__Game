package service

import  entity.*
import view.*

/**
 * [Refreshable] implementation that refreshes nothing, but remembers
 * if a refresh method has been called (since last [reset])
 */
class TestRefreshable: Refreshable {

    var refreshAfterStartGameCalled: Boolean = false
        private set

    var refreshAfterPlayerStateChangeCalled: Boolean = false
        private set

    var refreshAfterPlacedCardsChangeCalled: Boolean = false
        private set

    var refreshAfterEndGameCalled: Boolean = false
        private set

    /**
     * resets all *Called properties to false
     */
    fun reset() {
        refreshAfterStartGameCalled = false
        refreshAfterPlayerStateChangeCalled = false
        refreshAfterPlacedCardsChangeCalled = false
        refreshAfterEndGameCalled = false
    }

    override fun refreshAfterStartGame() {
        refreshAfterStartGameCalled = true
    }

    override fun refreshAfterPlayerStateChange(player: Player) {
        refreshAfterPlayerStateChangeCalled = true
    }

    override fun refreshAfterPlacedCardsChange() {
        refreshAfterPlacedCardsChangeCalled = true
    }

    override fun refreshAfterEndGame() {
        refreshAfterEndGameCalled = true
    }

}
