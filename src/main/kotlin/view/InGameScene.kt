package view

import entity.*
import service.GameService
import tools.aqua.bgw.animation.*
import tools.aqua.bgw.components.container.CardStack
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color


/**
 * This is the main thing for the "Schwimmen" game. The scene shows a dummy card stack, the center
 * cards and the hand of the current player.
 * The actions knock, pass and take all can be triggered with buttons. The action switch cards can be triggered by
 * dragging the hand card on the corresponding center card.
 * After an action took place, the player ends the turn by clicking the next round button.
 * The take all and next round button sometimes appear darker than the other buttons, I don't know why :(
 *
 * @param gameService the [GameService] managing the current game
 */
class InGameScene(private val gameService: GameService) : BoardGameScene(1920, 1080), Refreshable {

    // used to load images for cards
    private val cardImageLoader = CardImageLoader()

    // used to visualize the draw pile
    private val drawPileView = CardStack<CardView>(height = 300, width = 195, posX = 20, posY = 390).apply {
        visual = ColorVisual(Color(255, 255, 255, 50))
    }

    // shows the number of cards left on the stack
    private val cardsLeftLabel = Label(
        width = 100, height = 35,
        posX = 95, posY = 320,
        alignment = Alignment.CENTER_LEFT,
        font = Font(50),
    )

    // shows the cards in the center of the field
    private val centerCards = LinearLayout<CardView>(
        posX = 1920/2 - 500,
        posY = 1080/3,
        alignment = Alignment.CENTER,
        spacing = 50,
        width = 1000,
    )

    // shows the hand of the current player
    private val currentHand = LinearLayout<CardView>(
        posX = 1920/2 - 500,
        posY = 880,
        alignment = Alignment.CENTER,
        spacing = -30,
        width = 1000,
    )

    // button to trigger the "take all" action
    private val takeAllButton = Button(
        posX = 1500,
        posY = 900,
        width = 300,
        height = 100,
        text = "TAKE ALL",
        font = Font(30),
    ).apply {
        onMouseClicked = {
            gameService.actionService.takeAll()
            components.filterIsInstance<Button>().forEach { it.isVisible = false }
            nextRoundButton.isVisible = true
        }
        onMouseEntered = { visual = ColorVisual.LIGHT_GRAY }
        onMouseExited = { visual = ColorVisual.WHITE }
    }

    // button to trigger the "knock" event
    private val knockButton = Button(
        posX = 1500,
        posY = 750,
        width = 300,
        height = 100,
        text = "KNOCK",
        font = Font(30),
    ).apply {
        onMouseClicked = {
            gameService.actionService.knock()
            components.filterIsInstance<Button>().forEach { it.isVisible = false }
            nextRoundButton.isVisible = true
        }
        onMouseEntered = { visual = ColorVisual.LIGHT_GRAY }
        onMouseExited = { visual = ColorVisual.WHITE }
    }

    // button to trigger the "pass" event
    private val passButton = Button(
        posX = 1500,
        posY = 600,
        width = 300,
        height = 100,
        text = "PASS",
        font = Font(30),
    ).apply {
        onMouseClicked = {
            gameService.actionService.pass()
            components.filterIsInstance<Button>().forEach { it.isVisible = false }
            nextRoundButton.isVisible = true
        }
        onMouseEntered = { visual = ColorVisual.LIGHT_GRAY }
        onMouseExited = { visual = ColorVisual.WHITE }
    }

    // button to trigger the next round
    private val nextRoundButton = Button(
        posX = 1500,
        posY = 900,
        width = 300,
        height = 100,
        text = "NEXT ROUND",
        font = Font(30),
    ).apply {
        onMouseClicked = {
            currentHand.isVisible = false
            playAnimation( DelayAnimation(2000).also {
                it.onFinished = {
                    gameService.nextRound()
                    currentHand.isVisible = true
                    components.filterIsInstance<Button>().forEach { it.isVisible = true }
                    this.isVisible = false
                    this.isDisabled = false
                }
            })
            this.isDisabled = true
        }
        onMouseEntered = { visual = ColorVisual.LIGHT_GRAY }
        onMouseExited = { visual = ColorVisual.WHITE }
    }

    // label displaying the names with the current player at the top and the names of the other players ordered by
    // who is next
    private val currentPlayerLabel = Label(
        width = 200, height = 300,
        posX = 1500, posY = 100,
        alignment = Alignment.TOP_LEFT,
        font = Font(50),
    )

    // for switching selected hand card
    private var selectedCard: Card? = null

    init {
        // dark green for "Casino table" flair
        background = ColorVisual(108, 168, 59)

        nextRoundButton.isVisible = false

        addComponents(
            drawPileView,
            cardsLeftLabel,
            centerCards,
            currentHand,
            takeAllButton,
            knockButton,
            passButton,
            nextRoundButton,
            currentPlayerLabel
        )
    }

    // returns the corresponding card view to a card object
    private fun schwimmenCardView(card: Card): CardView = CardView(
        height = 300,
        width = 195,
        front = ImageVisual(cardImageLoader.frontImageFor(card.suit, card.value)),
        back = ImageVisual(cardImageLoader.backImage),
    )

    override fun refreshAfterStartGame() {
        drawPileView.clear()
        centerCards.clear()
        currentHand.clear()

        // at start, the action buttons should be visible
        components.filterIsInstance<Button>().forEach { it.isVisible = true }
        nextRoundButton.isVisible = false

        // add one CardView to imply deck
        drawPileView.add(CardView(
            height = 300,
            width = 195,
            front = ImageVisual(cardImageLoader.frontImageFor(CardSuit.CLUBS, CardValue.SEVEN)),
            back = ImageVisual(cardImageLoader.backImage),
        ))
    }

    override fun refreshAfterPlacedCardsChange() {
        // update center cards
        centerCards.clear()
        for(card in gameService.currentGame.placedCards) {
            val view = schwimmenCardView(card).apply {
                // front should be visible
                currentSide = CardView.CardSide.FRONT

                // a hand card can be dropped if the nextRoundButton is not visible i.e. no other action has been
                // performed this round
                dropAcceptor = {
                    it.draggedComponent is CardView && !nextRoundButton.isVisible
                }

                // animate the card to be bigger, if a valid drag target is held over it and no action has been
                // performed this round
                onDragGestureEntered = {
                    if(!nextRoundButton.isVisible) {
                        playAnimation(
                            ScaleAnimation(componentView = this, byScaleX = 1.1, byScaleY = 1.1, duration = 100)
                        )
                    }
                }
                onDragGestureExited = {
                    if(!nextRoundButton.isVisible) {
                        playAnimation(
                            ScaleAnimation(componentView = this, byScaleX = 1 / 1.1, byScaleY = 1 / 1.1, duration = 100)
                        )
                    }
                }

                // switch this card with dropped card
                onDragDropped = {
                    val immutableSelectedCard = selectedCard
                    if (immutableSelectedCard != null) {
                        gameService.actionService.tradeOne(immutableSelectedCard, card)
                        it.draggedComponent.isVisible = false
                        components.filterIsInstance<Button>().forEach { it.isVisible = false }
                        nextRoundButton.isVisible = true
                    }
                    selectedCard = null
                }
            }
            centerCards.add(view)
        }
        cardsLeftLabel.text = gameService.currentGame.drawPile.size.toString()
    }

    override fun refreshAfterPlayerStateChange(player: Player) {
        // update hand cards
        currentHand.clear()
        for(card in player.hand){
            val view = schwimmenCardView(card).apply {
                currentSide = CardView.CardSide.FRONT
                isDraggable = true
                rotation = player.hand.indexOf(card)*5.0 - 5.0

                // set and reset selected card
                onDragGestureStarted = { selectedCard = card }
                onDragGestureEnded = { _, success -> if(!success) selectedCard = null }
            }
            currentHand.add(view)
        }

        // update the player label
        val players = gameService.currentGame.players
        currentPlayerLabel.text = "Players:\n"
        for (i in players.indices) {
            val p = players[(players.indexOf(player)+i)%players.size]
            if(p.hasKnocked)
                break
            currentPlayerLabel.text += p.name + "\n"
        }

        //when the hand cards change, no reference should point to a card of the old hand
        selectedCard = null
    }
}