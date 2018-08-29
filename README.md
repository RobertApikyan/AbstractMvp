![N|Solid](https://github.com/RobertApikyan/AbstractMvp/blob/master/intro/cover.png?raw=true)

### MinSDK 14+

### AbstractMvp

AbstractMvp is library that provides abstract components for MVP arcitecture realization, with problems solutions that are exist in classic MVP. 

## CLASSIC MVP ISSUES THAT ARE SOLVED IN ABSTRACT MVP 

#### Nullable View instance
In classic MVP realisation we attach the view instance to the presenter and detach it when view is going to be destroyed, so at some point the view instance inside presenter will be null and every time before accessing the view instance we need to make null check, in order to avoid NullPointerException. This behavior is secure, but it requires additional null checkings. To overcome with this, AbstractMvp library provides ViewActions, which are closures, that will be executed only when the view is not null. (later, detailed about ViewAction).

#### Lost UI updates
After doing some background jobs presenter needs to update ui, but at that point view instance is null. Since view is null, the ui updates will not be executed. AbstractMvp provides ViewActionDispatcher as a solution, which is another abstraction layer and it knows when the view is attached or not, and if it is not attached the viewAction will be cached inside ViewActionDipatcher, and executed when view become attached again (later, detailed about ViewActionDipatcher)

#### Not Persistence Presenter
Usually presenter instance is inside our viewController (Activity or Fragment), and it will be destroyed with viewController. To overcome this, and make presenter instance persistance per viewController life scope, AbstractMvp provides PresenterHolder abstraction, which can be implemented with android ViewModels, Loaders and other lifecycle persistance mechanisms (later, detailed about PresenterHolder).

## ABSTRACT MVP WORKING MECHANISM 

Here we have a View interface that is implemented by viewController (Activity or Fragment) and a Presenter. View contains some methods methodA(), methodB(), ... methodN() that are implemented by viewController. When presneter getting created, it start some background jobs, after finishing them, it needs to notify UI about new changes by calling view.methodB() method. Below is the rough description of steps how it will be done. 

![N|Solid](https://github.com/RobertApikyan/AbstractMvp/blob/master/intro/structure.png?raw=true)

1. Presenter creates new ViewAction closure with methodB() and send it via ViewActionDispatcher. Code snippet with Kotlin will look like this
```kotlin
// Create the ViewAction for methodB
val actionMethodB = IViewAction.fromLambda() { view ->
  view.methodB()
}
 // Notify viewActionDispatcher about actionMethodB
viewActionDispatcher.onViewAction(actionMethodB)
```
2. ViewActionDispatcher will send the viewAction to ViewActionObserver, which contains view instance. Depending from ViewActionDispatcher implementation, viewActions can be cached, if the view is detached, and will be sent when the view will become attached again.
```kotlin 
// Sending actionMethodB to ViewActionObserver 
viewActionObserver.onInvoke(actionMethodB)
```
3. After receiving actionMethodB instance, ViewActionObserver executes it by passing the view instance. 
```kotlin
// Executing actionMethodB inside ViewActionObserver
val view = viewHolder.get() // recieving view instance
actionMethodB.invoke(view) // executing actionMehtodB ViewAction
```
4. When actionMethodB is getting executed, the methodB() will be called on our viewController (Activity or Fragment)

## ABSTRACT MVP MAIN COMPONENTS 
Abstract MVP is consist from a several abstract components, that need to be implemented. Here is the list of them

### Components That are related with View
#### 1. ViewAction
#### 2. ViewActionDispatcher
#### 3. ViewActionObserver
### Components That are related with Presenter
#### 1. PresenterHolder
#### 2. PresenterLifecycleHandler

Lets Discuss them separatly.

### ViewAction

ViewAction is a generic interface [```IViewAction<V:IView>```](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/view/IViewAction.kt) with single method invoke(view: V) where V is the generic type that is inherited from the base IView interface. ViewActions are created inside presnter and passing to viewActionDispatcher and ViewActionObserver calls invoke(view: V) method.


### ViewActionDispatcher

VeiwActionDipatcher is a generic interface [```IViewActionDispatcher<V:IView>```](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/view/IViewActionDispatcher.kt), which is responsible for viewActions delivery to ViewActionObserver where V is the generic type that is inherited from the base IView interface. This interface contains two methods. 
First one is ``` setViewActionObserver(viewHolder: ViewHolder<V>, viewActionObserver: IViewActionObserver<V>) ``` which is called every time when new view is attached. With First argument [viewholder](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/view/ViewHolder.kt) we can get view instance by calling viewHolder.get() method. When the viewController will be destroyed view instance will be automatically removed from viewHolder container. Second argument is viewActionObserver instance. We can send view actions to viewActionObserver by calling viewActionObserver.onInvoke(viewAction) and passing viewAction instance.
Second method is ``` onViewAction(actionType: ActionType, viewAction: IViewAction<V>) ```, which is calling from presenter, every time when we need to pass new viewAction. [ActionType](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/view/ActionType.kt) is an enum with values are STICKY and IMMEDIATE. Here is the difference 
STICKY - When viewActionDispatcher receives viewActions and in that time the view is detached, the viewActions will be added in to queue and delivered when the view will become attached again.
IMMEDIATE - ViewActions will be delivered only if view is attached. If view is detached action will be lost.VeiwActionDipatcher can be implemented as an RxJava Observable or as a LiveData from Android arc. components.


### ViewActionObserver

VeiwActionObserver is a generic interface [```IViewActionObserver<V : IView> ```](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/view/IViewActionObserver.kt) with two methods. 
First one is ```onCreate(viewHolder:ViewHolder)``` which is calling by framework. Here you can save viewHolder instance.
Second one is ```onInvoke(viewAction: IViewAction<V>)``` this method is calling by viewActionDispatcher.


### PresenterHolder

PresenterHolder is a generic interface [```IPresenterHolder<V : IView, P : Presenter<V>>```](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/presenter/IPresenterHolder.kt) with tree methods (put, get, hasPresenter). All this methods are going to be called by framework. The main point of this container class is to make presenter instance persistence from viewController lifecycle scope. This Interface can be implemented with Android Loaders api or with ViewModels from Android arc. components.

### PresenterLifecycleHandler

PresenterLifecycleHandler an interface [IPresenterLifecycleHandler](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/presenter/IPresenterLifecycleHandler.kt) with one method ```onCreate(presenterLifecycle: IPresenterLifecycle)```. This method is calling by framework. Here we receive [presenterLifecycle](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/presenter/IPresenterLifecycle.kt) instance, which has four methods 
```kotlin
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
```
PresenterLifecycleHandler's implementation can be done with custom activity lifeycle callback mechanism or it will be more easy to implement with a Lifecycle component from Android arc. components.

### BINDING ALL TOGETHER 

AbstractMvp library provides [Mvp.Factory<V:IView, P:Presenter<V>>](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/Mvp.kt) genric interface. Factory class must implement from [Mvp.Factory<V:IView, P:Presenter<V>>](https://github.com/RobertApikyan/AbstractMvp/blob/master/abstractMvp/src/main/java/robertapikyan/com/abstractmvp/presentation/Mvp.kt)  and override all methods and returns already implemented componens. 
To get presenter instance call ```Mvp.from(factory:Factory<V,P>)``` method and pass your factory instance. 


This library is The abstraction for MVP arctecture, You can implemented it and create your custom MVP arc. implementation.











