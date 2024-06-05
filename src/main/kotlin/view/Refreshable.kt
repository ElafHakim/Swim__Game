package view

import entity.*
import  service.*

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 */

interface Refreshable {

    /**
     * perform refreshes that are necessary after a game started
     */
    fun refreshAfterStartGame() {}

    /**
     * perform refreshes that are necessary after a player has changed his state
     *
     * @param player the player whose state has changed
     */
    fun refreshAfterPlayerStateChange(player: Player) {}

    /**
     * perform refreshes that are necessary after the placed cards have changed
     */
    fun refreshAfterPlacedCardsChange() {}

    /**
     * perform refreshes that are necessary after the last round was played
     */
    fun refreshAfterEndGame() {}
}