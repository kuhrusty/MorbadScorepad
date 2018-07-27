# Morbad Scorepad

This is a support app for the massively awesome **DUNGEON DEGENERATES:
HAND OF DOOM** boardgame; if you don't know what that is, you're in the
wrong place.  *(Like, in **life**.)*

The only completely functional part of this app is the skill browser,
which shows the fronts & backs of the available skills for a given
adventurer.  (Originally, I started writing this to record games in
progress, but it turns out that taking photos of the board & characters
is pretty easy.)

### TO CHECK OUT THE CODE

This **will not compile** in its current state.

The issue is, this uses a massive amount of content (images, card text)
which is owned by Goblinko, and I haven't exactly received *permission*
to add all that to this repository.  So, currently this repository
contains all the stuff which *isn't* Goblinko's, but the instructions
below will proceed as if it's complete.

In Android Studio 3.1.3:

1. File -> New -> Project from Version Control -> GitHub

2. Clone Repository:
   - Repository URL: https://github.com/kuhrusty/MorbadScorepad.git
   - Parent Directory: (whatever you want; probably
     /home/.../AndroidStudioProjects)
   - Directory Name: (whatever you want; probably MorbadScorepad)

   That should check the stuff out and start a Gradle build.  (If you
   get errors about missing .iml files, ignore them--do *not* remove the
   modules it's talking about.)

3. Hit the "Sync Project with Gradle Files" button in the toolbar.  This
   should generate the .iml files it was complaining about.

### TO BUILD & RUN IT

In Android Studio, there may be a rectangular button in the toolbar
below the menu bar which says "app" and which has a green or gray
triangle to the right of it; if you click on that triangle, you might
get a window which lets you choose between your connected device, or an
emulated device (that is, running on a virtual/pretend phone in Android
Studio, not your real phone).  If your phone doesn't show up in that
window, something is wrong.  Choose your phone (plugged in as a USB
device, connected as a camera or a media device... probably camera) and
wait for the "Ask AutoDad 1.0!" screen to show up.

-----

From here down are older notes of dubious applicability to anyone but
myself.

### Tasks

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




-----

- start screen
  - button for character stuff (or, one button to create new character,
    another for load existing character?)
  - button for campaign stuff (or, one button to save a new campaign,
    another for setting up saved campaign?)
  - "about" link
  - settings button/link

- choose character activity

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
