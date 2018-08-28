package robertapikyan.com.abstractmvp.presentation.view

/**
 * IViewActionDispatcher main purpose is viewActions delivery to IViewActionObserver where
 * ViewActions will be invoked
 */
interface IViewActionDispatcher<V : IView> {

    /**
     * Receive viewHolder and viewActionObserver instances.
     * Access to view via viewHolder.get() method and dispatch viewAction via
     * Dispatch view actions via viewActionObserver.invoke(viewAction) method
     */
    fun setViewActionObserver(viewHolder: ViewHolder<V>, viewActionObserver: IViewActionObserver<V>)

    /**
     * Call this method inside presenter, in order to send viewAction to view
     * @param actionType, implement you realization for each actionType
     * @param viewAction from presenter
     */
    fun onViewAction(
            actionType: ActionType,
            viewAction: IViewAction<V>
    )
}