package at.cath.simpletabs.gui.settings

interface SaveCallback {
    fun canSave(): Boolean
    fun save()
}