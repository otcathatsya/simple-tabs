package at.cath.simpletabs.tabs

import kotlinx.serialization.Transient
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@kotlinx.serialization.Serializable
class ChatTab(
    var name: String,
    var regex: String = "(.*?)",
    var inverted: Boolean = false,
    var muted: Boolean = false,
    var uuid: String = UUID.randomUUID().toString(),
    var literal: Boolean = false
) {

    @Transient
    private var matcher: Matcher = compilePattern(regex, literal)

    @Transient
    val messages = mutableListOf<String>()

    var unreadCount = 0

    private fun compilePattern(s: String, literal: Boolean): Matcher =
        Pattern.compile(s, if (literal) Pattern.LITERAL else 0).matcher("")

    fun updateSettings(
        name: String,
        regex: String = "",
        muted: Boolean = false,
        literal: Boolean = false,
        inverted: Boolean = false
    ) {
        this.name = name
        this.regex = regex
        this.muted = muted
        this.inverted = inverted
        this.matcher = compilePattern(regex, literal)
    }

    fun acceptsMessage(input: String): Boolean = matcher.reset(input).find() == !inverted
}