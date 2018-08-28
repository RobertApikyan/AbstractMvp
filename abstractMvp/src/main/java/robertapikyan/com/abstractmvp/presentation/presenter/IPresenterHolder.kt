package com.robertapikyan.abstractmvp.presentation.presenter

import com.robertapikyan.abstractmvp.presentation.view.IView

/**
 * IPresenterHolder represent's holder container for Presenter instance,
 * It receive's presenter instance via put() method, and provide's the same instance
 * via get() method
 * The main point of this class is to inherit from android lifecycle persistence objects such as
 * Loaders and ViewModels and every time provide the same presenter instance
 */
interface IPresenterHolder<V : IView, P : Presenter<V>> {
    fun hasPresenter(): Boolean
    fun put(presenter: P)
    fun get(): P
}