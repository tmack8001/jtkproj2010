# JTKMap #

OK, so some shit about the mapping shit:
  * You can access both the workspace map and c-space maps via functions.
  * `JTKMap map = new JTKMap()` will create the map.
  * To access the workspace map, `map.workspace(x,y)` can be used.
  * For the c-space, use `map.cspace(x,y)`
  * **NOTE:** You can use `double`s or `int`s - `double` arguments will be in _meters_ (as in stage) while `int` arguments will be in _pixels_ (in the image).