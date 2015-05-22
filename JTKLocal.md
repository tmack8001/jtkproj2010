# Localization #

## Constructor: `new JTKLocal(sonar,N,map)` ##
  * sonar is sonar model: `new SonarModel(int)`
  * `N` is number of particles
  * `map` is the map

## Updating with new info: `update(speed,turnrate,sp[])` ##
  * `speed` and `turnrate` are what get passed to player
  * `sp[]` is the sonar reading

## Testing ##
  * just run `java JTKLocal`
  * host/port are hardcoded in `main`