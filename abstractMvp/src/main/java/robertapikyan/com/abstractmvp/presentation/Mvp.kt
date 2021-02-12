package robertapikyan.com.abstractmvp.presentation

import robertapikyan.com.abstractmvp.presentation.presenter.IPresenterHolder
import robertapikyan.com.abstractmvp.presentation.presenter.IPresenterLifecycleHandler
import robertapikyan.com.abstractmvp.presentation.presenter.Presenter
import robertapikyan.com.abstractmvp.presentation.presenter.PresenterProxy
import robertapikyan.com.abstractmvp.presentation.view.IView
import robertapikyan.com.abstractmvp.presentation.view.IViewActionDispatcher
import robertapikyan.com.abstractmvp.presentation.view.IViewActionObserver
import robertapikyan.com.abstractmvp.presentation.view.ViewHolder

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
        fun getPresenterKey():Any
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
            val presenterKey = factory.getPresenterKey()
            val presenter by lazy { factory.getPresenter() }
            val presenterHolder = factory.getPresenterHolder()
            val presenterProxy by lazy { PresenterProxy(presenter) }
            val presenterLifecycleHandler by lazy { factory.getPresenterLifecycleHandler() }

            // Action dispatcher and observable
            val viewActionObserver = factory.getViewActionObserver()
            val viewActionDispatcher by lazy { factory.getViewActionDispatcher() }

            // holds state whatever presenter is created or restored
            val hasStoredPresenter = presenterHolder.hasPresenter(presenterKey)

            // if holder is empty, we create new presenter instance
            if (!hasStoredPresenter) {
                presenterHolder.put(presenterKey,presenter)
            }

            // set the viewHolder instance,
            // in order to clear it when view scope is going to be destroyed
            presenterProxy.viewHolder = viewHolder

            viewHolder.putView(view)

            // pass the presenterProxy instance as PresenterLifecycle
            presenterLifecycleHandler.onCreate(presenterProxy)

            viewActionObserver.onCreate(viewHolder)

            if (hasStoredPresenter) {
                // if presenter instance is restored, we just set viewActionObserver to viewActionDispatcher,
                // and call presenter.onRestore()
                val storedPresenter = presenterHolder.get(presenterKey)
                storedPresenter._viewActionDispatcher
                        .setViewActionObserver(viewHolder, viewActionObserver)
                storedPresenter.onRestore()
            } else {
                // if presenter is created we set viewActionObserver instance to viewActionDispatcher,
                // pass the viewActionDispatcher instance to presenter,
                // and call presenter.onCreate(). At this point presenter is ready
                viewActionDispatcher.setViewActionObserver(viewHolder, viewActionObserver)
                presenter._viewActionDispatcher = viewActionDispatcher
                presenter.onCreate()
            }

            // return the presenter instance
            return presenterHolder.get(presenterKey)
        }
    }
}