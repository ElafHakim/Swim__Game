package entity

/**
 * Data class for the card objects.
 *
 * It is characterized by a [CardSuit] and a [CardValue]. The value of [points] is calculated on basis of the
 * [value] according to the game rules.
 *
 * @param suit The suit of the card.
 * @param value The value of the card.
 */

data class Card(val suit: CardSuit, val value: CardValue) {

    override fun toString() = "$suit$value"

    /**
     * The number of points the card contributes to the [Player]s score. Since 30.5 is a possible score, this variable
     * is a floating point number.
     */
    val points = when(value) {
        CardValue.TWO -> 2.0
        CardValue.THREE -> 3.0
        CardValue.FOUR -> 4.0
        CardValue.FIVE -> 5.0
        CardValue.SIX -> 6.0
        CardValue.SEVEN -> 7.0
        CardValue.EIGHT -> 8.0
        CardValue.NINE -> 9.0
        CardValue.TEN, CardValue.JACK, CardValue.QUEEN, CardValue.KING -> 10.0
        CardValue.ACE -> 11.0
    }
}