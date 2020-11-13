# Project 2 - *Tweeter* (Part 2)

**Tweeter** is an android app that allows a user to view his Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **18** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User can **compose and post a new tweet**
  - [x] User can click a “Compose” icon in the Action Bar on the top right
  - [x] User can then enter a new tweet and post this to twitter
  - [x] User is taken back to home timeline with **new tweet visible** in timeline
  - [x] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
  - [x] User can **see a counter with total number of characters left for tweet** on compose tweet page

The following **optional** features are implemented:

- [x] User is using **"Twitter branded" colors and styles**
- [x] User can click links in tweets launch the web browser 
- [ ] User can **select "reply" from detail view to respond to a tweet**
- [x] The "Compose" action is moved to a FloatingActionButton instead of on the AppBar
- [ ] Compose tweet functionality is build using modal overlay
- [x] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.org/android/Using-Parceler).
- [x] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.
- [x] When a user leaves the compose view without publishing and there is existing text, prompt to save or delete the draft. If saved, the draft should then be **persisted to disk** and can later be resumed from the compose view.
- [ ] Enable your app to receive implicit intents from other apps. When a link is shared from a web browser, it should pre-fill the text and title of the web page when composing a tweet. 

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='SimpleTweet2.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes
In part 2, I ran into a bug after updating the project's gradle plugin. My gradle build was throwing the error: 
A problem was found with the configuration of task ':app:processDebugManifest' (type 'ProcessMultiApkApplicationManifest').
> File 'E:\Project\AndroidLayout\app\build\intermediates\merged_manifest\debug\out\AndroidManifest.xml' specified for property 'mainMergedManifest' does not exist.

The fix for this was rather simple, I just had to go into the project settings and downgrade the Android Gradle plugin to 4.0.2 and the Gradle version to 6.4.1.

Moreover, I ran into issues inflating my custom menu items since I modified my appBar within the timeline activity. Adding the following code
to track the toolbar element of the layout fixed my issue of the menu buttons not showing:

>(In the TimelineActivity class at the top of the body, where the other data members are declared)
>Toolbar toolbar; 

>(In the onCreate Method)
>        toolbar = (Toolbar) findViewById(R.id.toolbar);
>        setSupportActionBar(toolbar);
>        toolbar.showOverflowMenu();

And import:
>import androidx.appcompat.widget.Toolbar;

I also ran into an issue with my emulator where the system UI was no longer displaying. I could not scroll down to disable/enable
wifi and data connections. To fix this issue, I had to go into the android virtual device manager and cold boot the device.

Another bug where the filter options for logcat would not show. You can fix this by either restarting Android studio or opening up the
event log (which resides in the bottom right corner by default).

I had another issue where my floating action bar would hide when scrolling down, but never re-appear when scrolling up.
To fix this I had to change the code of the 'onNestedScroll(...)' method, changing:

>child.hide();  

to:

>child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
>        @Override
>        public void onHidden(FloatingActionButton fab) {
>            super.onHidden(fab);
>            fab.setVisibility(View.INVISIBLE);
>        }
>    });

I encountered an issue with writing to a file using the CodePath tutorials provided. I had to change my file access method to: MODE_PRIVATE
as my program would crash upon opening the composeActivity if I gave the default MODE_WORLD_WRITABLE. Logcat through a "file could not be found"
error otherwise. I also had to call .getBytes() in my write method in order to be able to use the private mode:

>fos.write(etCompose.getText().toString().getBytes());

# Project 2 - *SimpleTweet* (Part 1)

**SimpleTweet** is an android app that allows a user to view his Twitter timeline. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **6** hours spent in total

## User Stories

The following **required** functionality is completed:

- [x] User can **sign in to Twitter** using OAuth login
- [x]	User can **view tweets from their home timeline**
  - [x] User is displayed the username, name, and body for each tweet
  - [x] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
- [x] User can refresh tweets timeline by pulling down to refresh

The following **optional** features are implemented:

- [x] User can view more tweets as they scroll with infinite pagination
- [ ] Improve the user interface and theme the app to feel "twitter branded"
- [x] Links in tweets are clickable and will launch the web browser
- [ ] User can tap a tweet to display a "detailed" view of that tweet
- [ ] User can see embedded image media within the tweet detail view
- [ ] User can watch embedded video within the tweet
- [ ] User can open the twitter app offline and see last loaded tweets
- [x] On the Twitter timeline, leverage the CoordinatorLayout to apply scrolling behavior that hides / shows the toolbar.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='SimpleTweet.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

I ran into quite a few rudamentary issues with this project. My primary issue was working with Twitter's API. As stated in the provided tutorials, the twitter API is quite rigorous in terms of its limitations and, while testing out a new application, this can often cause timeouts. I was rather confused when I ran my app a few times and all of a sudden I was getting a json timeout error. Unfortunately, there is not much you can do once you get timed out from the API but wait 15 minutes and then try again. In retrospect, when making updates to the application, verify your code first, then try to run it after you have scrutinized your work, this will help prevent the occurrence of a timeout from having too many calls. I also ran into an issue where I was able to successfully log into my application, the Timeline activity would launch, however, no tweets displayed. This was because I had forgotten to code my size accessor method for tweets, which meant that the program always thought I had a list of 0 tweets. I will not lie, it took me maybe two hours to figure out. A reminder, yet again, to never overlook simple mistakes before believing your issue is something complex!

## Open-source libraries used

- [Android Async HTTP](https://github.com/codepath/CPAsyncHttpClient) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [2020] [Matthew Thung]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
