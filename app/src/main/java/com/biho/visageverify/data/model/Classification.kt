package com.biho.visageverify.data.model

data class Classification(
    val name: String,
    val likeness: Array<FloatArray>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Classification

        if (name != other.name) return false
        return likeness.contentDeepEquals(other.likeness)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + likeness.contentDeepHashCode()
        return result
    }
}
