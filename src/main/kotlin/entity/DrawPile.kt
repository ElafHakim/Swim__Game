package entity

/**
 * Data structure that holds [Card] objects and provides implementations of the two types of interactions that occur
 * with the [DrawPile], [drawOne] and [drawThree].
 *
 * A additional [size] parameter is added so [remainingCards] does not need to be referenced directly to get the number
 * of remaining cards. At initialisation, the 32 cards of the game description are added to the pile and the shuffled.
 */
class DrawPile {

    /**
     * Cards that are still in the [DrawPile].
     */
    val remainingCards: MutableList<Card> = mutableListOf()

    /**
     * Number of cards still in the [DrawPile].
     */
    val size get() = remainingCards.size

    init {
        // add one card for every combination of suit and value
        for (suit in CardSuit.values()) {
            for (value in CardValue.shortDeck()) {
                remainingCards.add(Card(suit, value))
            }
        }
        // make order random
        remainingCards.shuffle()
    }

    /**
     * returns the first [Card] of the [remainingCards]
     *
     * @throws IllegalStateException if there is no card on the stack left to draw
     */
    fun drawOne(): Card {
        check(remainingCards.size >= 1) {
            "not enough card in draw-pile"
        }
        return remainingCards.removeFirst()
    }

    /**
     * returns a [List] of the fist three [Card]s of the [remainingCards]
     *
     * @throws IllegalStateException if there aren't three cards left on the stack to draw
     */
    fun drawThree(): List<Card> {
        check(remainingCards.size >= 3) {
            "not enough card in draw-pile"
        }
        return listOf(drawOne(), drawOne(), drawOne())
    }
}