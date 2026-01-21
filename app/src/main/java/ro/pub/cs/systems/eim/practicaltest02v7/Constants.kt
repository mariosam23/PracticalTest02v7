package ro.pub.cs.systems.eim.practicaltest02v7

import java.util.UUID

object Constants {
    const val URL = "time-a-g.nist.gov"

    fun generateRandomClient() : String {
        val uuid = UUID.randomUUID()
        return uuid.toString()
    }
}