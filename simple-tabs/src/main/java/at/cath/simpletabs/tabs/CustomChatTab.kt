package at.cath.simpletabs.tabs

import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomChatTab(
    var name: String = "All",
    private var regex: String = "(.*?)",
    var muted: Boolean = false,
    private var inverted: Boolean = false,
    literal: Boolean = false
) : ChatTab {

    private var matcher: Matcher = compilePattern(regex, literal)
    var unreadCount = 0
    val messages = mutableListOf<String>()

    private fun compilePattern(s: String, literal: Boolean): Matcher =
        Pattern.compile(s, if (literal) Pattern.LITERAL else 0).matcher("")

    public fun updateSettings(
        name: String = "All",
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