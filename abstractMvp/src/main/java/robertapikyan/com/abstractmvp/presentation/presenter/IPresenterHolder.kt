package robertapikyan.com.abstractmvp.presentation.presenter

import robertapikyan.com.abstractmvp.presentation.view.IView

/**
 * IPresenterHolder represent's holder container for Presenter instance,
 * It receive's presenter instance via put() method, and provide's the same instance
 * via get() method
 * The main point of this class is to inherit from android lifecycle persistence objects such as
 * Loaders and ViewModels and every time provide the same presenter instance
 */
interface IPresenterHolder<V : IView, P : Presenter<V>> {
    fun hasPresenter(presenterKey:Any): Boolean
    fun put(presenterKey:Any,presenter: P)
    fun get(presenterKey:Any): P
}