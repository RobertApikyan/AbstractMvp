package com.robertapikyan.abstractmvp.presentation.presenter

/**
 * IPresenterLifecycleHandler is responsible for presenter lifecycle handling
 * Receive presenter lifecycle instance via setViewActionObserver method,
 * All lifecycle methods will be called by PresenterLifecycleHandler except Lifecycle::setViewActionObserver method,
 * this method will be called by MVP framework
 */
interface IPresenterLifecycleHandler {
    fun onCreate(presenterLifecycle: IPresenterLifecycle)
}