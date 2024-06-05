package entity

import java.lang.IllegalArgumentException

/**
 * Class that represents a game state of "Schwimmen".
 *
 * A new [Game] object is created, if a list of 2 to 4 [Player] objects is supplied. The [currentPlayer] is
 * initialised as the fist object of the list. The [passCount] is initialised as 0, since no passes have taken place
 * at the start of the game. A [DrawPile] with all cards in random order is initialised, the three [placedCards] and
 * each players hand are drawn from it.
 *
 * @param players A list of [Player]-Objects with length 2-4. The first object of the list will be the initial
 * [currentPlayer], the order of the list will be the playing order.
 * @throws IllegalStateException if the [players] parameter contains less than two or more the four [Player]s
 */

class Game(val players: List<Player>) {

    /**
     * Player whose turn it currently is, initialized as fist element of [players].
     */
    var currentPlayer = players.first()

    /**
     * Number of Players who have passed in succession, initialized as 0.
     */
    var passCount = 0

    /**
     * The [DrawPile] for this [Game] instance.
     */
    val drawPile = DrawPile()

    /**
     * The three [Card]s in the center of the board.
     *
     * @throws IllegalArgumentException when a list that has a size other than three is assigned to this variable
     */
    var placedCards = drawPile.drawThree()
        set(value) {
            field = if(value.size == 3) {
                value
            } else {
                throw IllegalArgumentException("list of placed cards should be of size 3")
            }
        }

    init {
        check(players.size in 2..4) {
            "number of players has to be between 2 and 4"
        }
    }

}