package view

import service.GameService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual

/**
 * [MenuScene] that is used for starting a new game. It is displayed directly at program start or reached
 * when "New Game" is clicked in [ScoreboardScene]. After providing the names of at least two players,
 * [startButton] can be pressed. There is also a [quitButton] to end the program.
 *
 * @param gameService the [GameService] managing the current game
 */
class MainMenuScene(private val gameService: GameService) : MenuScene(800, 1080), Refreshable {

    // labels and fields to take in player names

    private val p1Label = Label(
        width = 200, height = 35,
        posX = 80, posY = 200,
        alignment = Alignment.CENTER_LEFT,
        text = "Player 1:", font = Font(25)
    )

    private val p1Input: TextField = TextField(
        width = 300, height = 70,
        posX = 80, posY = 250,
        font = Font(35),
    )

    private val p2Label = Label(
        width = 200, height = 35,
        posX = 430, posY = 200,
        alignment = Alignment.CENTER_LEFT,
        text = "Player 2:", font = Font(25)
    )

    private val p2Input: TextField = TextField(
        width = 300, height = 70,
        posX = 420, posY = 250,
        font = Font(35)
    )

    private val p3Label = Label(
        width = 200, height = 35,
        posX = 80, posY = 350,
        alignment = Alignment.CENTER_LEFT,
        text = "Player 3:", font = Font(25)
    )

    private val p3Input: TextField = TextField(
        width = 300, height = 70,
        posX = 80, posY = 400,
        font = Font(35)
    )

    private val p4Label = Label(
        width = 200, height = 35,
        posX = 420, posY = 350,
        alignment = Alignment.CENTER_LEFT,
        text = "Player 4:", font = Font(25)
    )

    private val p4Input: TextField = TextField(
        width = 300, height = 70,
        posX = 420, posY = 400,
        font = Font(35)
    )

    /**
     * Button that ends the application.
     */
    val quitButton = Button(
        width = 320, height = 80,
        posX = 70, posY = 580,
        text = "Quit", font = Font(40)
    ).apply {
        visual = ColorVisual(221, 136, 136)
        onMouseClicked = {
            gameService.menuService.quitGame()
        }
        onMouseEntered = { visual = ColorVisual(191, 106, 106) }
        onMouseExited = { visual = ColorVisual(221, 136, 136) }
    }

    /**
     * Button to start the game.
     */
    private val startButton = Button(
        width = 320, height = 80,
        posX = 410, posY = 580,
        text = "Start", font = Font(40)
    ).apply {
        visual = ColorVisual(136, 221, 136)
        onMouseClicked = {
            val filledFields = components.filterIsInstance<TextField>().filter { it.text.isNotBlank() }
            gameService.menuService.startGame(
                filledFields[0].text.trim(),
                filledFields[1].text.trim(),
                if(filledFields.size > 2) filledFields[2].text.trim() else null,
                if(filledFields.size > 3) filledFields[3].text.trim() else null,
            )
        }
        onMouseEntered = { visual = ColorVisual(106, 191, 106) }
        onMouseExited = { visual = ColorVisual(136, 221, 136)}
    }

    init {
        opacity = .5

        // activate start button, if at least two fields are filled in
        onKeyReleased = {
            startButton.isDisabled = components.filterIsInstance<TextField>().filter{ it.text.isBlank() }.size > 2
        }
        // start button should not be active at game start
        startButton.isDisabled = true

        addComponents(
            p1Label, p1Input,
            p2Label, p2Input,
            p3Label, p3Input,
            p4Label, p4Input,
            quitButton, startButton,
        )
    }

}