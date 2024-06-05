package service

import entity.*
import kotlin.system.exitProcess

/**
 * Service layer class providing the logic for main menu related actions.
 *
 * @param gameService the main class of this service layer instance
 */
class MainMenuService(private val gameService: GameService): AbstractRefreshingService() {

    /**
     * Initiates a new game of "Schwimmen."
     *
     * @param p1 name of a player, necessary since two players minimum
     * @param p2 name of another player, necessary since two players minimum
     * @param p3 optional name of an additional player
     * @param p4 optional name of an additional player
     */
    fun startGame(p1: String, p2: String, p3: String?, p4: String?) {
        val players =
            listOfNotNull(Player(p1), Player(p2), p3?.let { Player(it) }, p4?.let { Player(it) }).toMutableList()
        players.shuffle()
        val game = Game(players)
        gameService.currentGame = game
        with(gameService) {
            for(player in game.players) {
                player.handOutCards()
            }
        }
        onAllRefreshables {
            refreshAfterStartGame()
            refreshAfterPlacedCardsChange()
            refreshAfterPlayerStateChange(game.currentPlayer)
        }
    }

    /**
     * ends the application with exit code 0
     */
    fun quitGame() {
        exitProcess(0)
    }
}