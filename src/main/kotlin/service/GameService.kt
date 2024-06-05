package service

import entity.*
import view.*

/**
 * Main class of the service layer for "Schwimmen". Provides access
 * to all other service classes and holds the [currentGame] state for these
 * services to access. Also provides the logic for actions not directly
 * related to a single player.
 *
 * @throws UninitializedPropertyAccessException if [currentGame] is accessed before the game has started
 */
class GameService: AbstractRefreshingService() {

    /**
     * The currently active game. Initialized when startGame() in [MainMenuService] is called.
     *
     * @throws UninitializedPropertyAccessException if accessed before initialization
     */
     lateinit var currentGame: Game

    /**
     * The [MainMenuService] this class provides access to.
     */
    val menuService: MainMenuService = MainMenuService(this)

    /**
     * The [ActionService] this class provides access to.
     */
    val actionService: ActionService = ActionService(this)

    // player after current player in the list, first player if current is last element
    private val nextPlayer get() = currentGame.players.let {
        it[(it.indexOf(currentGame.currentPlayer) + 1) % it.size]
    }

    /**
     * The current player is set to the next player.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun nextRound() {
        currentGame.currentPlayer = nextPlayer
        onAllRefreshables {
            refreshAfterPlayerStateChange(currentGame.currentPlayer)
        }
    }

    /**
     * Ends the game if the next player has knocked.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun endGameIfNecessary() {
        if(nextPlayer.hasKnocked) {
            endGame()
        }
    }

    /**
     * Hands out top three cards form the [DrawPile] to the layer and refreshes.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun Player.handOutCards() {
        this.hand = currentGame.drawPile.drawThree()
        onAllRefreshables {
            refreshAfterPlayerStateChange(this@handOutCards)
        }
    }

    /**
     * Places top three cards form the [DrawPile] in the center of the board and refreshes.
     *
     * @throws UninitializedPropertyAccessException if the game has not started i.e. [MainMenuService.startGame] has
     * not been called
     */
    fun resetPlacedCards() {
        currentGame.placedCards = currentGame.drawPile.drawThree()
        onAllRefreshables {
            refreshAfterPlacedCardsChange()
        }
    }

    /**
     * Makes the necessary refresh after game end.
     */
    fun endGame() {
        onAllRefreshables {
            refreshAfterEndGame()
        }
    }

    /**
     * Adds the provided [refreshable] to all services connected
     * to this root service
     */
    fun addRefreshableToServiceLayer(refreshable: Refreshable) {
        this.addRefreshable(refreshable)
        menuService.addRefreshable(refreshable)
        actionService.addRefreshable(refreshable)
    }

}