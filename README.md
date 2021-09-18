# TrackMyLocation
 You can track live your journey from a starting Location to Ended Location on Google Map

# How to use the app

## You Must

In order to get live location this app use `BACKGROUND_LOCATION` and this need to be permitted by user. If you Deney permission then you can't start your journey.

You must allow specific permission which is 'Allow all the time'. This will allow this app to use location form background

## Limitation of BACKGROUND_LOCATION

From Android 8.0 (API level 26) onwards limits how frequently an app can retrieve the user's current location.

When app is running in the FOREGROUND - app can receive location updates quite frequently. But,
When app is running in the BACKROUND - app can receive location updates very less frequently.

## Start New Journey

Clicking on `START JOURNEY Button` it will ask for Permission(s) and also it will ask to `Turn on GPS`(if not ON already),
Then it will open a Screen with Google Map, initially map will show your current location.

As you move place to place - App will `automatically receives your location` for a given interval and you can see a `Polyline` on Map.

## Journey Details shown

Along with Polyline, you can find few more details like
```
Time of Started Journey
Duration of Journey
Distance till now
Speed
```

## End Journey

Clicking on `STOP JOURNEY Button` it will ask for confirmaton with a Dialog and also you may keep going or end this journey.
Clicking on `End Button` it will `save` your journey Details into `Room Database` which will reflact Home Screen.

# Technical Info

## UI/UX

App is implemented with a quite simiply UX with minimal Buttons.

Fully flat hierarchy UI designinig  usin `ConstraintLayout`

Apps supports dynamic `sdp` values for size

All Icons are `vetcor`, No blurry issues

Only Light/Day theme supported as of now.

```
ConstraintLayout is been used for evey kind of designing for all the screens
Following modern Material Color config principles
Custom Drawable for Gradient-Shadow and more
```

## Architecture Pattern

App is having `MVP` Architecture pattern

## Design Pattern

App is fully reactive using `RxJava -> Observer` Class from `androidx - lifecycle`

No static fun - No memroy blockage, used `Singleton Design Pattern` for helper funtions

## Language

`Kotlin` used with many of its features
```
Coroutines
suspend Funcions
Extenstion function
object file for helper Functions
Data Class
Each and everywhere null check
```

## Jitpack

App using `androidx`
```
lifecycle
eventbus
room
```

## gradle plugins

All plugins are organised in a separate file call `dependencies.gradle`

## Database

Room Database