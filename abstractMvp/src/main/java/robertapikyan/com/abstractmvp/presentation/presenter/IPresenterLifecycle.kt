package robertapikyan.com.abstractmvp.presentation.presenter

/**
 * IPresenterLifecycle is Presenter lifecycle representation
 */
interface IPresenterLifecycle {

    /**
     * onViewAttach, will be called with activity onCreate
     */
    fun onViewAttach()

    /**
     * onViewStop, will be called with activity onStart
     */
    fun onViewStart()

    /**
     * onViewStop, will be called with activity onStop
     */
    fun onViewStop()

    /**
     * onViewDetach, will be called with activity onDestroy
     */
    fun onViewDetach()
}