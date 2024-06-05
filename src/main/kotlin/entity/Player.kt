package entity

import java.lang.IllegalArgumentException

/**
 * Entity to represent a player in the game "Schwimmen". The player has a [name], a list [hand] of his three current
 * hand cards and a boolean [hasKnocked] keeping track of whether he has knocked.
 *
 * At initialisation, a check whether the [name] is non-empty is performed. The value of [points] represents the
 * value of the players current hand and is calculated on basis of the current [hand] according to game rules at
 * every access.
 *
 * @param name A non-empty [String] representing the name of the player.
 * @throws IllegalStateException if the [name] parameter is an empty string
 */
class Player(val name: String) {

    /**
     * Indicates weather this player has knocked.
     */
    var hasKnocked = false

    /**
     * The three [Card]s in this players hand. At game start, this value gets assigned three cards from the games
     * stack.
     *
     * @throws IllegalArgumentException when a list that has a size other than three is assigned to this variable
     */
    var hand = emptyList<Card>()
        set(value) {
            field = if(value.size == 3) {
                value
            } else {
                throw IllegalArgumentException("list of placed cards should be of size 3")
            }
        }

    /**
     * The value of this players hand according to the games rules i.e. 30.5 if all three cards have the same value,
     * the maximal sum of point values of cards with the same color else.
     */
    val points get() =
        if(hand.distinctBy { it.value }.size == 1) {
            30.5
        } else {
            CardSuit.values().maxOf { s ->  hand.sumOf { if (it.suit == s) it.points else 0.0 } }
        }

    init {
        check(name.length > 0) {
            "name should not be empty"
        }
    }
}