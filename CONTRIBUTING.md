There are quite a few ways you can improve this thing.  Some of the
stuff below involves writing code; some of it is just screwing with
images or entering data.

See also the project's list of open issues on GitHub:
https://github.com/kuhrusty/MorbadScorepad/issues

<!--ts-->
* [Interesting stuff in the code](#interesting-stuff-in-the-code)
* [Interesting features for developers](#interesting-features-for-developers)
* [To check out the code](#to-check-out-the-code)
* [Adding a new expansion](#adding-a-new-expansion)
* [Adding new skill cards](#adding-new-skill-cards)
* [Character Icons](#character-icons)
<!-- Added by: rusty, at: 2018-12-23T19:11-08:00 -->
<!--te-->

### Interesting stuff in the code

- Parsing expressions like "MRL <5 or STR 6" into a tree of operations
  which is applied to Adventurers to determine whether a given skill is
  applicable (mostly in the unfortunately named `Req` class)
- Loading Drawables from names generated at runtime
  (`SkillListActivity.loadImage()`)
- Dynamically generating preferences based on data read from file
  (`SettingsActivity.updateExpansionPrefs()`)

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
1. Download the Dominican font from... err... see
   [issue #7](https://github.com/kuhrusty/MorbadScorepad/issues/7).
1. Build -> Clean Project; then you should be able to build & install on
   your device using the green triangle-thing in the toolbar.

###  Adding a new expansion

This is really easy, and can be done without editing any code.

1. Unfortunately, the list of expansions is stored in one file (rather
   than building the list by searching for expansions at runtime).  Edit
   `app/src/main/resources/HandOfDoom/expansions.json` and copy one of
   the existing entries for your new expansion:
     - `id` - just pick something unique
     - `name` - the name which will be displayed in the Settings activity
     - `subdir` - the directory under
       `app/src/main/resources/HandOfDoom/expansions` where your
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

### Adding new skill cards

Probably the simplest, most (err, *only)* functional bit of the app is
the skill list browser: you choose your character, and then it shows you
which skills that character can have (assuming their stats aren't jacked
up by being Weakened, Blinded, etc.).

**Unfortunately,** rather than *generating* card images, it uses *scans*
of the physical cards.  (Ugh!)  This means, if you add new skills, you
also have to add images.  (See
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
mdpi would be 160 * 1.75 = 280dp.

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

* Add some drawable ID to resources/HandOfDoom/adventurers.json
* Make the code in `EditCampaignActivity.updateAdventurerIcon()`
  attempt to load that adventurer-specific image (for an example of
  loading a Drawable from a resource *name* instead of ID, see
  `SkillListActivity.loadImage()`.
