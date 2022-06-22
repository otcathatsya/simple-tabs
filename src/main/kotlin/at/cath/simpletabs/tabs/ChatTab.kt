package at.cath.simpletabs.tabs

import at.cath.simpletabs.utility.SimpleColour
import kotlinx.serialization.Transient
import net.minecraft.text.Text
import java.util.*


@kotlinx.serialization.Serializable
class ChatTab(
    var name: String,
    var regex: String = ".*",
    var inverted: Boolean = false,
    var muted: Boolean = false,
    var uuid: String = UUID.randomUUID().toString(),
    var literal: Boolean = false,
    var prefix: String = "",
    var language: TranslationTarget = TranslationTarget()
) {
    @Transient
    private var regExp: Regex = compileRegex(regex, literal)

    @Transient
    val messages = mutableListOf<Text>()

    @Transient
    var unreadCount = 0

    var theme = TabTheme(SimpleColour(166, 159, 152, 255 / 2), SimpleColour.WHITE, SimpleColour.BLACK)

    private fun compileRegex(pattern: String, literal: Boolean): Regex =
        Regex(pattern, if (literal) setOf(RegexOption.LITERAL, RegexOption.IGNORE_CASE) else setOf())

    fun updateSettings(
        name: String = this.name,
        regex: String = this.regex,
        muted: Boolean = this.muted,
        literal: Boolean = this.literal,
        inverted: Boolean = this.inverted,
        prefix: String = this.prefix,
        language: TranslationTarget = this.language
    ) {
        this.name = name
        this.regex = regex
        this.muted = muted
        this.inverted = inverted
        this.literal = literal
        this.language = language
        this.prefix = prefix
        this.regExp = compileRegex(regex, literal)
    }

    fun acceptsMessage(input: String): Boolean = regExp.containsMatchIn(input) == !inverted

    @kotlinx.serialization.Serializable
    class TranslationTarget(val targetLanguage: String = "")
}