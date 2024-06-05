package view

import service.GameService
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * [MenuScene] that is displayed when the game is finished. It shows the final result of the game
 * as well as the score. Also, there are two buttons: one for starting a new game and one for
 * quitting the program.
 *
 * @param gameService the [GameService] managing the current game
 */
class ScoreboardScene(private  val gameService: GameService) : MenuScene(800, 1080), Refreshable {

    // label for menu titel
    private var titel = Label(
        width = 800, height = 60,
        posX = 0, posY = 10,
        alignment = Alignment.TOP_CENTER,
        text = "SCORE BOARD",
        font = Font(50)
    )

    // label for game result
    private var resultLabel = Label(
        width = 800, height = 500,
        posX = 40, posY = 200,
        alignment = Alignment.TOP_LEFT,
        text = "",
        font = Font(35)
    )

    // button to quit the game
   val quitButton = Button(
        width = 320, height = 80,
        posX = 70, posY = 580,
        text = "Quit", font = Font(40)
    ).apply {
        visual = ColorVisual(221, 136, 136)
        onMouseEntered = { visual = ColorVisual(191, 106, 106) }
        onMouseExited = { visual = ColorVisual(221, 136, 136) }
    }

    // button to return to the main menu scene
    val startButton = Button(
        width = 320, height = 80,
        posX = 410, posY = 580,
        text = "New Game", font = Font(40)
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseEntered = { visual = ColorVisual(106, 191, 106) }
        onMouseExited = { visual = ColorVisual(136, 221, 136)}
    }

    init {
        opacity = .5

        addComponents(
            resultLabel,
            titel,
            quitButton,
            startButton
        )
    }

    // sets result label to a list of players with their corresponding points, sorted descending by points
    override fun refreshAfterEndGame() {
        var resultString = ""
        for(player in gameService.currentGame.players.sortedByDescending { it.points }) {
            resultString += player.name + "\t\t" + player.points + "\t\t" +
                        player.hand[0].toString() + "\t" +
                        player.hand[1].toString() + "\t" +
                        player.hand[2].toString() + "\t" + "\n"
        }
        resultLabel.text = resultString
    }
}