package com.wykh4hn.asm2_cos30017.model

/**
 * Choices in the sort menu. The order here must match R.array.sort_options,
 * because the Spinner position is turned into an option with SortOption.entries[position].
 */
enum class SortOption {
    /** The order of the base list. */
    DEFAULT,

    /** Rating, high to low. */
    RATING,

    /** Year, newest to oldest. */
    YEAR,

    /** Daily cost, low to high. */
    COST
}