package robertapikyan.com.abstractmvp.presentation.view

/**
 * IViewActionObserver is receiving viewActions from IViewActionDispatcher via onInvoke method,
 * Get access to view via viewHolder.get() method and invoke view actions via
 * viewAction.invoke(viewHolder.get()) method
 */
interface IViewActionObserver<V : IView> {

    fun onCreate(viewHolder: ViewHolder<V>)

    /**
     * This method is calling by IViewActionDispatcher
     */
    fun onInvoke(viewAction: IViewAction<V>)
}