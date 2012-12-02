
Pingly
======

Download [Pingly 0.9.2_beta from the Google Play Store](https://play.google.com/store/apps/details?id=net.nologin.meep.pingly)!

What Is Pingly?
---------------

Pingly is an Android app that allows you to configure tests (called probes) to check whether a host or service is alive. These probes can be run manually, but also scheduled to run once in the future, or in a repeating manner. Scheduled runs can generate success/failure notifications. The log output of each run is saved (up to a configurable maximum).

At the moment, the following types of tests (probes) are available:

- **HTTP Response** - Make a GET request to the configured URL. Any HTTP response means success. (I may expand this in the future to check response codes/headers/content etc)
- **Socket Connection** - Attempt a TCP connection to the specified host/port.
- **Ping** - Ping the specified host, expecting responses to the specified number of packets in the given deadline.

Probes can be started manually, or can be scheduled to run at a specific time with a specific frequency (Eg, once off, every N minutes, etc).

Pingly keeps a log of the run history of each probe (up to a maximum, defined in Settings).

What devices will it run on?
----------------------------

It should run on any device running [Android 2.1.x (API level 7)](http://developer.android.com/guide/appendix/api-levels.html) or higher. It is not optimized for tablets/high resolution devices (yet), but should still work fine.

Design Comments / TODO Wishlist
-------------------------------

Pingly was my first attempt at a complete android application, so it's by no means a perfect.  It's written using almost
no external libraries (I wanted to see what could be achieved with the android libraries alone).

It was initially written as an android 1.6 application (which will explain why I'm using some older API techniques), but
these days, 1.5/6 are [all but gone](http://developer.android.com/about/dashboards/index.html), I moved it to a 2.1
minimum. Given it was my first app, I decided to keep using just the core 2.1 libs.

**TODO**

- Add more graphics for higher res devices and improve [layout for tablets](http://developer.android.com/guide/practices/tablets-and-handsets.html).  May necessitate moving to a fragment based layout.
- For continued development, it may be prudent to [start using newer APIs] (http://developer.android.com/tools/extras/support-library.html).


Why does it need those permissions?
-----------------------------------

* [Full Internet Access](http://developer.android.com/reference/android/Manifest.permission.html#INTERNET) - Pingly cannot probe anything if it cannot make connections!
* [View network state](http://developer.android.com/reference/android/Manifest.permission.html#ACCESS_NETWORK_STATE) - To see if an active data connection is available.
* [Automatically Start At Boot](http://developer.android.com/reference/android/Manifest.permission.html#RECEIVE_BOOT_COMPLETED) - To reschedule any active scheduled probes after a reboot.
* [Control Vibrator](http://developer.android.com/reference/android/Manifest.permission.html#VIBRATE) - Vibrate when a notification is generated (Can be turned on in the settings).

Play Store Changelog
--------------------

* **28 Nov 2012** - *0.9.2_beta* 
	- Fix SQLHelper concurrency issue
	- Fix ProbeRunner issue where a new run would be started on screen rotate.
* **10 May 2012** - *0.9_beta*
	- First version published to the play store!

