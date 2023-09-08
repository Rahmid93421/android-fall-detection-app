# Android Fall Detection App
A basic implementation of https://eudl.eu/pdf/10.1007/978-3-642-23902-1_4 for Android devices, made in Android Studio and Java.
The app was made for a college project, but unfortunately I did not got to finish all the features that were requested ( do not worry, I passed :) ).
I want to note that this was my first try on working with Android Studio and with Java programming language, so I am quite pleased that it got to work. Also the project was done in around 1 week before presentation, so I am even more pleased that it got to a working point and the final app was working :)

So, the full feature set that were requested:
  - The concept of the app (basically this paper that helped me to get the logic https://eudl.eu/pdf/10.1007/978-3-642-23902-1_4)  $${\color{green}(ALREADY DONE)}$$
  - The scenario (meaning also the scenario from the paper, an app that detects when a person fell - mostly for elder people, using the phone accelerometer) $${\color{green}(ALREADY DONE)}$$
  - To implement the alghorithm that is responsible for the fall detection $${\color{green}(DONE)}$$
  - Once the fall was detected, a notification should pop up asking for the 5 seconds if the person is ok (+ vocal validation, but did not have enough time to try implementing it) $${\color{red}(PARTIALLY DONE)}$$
  - In case the 5 seconds passed, the app should send a message on Telegram to notice the responsible that the person fell (+ photo from the phone and GPS coordinates, again not enough time to do it) $${\color{red}(PARTIALLY DONE)}$$

The project contains 2 main file:
  - app\src\main\java\com\proiect\proiect_java\MainActivity.java
  - app\src\main\java\com\proiect\proiect_java\ThreadClass.java

MainActivity - is the main entry in the program, from there starts everything. The accelerometer data is fetched from the MainActivity class from the 'onSensorChanged(...)' callback. It gets called everytime the value of the sensor changed. The values get through a [Low Pass Filter](https://en.wikipedia.org/wiki/Low-pass_filter) (the implementaion was found here https://github.com/Bhide/Low-Pass-Filter-To-Android-Sensors#programmatically-apply-low-pass-filter). The values are then stored in a global variable that is located in the ThreadClass.

The ThreadClass does the fall detection process. Basically in MainActivity is instantiated a thread and that thread does the process of the accelerometer values all the time. Also the functionality to send a message through Telegram is done in this file too.

The reason why I posted this app was the lack of other similar projects like this. I mean I do not wanted the full project already done, but I wanted a starting point for the fall detection thing. 
Hope this will help others with their needs or at least give them a starting point.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/rJZpy4SMdZg/0.jpg)](https://www.youtube.com/watch?v=rJZpy4SMdZg)
