package ru.pashkovske.buratino.price.offer.model

data class QuotationLevelOffers(
    val selfAffiliated: Offer?,
    val alienAffiliated: Offer?
) {
    constructor() : this(
        selfAffiliated = null,
        alienAffiliated = null
    )

    operator fun plus(offer: Offer): QuotationLevelOffers {
        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        return when (offer.affiliation) {
            OfferAffiliation.SELF -> {
                this.copy(selfAffiliated = offer.plusNullable(selfAffiliated))
            }
            OfferAffiliation.ALIEN -> {
                this.copy(alienAffiliated = offer.plusNullable(alienAffiliated))
            }
            else -> throw IllegalArgumentException("Unknown affiliation: ${offer.affiliation}")
        }
    }
}
