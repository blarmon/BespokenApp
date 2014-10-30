BespokenApp README
===========
bespoken provides a platform for spoken word poets to collaborate and share their work.  

Alex Peterson: The social networking features are powered by a separate backend, seen here:  https://github.com/PeteStreet/bespoken-backend
I focused the majority of my efforts on building the backend of the app, which is explained in more detail on its own readm.  My work also entailed setting up WebViews which display the contents of the backend, as well as wrestling with calling GET and POST request from Android in order to make the uploading/streaming function.  As of now, streaming only works on a few Android devices (the webviews with poem recordings contain an HTML5 <audio> tag).  

Lauren Naylor: I worked mainly on creating the record page, on which users can record, listen back, and post their spoken word poetry. Clicking on the microphone image on the record page will cause the app to start recording - the microphone turns into a stop button which will stop the recording and open a popup window where the user can playback their recording, enter its name and info, and post it. They can also hit the "try again" button, which closes the popup and allows the to rerecord their poetry.   I also worked on putting tabs in the middle of the poem and profile pages, and adding the swipe to refresh feature to the main feed, poem, and profile pages.
