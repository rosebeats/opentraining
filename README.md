Maintenance
=============
I am hoping to begin working on this app although I cannot garuntee how much time I will have to.

Open Training
=============

Open Training is an Android app for planning your fitness training.


Where can I download the app?
-----------------------------
F-Droid.org (catalogue of FOSS): https://f-droid.org/repository/browse/?fdid=de.skubware.opentraining

You can also use this QR-Code:

<a href='http://www.qrcode-generator.de' border='0' style='cursor:default'><img src='https://chart.googleapis.com/chart?cht=qr&chl=https://f-droid.org/repository/browse/?fdid=de.skubware.opentraining&chs=150x150&choe=UTF-8&chld=L|2' alt='hier qr code erstellen'></a>

**Note**: this fork is not currently hosted.  The original app can be found on F-Droid, but this fork will need to be built from source.

Programming language
--------------------
Java (and some shell scripts for development)

License
-------
GPL 3
Exercises are CC licensed(currently all CC-BY-SA)

Translations
------------
English, German. More wanted! Help here: http://crowdin.net/project/opentraining


Building Instruction
====================

Requirements
------------

  * Git
  * Android-SDK v19
  * Android Support Repository

Building with Eclipse (TODO: add instructions for android studio)
--------------------- 

#### 1. Clone the repository

    $ git clone git://github.com/chaosbastler/opentraining.git

#### 2. Change Eclipse workspace

#### 3. Import 'app'
    * File -> Import -> Existing Projects into Workspace
#### 4. Import 'test'
    * File -> Import -> Existing Projects into Workspace

#### 5. Import support library
    * Instructions: http://developer.android.com/tools/support-library/setup.html#add-library; 'Adding libraries with resources', step 1 to 4 should be enough

#### 6. Disable Lint für support library
    *Click right on project 'android-support-v7-appcompat' -> Properties -> Android Lint Preferences -> Ignore all


Building with gradle
--------------------

#### 1. Connect your phone with USB

#### 2. Build & Install

    $ gradle installDebug
