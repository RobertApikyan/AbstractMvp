package robertapikyan.com.abstractmvp.presentation.presenter

import robertapikyan.com.abstractmvp.presentation.view.IView
import robertapikyan.com.abstractmvp.presentation.view.IViewActionDispatcher

/**
 * Presenter, receive view action dispatcher instance via setViewActionObserver lifecycle method
 */
abstract class Presenter<V : IView> : IPresenterLifecycle {

    /**
     * Presenter Factory interface, usually will be
     * implemented by activity or fragment, in order to call presenter constructor and
     * provide it's instance
     */
    interface Factory<V : IView, P : Presenter<V>> {

        fun createPresenter(): P

        companion object {
            /**
             * Presenter Factory, with lambda expression
             */
            fun <V : IView, P : Presenter<V>> fromLambda(factory: () -> P) = object : Factory<V, P> {
                override fun createPresenter() = factory()
            }
        }
    }

    internal lateinit var _viewActionDispatcher: IViewActionDispatcher<V>

    /**
     * Dispatch view actions via calling viewActionDispatcher::onViewAction method
     */
    protected val viewActionDispatcher: IViewActionDispatcher<V>
        get() {
            assertNullableViewActionDispatcher()
            return _viewActionDispatcher
        }

    /**
     * This method is called only once when the presenter instance is created,
     * Depended on IPresenterHolder implementation, if presenter is some scope persistence (such as
     * android activity lifecycle) onCreate is called only once when presenter instance is created,
     * after that onRestore() method will be called when we fetch the presenter instance from
     * IPresenterHolder
     */
    open fun onCreate() {}

    /**
     * This method is called when presenter's already created instance is fetched
     * from IPresenterHolder
     */
    open fun onRestore() {}

    /**
     * onViewAttach, will be called with activity setViewActionObserver
     */
    override fun onViewAttach() {}

    /**
     * onViewStop, will be called with activity onStart
     */
    override fun onViewStart() {}

    /**
     * onViewStop, will be called with activity onStop
     */
    override fun onViewStop() {}

    /**
     * onViewDetach, will be called with activity onDestroy
     */
    override fun onViewDetach() {}

    /**
     * If viewActionDispatcher is not initialized yet, IllegalStateException will be thrown, in order
     * to indicate that viewActionDispatcher is get accessed before presenter::onCreate method call.
     * Usually This is happening when viewActionDispatcher is get accessed from
     * presenter constructor, or from class member fields.
     * If you do not want to throw IllegalStateException in this case, override this method and
     * return false.
     */
    protected open fun throwIfPresenterIsNotCreated() = true

    private fun assertNullableViewActionDispatcher() {
        if (throwIfPresenterIsNotCreated() && !::_viewActionDispatcher.isInitialized)
            throw IllegalStateException("Presenter is not created, or " +
                    "super.setViewActionObserver(viewActionDispatcher) is not called yet")
    }

}