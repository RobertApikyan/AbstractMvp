package com.robertapikyan.abstractmvp.presentation.view

/**
 * This call is holder container for view.
 * View instance will be automatically cleared from ViewHolder with activity onDestroy() method
 */
class ViewHolder<V : IView> {

    private var _view: V? = null

    internal fun putView(view: V) {
        _view = view
    }

    /**
     * Clear will be called by Mvp library
     */
    internal fun clear() {
        _view = null
    }

    /**
     * @return view nullable instance
     */
    fun getView() = _view

    /**
     * Check for view is null
     */
    fun hasView() = _view != null
}