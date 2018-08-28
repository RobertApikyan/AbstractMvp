package com.robertapikyan.abstractmvp.presentation.presenter

import com.robertapikyan.abstractmvp.presentation.view.ViewHolder

internal class PresenterProxy(
        private val presenterLifecycle: IPresenterLifecycle) : IPresenterLifecycle {

    lateinit var viewHolder: ViewHolder<*>

    override fun onViewAttach() = presenterLifecycle.onViewAttach()

    override fun onViewStart() = presenterLifecycle.onViewStart()

    override fun onViewStop() = presenterLifecycle.onViewStop()

    override fun onViewDetach() {
        presenterLifecycle.onViewDetach()
        if (::viewHolder.isInitialized)
            viewHolder.clear()
    }
}