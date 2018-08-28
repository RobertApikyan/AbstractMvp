package com.robertapikyan.abstractmvp.presentation

import com.robertapikyan.abstractmvp.presentation.presenter.IPresenterHolder
import com.robertapikyan.abstractmvp.presentation.presenter.IPresenterLifecycleHandler
import com.robertapikyan.abstractmvp.presentation.presenter.Presenter
import com.robertapikyan.abstractmvp.presentation.presenter.PresenterProxy
import com.robertapikyan.abstractmvp.presentation.view.IView
import com.robertapikyan.abstractmvp.presentation.view.IViewActionDispatcher
import com.robertapikyan.abstractmvp.presentation.view.IViewActionObserver
import com.robertapikyan.abstractmvp.presentation.view.ViewHolder

class Mvp {

    /**
     * One of the important interface for MVP library.
     * This is the starting point for MVP implementation
     * Inherit from this Factory, and provide your custom implementations
     * for IViewActionDispatcher, IViewActionObserver, IPresenterHolder, IPresenterLifecycleHandler ...
     */
    interface Factory<V : IView, P : Presenter<V>> {
        fun getView(): V
        fun getViewActionDispatcher(): IViewActionDispatcher<V>
        fun getViewActionObserver(): IViewActionObserver<V>
        fun getPresenter(): P
        fun getPresenterHolder(): IPresenterHolder<V, P>
        fun getPresenterLifecycleHandler(): IPresenterLifecycleHandler
    }

    companion object {
        /**
         * Use this method in order to receive your presenter instance
         * based on you Factory implementation
         */
        fun <V : IView, P : Presenter<V>> from(factory: Factory<V, P>): P {
            // View
            val view = factory.getView()
            val viewHolder = ViewHolder<V>()

            // Presenter
            val presenter by lazy { factory.getPresenter() }
            val presenterHolder = factory.getPresenterHolder()
            val presenterProxy by lazy { PresenterProxy(presenterHolder.get()) }
            val presenterLifecycleHandler by lazy { factory.getPresenterLifecycleHandler() }

            // Action dispatcher and observable
            val viewActionObserver = factory.getViewActionObserver()
            val viewActionDispatcher by lazy { factory.getViewActionDispatcher() }

            // holds state whatever presenter is created or restored
            val isPresenterCreated = !presenterHolder.hasPresenter()

            // if holder is empty, we create new presenter instance
            if (!presenterHolder.hasPresenter()) {
                presenterHolder.put(presenter)
            }

            // set the viewHolder instance,
            // in order to clear it when view scope is going to be destroyed
            presenterProxy.viewHolder = viewHolder

            viewHolder.putView(view)

            // pass the presenterProxy instance as PresenterLifecycle
            presenterLifecycleHandler.onCreate(presenterProxy)

            viewActionObserver.onCreate(viewHolder)

            // now if presenter is created we set viewActionObserver instance to viewActionDispatcher,
            // pass the viewActionDispatcher instance to presenter,
            // and call presenter.onCreate(). At this point presenter is ready
            if (isPresenterCreated) {
                viewActionDispatcher.setViewActionObserver(viewHolder, viewActionObserver)
                presenter._viewActionDispatcher = viewActionDispatcher
                presenter.onCreate()
            } else {
                // if presenter instance is restored, we just set viewActionObserver to viewActionDispatcher,
                // and call presenter.onRestore()
                presenterHolder.get()
                        ._viewActionDispatcher
                        .setViewActionObserver(viewHolder, viewActionObserver)
                presenterHolder.get().onRestore()
            }

            // return the presenter instance
            return presenterHolder.get()
        }
    }
}