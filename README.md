![N|Solid](https://github.com/RobertApikyan/AbstractMvp/blob/master/intro/cover.png?raw=true)

### MinSDK 14+

### AbstractMvp

AbstractMvp is library that provides abstract components for MVP arcitecture realization, with problems solutions that are exist in classic MVP. 

## CLASSIC MVP ISSUES THAT ARE SOLVED IN ABSTRACT MVP 

#### Nullable View instance
In classic MVP realisation we attach the view instance to the presenter and detach it when view is going to be destroyed, so at some point the view instance inside presenter will be null and every time before accessing the view instance we need to make null check, in order to avoid NullPointerException. This behavior is secure, but it requires additional null checkings. To overcome with this, AbstractMvp library provides ViewActions, which are closures, that will be executed only when the view is not null. (later, detailed about ViewAction).

#### Losted UI updates
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





