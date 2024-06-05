package view

import service.GameService
import tools.aqua.bgw.core.BoardGameApplication

/**
 * Implementation of the BGW [BoardGameApplication] for the card game "Schwimmen"
 */

class App : BoardGameApplication("Schwimmen"), Refreshable {

    /**
     * Central service from which all others are created/accesse also provides logic for actions not directly
     * related to a single player.
     */
    private  val gameService = GameService()

    /**
     * This menu scene is shown after application start and after the scoreboard is closed.
     */
    private  val mainMenuScene = MainMenuScene(gameService)

    /**
     * This is where the actual game takes place.
     */
    private  val inGameScene = InGameScene(gameService)

    /**
     * This scene is shown after a game has ended.
     */
    private  val scoreboardScene = ScoreboardScene(gameService).apply {
        this.quitButton.onMouseClicked = {
            gameService.menuService.quitGame()
        }
        this.startButton.onMouseClicked = {
            gameService.menuService.startGame("Bob", "Alice", null, null)
            this@App.showGameScene(inGameScene)
            this@App.showMenuScene(mainMenuScene, 0)
        }
    }

    init {

        // all scenes and the application itself need too
        // react to changes done in the service layer
        gameService.addRefreshableToServiceLayer(this)
        gameService.addRefreshableToServiceLayer(mainMenuScene)
        gameService.addRefreshableToServiceLayer(inGameScene)
        gameService.addRefreshableToServiceLayer(scoreboardScene)

        // This is just done so that the blurred background when showing
        // the new game menu has content and looks nicer
        gameService.menuService.startGame("Bob", "Alice", null, null)

        this.showGameScene(inGameScene)
        this.showMenuScene(mainMenuScene, 0)
    }

    override fun refreshAfterStartGame() {
        this.hideMenuScene()
    }

    override fun refreshAfterEndGame() {
        this.showMenuScene(scoreboardScene)
    }
}