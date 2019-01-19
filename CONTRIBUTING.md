There are quite a few ways you can improve this thing.  Some of the
stuff below involves writing code; some of it is just screwing with
images or entering data.

See also the project's list of open issues on GitHub:
https://github.com/kuhrusty/MorbadScorepad/issues

**Contents:**
<!--ts-->
* [About the code](#about-the-code)
* [Interesting features for developers](#interesting-features-for-developers)
* [To check out the code](#to-check-out-the-code)
* [Adding a new expansion](#adding-a-new-expansion)
* [Adding your own danger card recordings](#adding-your-own-danger-card-recordings)
* [Adding new skill cards](#adding-new-skill-cards)
* [Character Icons](#character-icons)
* [More blather about Drawables](#more-blather-about-drawables)
<!-- Added by: rusty, at: 2019-01-18T18:17-08:00 -->
<!--te-->

### About the code

Because this started off as an app for saving the game state (basically
the record sheets on p. 34-35), there's a bunch of incomplete crap
related to saving & loading games.  If you're just here for the
skill browser, you can ignore all that (and, with the default settings,
the incomplete/broken stuff is hidden from users anyway).

See also [Interesting stuff in the code](INTERESTING.md).

### Interesting features for developers

- All the data files are in JSON, which is pretty human-readable.
- Adding a new expansion is pretty easy; see below.
- If developer options are turned on in **Settings**, you can have it
  look at a different directory for a completely different set of data
  files.

### To check out the code

In Android Studio 3.2.1:

1. File -> New -> Project from Version Control -> Git
1. Clone Repository:
   - URL: https://github.com/kuhrusty/MorbadScorepad.git
   - Directory: (whatever you want; probably
     `/home/.../AndroidStudioProjects/MorbadScorepad`)

   That should check the stuff out and start a Gradle build.  (If you
   get errors about missing .iml files, ignore them--do *not* remove the
   modules it's talking about.)
1. ~~Download the Dominican font from~~... err... see
   [issue #7](https://github.com/kuhrusty/MorbadScorepad/issues/7).
   For now a free Caslon Antique font is included in the source tree.
1. Build -> Clean Project; then you should be able to build & install on
   your device using the green triangle-thing in the toolbar.

###  Adding a new expansion

This is really easy, and can be done without editing any code.

1. Unfortunately, the list of expansions is stored in one file (rather
   than building the list by searching for expansions at runtime).  Edit
   `app/src/main/assets/HandOfDoom/expansions.json` and copy one of
   the existing entries for your new expansion:
     - `id` - just pick something unique
     - `name` - the name which will be displayed in the Settings activity
     - `subdir` - the directory under
       `app/src/main/assets/HandOfDoom/expansions` where your
       expansion's files will be found
1. Create that new `subdir` so that you have a place to put your new
   files.
1. Depending on the kind of expansion, copy some files from the base
   game or another expansion into your new directory.  For example, if
   this is a character expansion, you want a new `adventurers.json` with
   the new characters, and a `skills.json` containing any new skills.
   If it's a world book which contains new missions, you'll want a new
   `missions.json` containing the list of missions.
1. OK, I lied a *little* bit about not having to edit any code.  Update
   the number of expected expansions in
   `JSONGameRepositoryTest.testGetExpansions()`, as it's probably going
   to croak when it finds your new expansion.
1. To test, either build & run the app and confirm that your new
   expansion shows up in the settings activity, or, better, add a unit
   test (or add to an existing test) in `JSONRepositoryTest`.  In your
   test, you'll need to create a new `GameConfiguration`, passing in
   your new expansion's `id`; there are examples of this in
   `JSONRepositoryTest`.

### Adding your own danger card recordings

To add your own recordings to the Danger Deck activity (which are
probably going to be better than mine!):

1. In `app/src/main/res/values/strings.xml`, find
   `pref_danger_voice_set_titles`, and add an entry with a human-readable
   name for your new voice set.
1. Also add an entry to `pref_danger_voice_set_values` with some unique
   string which will be the new voice set's ID.
1. Record yourself reading one card as a test and put it in
   `app/src/main/res/raw`; its name should be
   `danger_`*voice set ID*`_`*danger card ID*`.m4a` (or whatever kind of
   audio file you're using).  To get the list of danger card IDs, look
   in `app/src/main/assets/HandOfDoom/danger.json`.  (For example, the
   Bandit Camp is `bandit_camp`, so its audio file in the `rusty` voice
   set would be `danger_rusty_bandit_camp.m4a`.)
1. Build & deploy the app, and confirm that your new voice set shows up
   in Settings; choose it, and make sure the "Danger Card sounds"
   setting is "Full" ("Name Only" probably does the same thing).
1. Start the Danger Deck activity, and draw cards until the one with
   your sound comes up; confirm that you hear your sound instead of my
   `not_done.m4a` file, which plays when we can't find the audio file
   for a danger card.  (To skip through to the card with your sound,
   use the "Draw Secondary" button to keep the "reshuffle danger deck"
   cards from reshuffling; then tap the card to play its sound.)

If that all works, go back and finish recording the rest of your danger
card readings.  (**It would be cool** if you had different background
sounds: banjo music or buzzing insects for the wetlands, techtonic
rumbling for the badlands, blowing wind for the highlands, etc.)

I don't know that much about audio file formats, so I just went with
whatever my phone's voice recorder used; the sound quality could
probably be improved *and* the file sizes could probably be reduced.

### Adding new skill cards

Unfortunately, rather than *generating* card images, the skill browser
uses *scans* of the physical cards.  (Ugh!)  This means, if you add new
skills, you also have to add images.  (See
[issue 9](https://github.com/kuhrusty/MorbadScorepad/issues/9).)  Until
that's fixed, here are notes on how I use the GIMP plugin to process
cards.

First, put `extras/card_to_android.py` in your GIMP plugins directory
(mine is `~/.gimp-2.8/plug-ins/`).  Start up the GIMP, and set your
rectangle select tool to use a fixed 900x1400 selection.  Then, scan
some cards at 600 DPI.  For each card:

- select it using the rectangle selection tool
- right click; run the "Card to Android" plugin
- enter the skill's skill ID (whatever you added to `skills.json`)
- if it's a Mastery instead of a Skill, choose "Card type" = "Mastery"

Scanning 10 cards at a time, I can do both sides in 15 minutes.

From https://developer.android.com/training/multiscreen/screendensities.html:

> A dp is a density-independent pixel that corresponds to the physical
> size of a pixel at 160 dpi.

...

> To generate these images, you should start with your raw resource in
> vector format and generate the images for each density using the
> following size scale:
>
> * xhdpi: 2.0
> * hdpi: 1.5
> * mdpi: 1.0 (baseline)
> * ldpi: 0.75
>
> This means that if you generate a 200x200 image for xhdpi devices, you
> should generate the same resource in 150x150 for hdpi, 100x100 for
> mdpi and finally a 75x75 image for ldpi devices.

So, if we want skill cards to display at ~2" tall (call it 1.75), then
mdpi would be 160 * 1.75 = 280dp.  (**Why** did I choose that size??
The original cards are ~2.5" tall, and I only cropped a little of the
border when scanning them.  I should go back and fix this.)

By the way, Android's layout stuff seems really dumb: when I want an
image to take up half or a quarter of the available space or whatever,
thinking in terms of inches or centimeters seems just as wrong as
thinking in terms of pixels.

### Character Icons

The little headshots which are displayed on buttons in the campaign
record sheet thing.  I want them to be 3/4" across (see complaint just
above about how dumb that seems), which is 120 dp.

Currently I just have one placeholder image which is used by all
characters.  To add a specific character's image:

* Scale to 240 x 240, save in drawable-xhdpi
* Scale to 180 x 180, save in drawable-hdpi
* Scale to 120 x 120, save in drawable-mdpi
* Scale to 90 x 90, save in drawable-ldpi

Again, if I were cool, I would've written a Script-Fu script to do all
that.)

Then, in order to get those adventurer-specific images to be used, make
these changes:

* Add some drawable ID to assets/HandOfDoom/adventurers.json
* Make the code in `EditCampaignActivity.updateAdventurerIcon()`
  attempt to load that adventurer-specific image (for an example of
  loading a Drawable from a resource *name* instead of ID, see
  `SkillListActivity.loadImage()`.

### More blather about Drawables

For the DangerDeckActivity background images, originally I did what I
thought I was *supposed* to do, and had images of different densities.
However, the result looked like crap on my tablet: it's a relatively
low-*density* screen, so it used the low-density image... but scaled it
up to match the screen resolution instead of choosing a higher-density
version of the image better suited to the screen *resolution*.

(So--using made-up numbers here--if a "low density" phone has a screen
which is 240x360 pixels, and my tablet has a larger-but-still-"low
density" screen which is 480x720 pixels, I think Android was choosing
the "low density" 240x360 image and scaling it up to 480x720--which
looked like crap--rather than just using the image appropriately sized
for the screen.)

So, I abandoned density-specific background images and just went with
one higher resolution image, and let lower-resolution devices scale it
down.  Arguably lame, but the scaled-*up* images really looked
distractingly, head-achingly bad.
