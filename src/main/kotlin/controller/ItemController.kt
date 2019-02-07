package controller

interface ItemController<Available, Active> {

    fun setAvailableItems(items: Available)

    fun setActiveItems(items: Active)

}