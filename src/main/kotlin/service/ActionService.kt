package service

import  entity.*

/**
 * Service layer class that provides the logic for the four possible actions a player
 * can take in "Schwimmen".
 *
 * @param gameService the main class of this service layer instance
 */
class ActionService(private val gameService: GameService): AbstractRefreshingService() {

    private val game get() = gameService.currentGame

    /**
     * Lets the current player pass. If all players have passed in succession, the card in the center are replaced.
     * If not enough cards to replace the center are left, the game ends. If the next player has knocked, the game
     * ends.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun pass() {
        game.passCount++
        if(game.passCount == game.players.size) {
            if (game.drawPile.size >= 3) {
                gameService.resetPlacedCards()
                game.passCount = 0
                onAllRefreshables {
                    refreshAfterPlacedCardsChange()
                }
            } else {
                gameService.endGame()
            }
        }
        gameService.endGameIfNecessary()
    }

    /**
     * The [cardInHand] and the [cardOnTable] get switched. The pass counter is reset. If the next player has knocked,
     * the game ends.
     *
     * @param cardInHand the hand card to be replaced by a card from the center
     * @param cardOnTable the table card to be replaced by a card from the players hand
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun tradeOne(cardInHand: Card, cardOnTable: Card) {
        val player = game.currentPlayer
        player.hand = listOf(replaceCardIfEqual(player.hand[0], cardInHand, cardOnTable),
                             replaceCardIfEqual(player.hand[1], cardInHand, cardOnTable),
                             replaceCardIfEqual(player.hand[2], cardInHand, cardOnTable))
        game.placedCards = listOf(replaceCardIfEqual(game.placedCards[0], cardOnTable, cardInHand),
                                  replaceCardIfEqual(game.placedCards[1], cardOnTable, cardInHand),
                                  replaceCardIfEqual(game.placedCards[2], cardOnTable, cardInHand))
        game.passCount = 0
        onAllRefreshables {
            refreshAfterPlayerStateChange(game.currentPlayer)
            refreshAfterPlacedCardsChange()
        }
        gameService.endGameIfNecessary()
    }

    private fun replaceCardIfEqual(current: Card, cardToReplace: Card, replacement: Card): Card {
        return if (current == cardToReplace) replacement else current
    }

    /**
     * The current players hand and the cards in the center are switched. The pass counter is reset.
     * If the next player has knocked, the game ends.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun takeAll() {
        val player = game.currentPlayer
        player.hand = game.placedCards.also { game.placedCards = player.hand }
        game.passCount = 0
        onAllRefreshables {
            refreshAfterPlayerStateChange(game.currentPlayer)
            refreshAfterPlacedCardsChange()
        }
        gameService.endGameIfNecessary()
    }

    /**
     *  The current players knocked status is set to true. The pass counter is reset.
     *  If the next player has knocked, the game ends.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun knock() {
        game.currentPlayer.hasKnocked=true
        game.passCount = 0
        gameService.endGameIfNecessary()
    }
}