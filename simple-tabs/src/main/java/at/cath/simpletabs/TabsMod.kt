package at.cath.simpletabs

import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object TabsMod : ModInitializer {
    const val MOD_ID = "simpletabs"
    val logger: Logger = LogManager.getLogger(MOD_ID)

    override fun onInitialize() {
        println("$MOD_ID loaded! Note that this mod applies changes purely client-side.")
    }
}