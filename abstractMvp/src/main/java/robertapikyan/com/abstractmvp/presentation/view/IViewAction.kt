package com.robertapikyan.abstractmvp.presentation.view

/**
 * Base view action,
 * invoke method will be called only if view is attached
 */
interface IViewAction<V : IView> {
    fun invoke(view: V)

    companion object {
        fun <V : IView> fromLambda(action: (V) -> Unit) = object : IViewAction<V> {
            override fun invoke(view: V) = action.invoke(view)
        }
    }
}