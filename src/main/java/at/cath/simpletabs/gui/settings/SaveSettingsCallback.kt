package at.cath.simpletabs.gui.settings

import net.minecraft.util.ActionResult

fun interface SaveSettingsCallback {
    fun onClose(): ActionResult
}