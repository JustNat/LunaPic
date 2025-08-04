package com.example.lunapic.repositories.network.aws.data

data class Media(
    val name : String,
    val body : ByteArray,
    val size : Long,
    val bucket : String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Media

        if (name != other.name) return false
        if (!body.contentEquals(other.body)) return false
        if (size != other.size) return false
        if (bucket != other.bucket) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + body.contentHashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + bucket.hashCode()
        return result
    }
}