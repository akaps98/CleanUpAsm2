s3916884 Kang Junsik

All features
- Sign up
- Sign in
- Create site (desired location and name)
- Join site (filter/search sites, find routes, display site detail info; it is displayed on dialog when site is clicked, some restrictions to join; already join volunteer cannot join again, site owner cannot join his site)
- See owned site detail (site name, site location in map, collected waste, edit collected waste, volunteers list, download volunteers list)
- admin (log in, see all sites, see details of specitic site, see statistics - number of joined volunteers in sites, amount of waste collected)

Technology
- ListView: list all volunteers in sites(owned site) and all sites which are created by users(admin).
- ImageView: to display an app logo at home activity.
- Spinner: let user choose the zoom options(less narrow, more narrow, less wider, more wider) of map.
- Map fragment: to display the location of each sites and select/create desired site.
- SearchView: let user input the site name to search.
- Firebase
->Authentication: to register and sign in
->Firestore database: to save database collection; Site and User, and load database to Android Studio to work on this project.
- Direction API: to show the routes from current location to selected site(I referred some utility classes from other's github which are DataParser, FetchURL, PointsParser and TaskLoadedCallback in 'utilites' directory.).
***Marker is customized. It is not the default provided by Android.

Drawbacks
- There is no notification. Failed to implement.