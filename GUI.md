# Sonars #

  * Just add `new SonarView()` to some window.
  * Pass the readings to `updateSonars(double[])`.

# JTKMapImage #

  * Make sure you have your [JTKMap](JTKMap.md) already!
  * use constructor: `new JTKMapImage(JTKMap)`
  * Pass in goal points: `setPoints(List<Point2D>)`
  * Pass in particles: `setParticles(List<Point2D>)`
  * Pass in current path: `setPath(List<Point2D>)`
  * ALL Points must be in _METERS_!