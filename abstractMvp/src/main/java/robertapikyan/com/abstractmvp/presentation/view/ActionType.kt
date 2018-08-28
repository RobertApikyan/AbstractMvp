package com.robertapikyan.abstractmvp.presentation.view

enum class ActionType {
    /**
     * If View is not active viewActions with STICKY type will be placed in queue and
     * delivered to view controller after it become active.
     */
    STICKY,
    /**
     * ViewAction will be delivered only if view is active.
     */
    IMMEDIATE,
}