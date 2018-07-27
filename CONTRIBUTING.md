There are quite a few ways you can improve this thing.  Some of the
stuff below involves writing code; some of it is just screwing with
images or entering data.

See also the project's list of open issues on GitHub:
https://github.com/kuhrusty/MorbadScorepad/issues

### Skill Cards

Probably the simplest, most (err, *only)* functional bit of the app is
the skill list browser: you choose your character, and then it shows you
which skills that character can have (assuming their stats aren't jacked
up by being Weakened, Blinded, etc.).  **However,** currently, only a few of the
bazillion skills have images.

**Best** would be to *generate* the card views rather than having
images of each card.  (Like, the **Immunity** skill is two sentences;
it's grossly inefficient to have four differently-scaled photos of the
card.)  However, that means someone will have to write the code to do
that.  (It would be interesting code...)

**Second best** would be to get card images from GOBLINKO.  I haven't
asked yet.

**Distant third best** is to scan the rest of the cards.  *Ugh.*

Here's how to use the GIMP plugin to process cards.  Put
`skill_to_android.py` in your GIMP plugins directory (mine is
`~/.gimp-2.8/plug-ins/`).  Start up the GIMP, and set your rectangle
select tool to use a fixed 900x1400 selection.  Then, scan some cards
at 600 DPI.  For each card:

- select it using the rectangle selection tool
- right click; run the "Skill to Android" plugin
- enter the skill's skill ID (whatever you added to `skill.json`;
  probably just
- if it's a Mastery instead of a Skill, check the "Is Mastery" box

Scanning 10 cards at a time, I can do both sides in 15 minutes.

Here's how I got the few test cards images (this stuff is all in The
GIMP):
- Scan at 600dpi
- Attempt to smooth out the halftone by Filters -> Blur -> Selective
  Gaussian Blur, Blur Radius 10.0, Max. Delta 80
- Crop to 900 x 1400
- Resize to 360 x 560, save in drawable-xhdpi
- Resize to 270 x 420, save in drawable-hdpi
- Resize to 180 x 280, save in drawble-mdpi
- Resize to 135 x 210, save in drawable-ldpi

(If I were *cool,* I would've written a Script-Fu script to do all
that.)

For the image file name, use "skill_" + the skill's ID from `skill.json`.

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

### TO DO

uhh, finish this file...

One thing to _maybe_ add later is the ability to have the decks of cards
managed by the app during play.  So, instead of spreading out

add support for NFC or Wi-Fi Direct, so that if everyone at the table
has the app, one person can send everyone else the saved game, and they
can set up their own characters.

currently, data files (lists of cards, adventurers, etc.) are read from
CLASSPATH, because that was easy.  _Technically_ what should happen is,
the first time the app starts up, it copies those files into the
device's public storage area, and from then on, uses those copies.

also, we do some file I/O on the UI thread which we probably shouldn't.


---

1. Start a new campaign.  Hit a "new campaign" button; enter the name of
   the campaign; start saving the campaign log.  It gets saved
   automatically.  Maybe you also save the adventurers; maybe you also
   save the deck draw/discard states.
1. Or, save a campaign.  Hit a "save campaign" button; choose the name
   of the existing campaign, or hit "new campaign"; it brings up the
   campaign record sheet screen, either blank or filled in with the
   selected campaign's values.
1. Set up an existing campaign.

### Tracking cards

There are three ways to track which cards were in the draw vs.
discard piles in your campaigns.

1. If you're not going to be playing _concurrent_ campaigns (that is, if
   you're going to play one campaign to completion before starting the
   next), there's no need to track the decks: simply put the cards back
   in the box in a way which preserves their state (Highlands Monster
   draw pile face down, Highlands Monster discard pile face up,
   Highlands Encounter draw pile face down, Highlands Encounter discard
   pile face up, Badlands Monster draw pile face down, etc.)  In
   Settings, turn off card tracking.
1. If you _are_ going to have concurrent campaigns, then enable card
   tracking in Settings; then, during cleanup, run through the list of
   cards in each deck's discard pile, recording those names.  (If one
   person reads off the names in the discard pile while someone else
   finds in the alphabetically sorted list in the app, it shouldn't take
   too long.)  Then, during setup, the app will tell you which cards go
   in the discard, etc.; pull those into the discard pile, and shuffle
   the draw pile.
1. Or, another way to handle concurrent campaigns is to let the app
   handle the card draws: instead of drawing cards, have the app tell
   you which Danger & Encounter & Monster cards were drawn.  (You
   probably want to have your _real_ decks of monster cards sorted
   alphabetically so that you can pull the cards and put them on the
   table for assigning targets etc.)  During cleanup, the app already
   knows which cards are in the draw pile vs. discard; just sort your
   cards (or not, if you prefer).




=====

- start screen
  - button for character stuff (or, one button to create new character,
    another for load existing character?)
  - button for campaign stuff (or, one button to save a new campaign,
    another for setting up saved campaign?)
  - "about" link
  - settings button/link

- choose character activity
  -

- track character activity
  - all the stuff on the normal sheet
  - options to bring up flavor text etc.
  - optional log of changes


- settings
  - remember removed cards?
    this is if you're going to have multiple campaigns going at once, and you
    want to preserve your discard & draw decks, and remember which are out of
    the game.  Unnecessary if you're only playing one campaign at a time.
  - developer mode - shows unfinished crap
  - sound effects
    - "ouch!" "now I'm mad!" etc. when taking wounds

OOH, you could have the app handle the decks of cards!

- edit deck activity
  - run through the list of cards, saying which are in the draw deck, which
    are in the discard, and which are out of the game
