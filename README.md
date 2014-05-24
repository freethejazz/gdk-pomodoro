Pomodoro
=====

The app is currently broken in XE16 and greater, but a fix is in the works.

This is a simple pomodoro app based on the sample Timer app.

When the app starts, the pomodoro immediately begins in a live card. When 
tapping the live card, a menu displays allowing a few actions:

- Pause: pause the pomodoro.  Shouldn't really be used, from what I
      understand about pomodoros
- Resume: resume a paused pomodoro
- Mulligan: resets to the beginning of the last pomodoro (from both
      within the work time and within the rest time)
- Stop: remove the pomodoro live card from the timeline

A 25 minute work block followed by a 5 minute rest block counts as a single
pomodoro, and when the next pomodoro starts, the pomos completed count is
incremented.

The voice trigger is:

"ok glass, start a pomodoro"

## Installation

The GDK (Glass Development Kit, the software library used to build this 
application) isn't finished yet.  As a result, the process for loading apps
like this isn't quite as refined as I'd hope.  `Pomodoro.apk` is the one 
you need if you're only interested in side-loading the app onto your Glass.

To download it, simply click on the file in the list above and on the next page,
click `View Raw` to download it.

Once you have the APK, you'll need to do the actual side-loading part.  There are
a lot of guides out there, but here's a video walkthrough I found with a quick search:

[Sideload and uninstall apps - YouTube](https://www.youtube.com/watch?v=TYJQhebDvRE)

(Make sure you've got ADB installed. An embedded link is provided in the video above
for instructions)


### TODO's

There are a few bits that I'd like to add at some point:

- Replace "Pomos Completed: #" with a tomato image for each pomo completed,
  maybe followed by a dashed outline of a tomato
- Customize the menu icons
- Change the sounds that play when the timer hits 0
- Allow the user to change the length of work and break times
- Schedule longer breaks into a sequence of pomos

## Comments

Java is not really my thing, so any feedback is greatly appreciated.  This
can be in the form of github issues, pull requests, tweets [@freethejazz](http://www.twitter.com/freethejazz),
emails to my twitter handle @ that big G email provider, complaints written
on $20 dollar bills mailed to me in Chicago, etc.  
