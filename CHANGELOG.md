# Changelog

All notable changes to this project will be documented in this file.  The
format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## Unreleased
#### Added
#### Changed


## [1.4.0] (Android versionCode 22) - 2024-03-14
#### Added
- This adds support for the new **Malingerers** character expansion!
- Add Stephan's CardRenderer stuff for
  [issue #9](https://github.com/kuhrusty/MorbadScorepad/issues/9),
  although I'm not properly loading selected skills yet (and the HTML is
  displaying both skill + mastery, etc.); this is just a starting point
  for goofing with.  To see this, turn on developer options & enable
  Skill List -> Use WebView. **NO** this was originally done 4+ years ago, and
  broken in the SDK 33 changes, and it looks like I never checked in a couple
  of the layout files!?

#### Changed
- Update to Android SDK 33


## [1.3.5] (Android versionCode 20) - 2019-06-29
#### Added
- In the Skill List activity, as long as we're letting people highlight
  skills, let's add a second color.


## [1.3.4] (Android versionCode 18) - 2019-06-27
#### Added
- In the Skill List activity, long-clicking on a skill name will let you
  highlight it (if it's a skill you're considering taking) or hide it
  (if it's a skill you know you *won't* take, or you've already taken)
  ([issue #30](https://github.com/kuhrusty/MorbadScorepad/issues/30)).


## [1.3.3] (Android versionCode 16) - 2019-06-24
#### Changed
- In the Skill List activity, fix a bug where Dilettante skills weren't
  being filtered by XP
  ([issue #29](https://github.com/kuhrusty/MorbadScorepad/issues/29)).
- Update to Android SDK 28
  ([issue #28](https://github.com/kuhrusty/MorbadScorepad/issues/28),
  weirdly enough).


## [1.3.2] (Android versionCode 14) - 2019-06-23
#### Added
- In the Skill List activity, add support for displaying skills allowed
  by the Dilettante skill
  ([issue #23](https://github.com/kuhrusty/MorbadScorepad/issues/23)).


## [1.3.1] (Android versionCode 12) - 2019-06-19
#### Changed
- Remove copyright warning, as we now have permission from GOBLINKO!
- Fiddle with expansion order (put Hand of Doom KS Exclusives first, as
  it was, chronologically).
- Replace PNG card scans with JPEGs to reduce the size of the APK; see
  [issue #22](https://github.com/kuhrusty/MorbadScorepad/issues/22) for
  way more blather about this than you want, unless you're a fan of
  autodoofusication.


## [1.3] (Android versionCode 10) - 2019-06-10
#### Added
- In the Danger Deck activity, add support for removing cards from the
  deck, needed for some missions' rewards
  ([issue #19](https://github.com/kuhrusty/MorbadScorepad/issues/19)).
- In the Skill List activity, add support for filtering skills by the
  amount of XP you have
  ([issue #18](https://github.com/kuhrusty/MorbadScorepad/issues/18)).
  As part of this, fiddle with the layout in landscape orientation.

#### Changed
- In the Danger Deck activity, fiddle with the content & position of the
  string showing how many cards are left in the draw pile.
- In the Skill List activity, add an icon indicating that the list of
  adventurers is something you can actually click on.  This trivial task
  took me about 6 hours; see the angry comments in the
  `SpinnerAlternative` class.
- In the Danger Deck activity, reload the deck from file when we think
  the selected set of expansions might have changed
  ([issue #20](https://github.com/kuhrusty/MorbadScorepad/issues/20)).
- In the Danger Deck activity, move button labels from the layout XML
  files to `strings.xml` (not sure why I didn't put them there in the
  first place).


## [1.2] (Android versionCode 8) - 2019-05-23
#### Added
- This adds support for the **Mean Streets** expansion!  New
  adventurers, new skills, new missions, new danger cards!


## [1.1.1] (Android versionCode 6) - 2019-02-03
#### Changed
- In the Danger Deck activity, improve handling of sounds; listen for
  completion on the MediaPlayer and clean it up as soon as it completes
  so that the next tap causes us to play the sound again instead of
  cleaning up the already-completed player.
- Move the Danger Deck activity's background images from `drawable` to
  `drawable-nodpi` for
  ([issue #16](https://github.com/kuhrusty/MorbadScorepad/issues/16)).


## [1.1] (Android versionCode 4) - 2019-01-18
#### Added
- **The Danger Deck activity is no longer developer-only.**
- In the Danger Deck activity, add the rest of the readings from the
  "Rusty" sound set.
- In the Danger Deck activity, store the deck state between runs
  ([issue #12](https://github.com/kuhrusty/MorbadScorepad/issues/12)).
- In the Danger Deck activity, tapping a card causes its audio to play.
- In the Danger Deck activity, add a "SHUFFLE ALL" button.
- In the Danger Deck activity, handle "RESHUFFLE DANGER DECK" Danger cards
  ([issue #11](https://github.com/kuhrusty/MorbadScorepad/issues/11)).
- In the Danger Deck activity, trim the Danger deck undo log after every
  shuffle, rather than letting it grow without limit
  ([issue #10](https://github.com/kuhrusty/MorbadScorepad/issues/10)).

#### Changed
- In the Skill List activity, now restoring the selected adventurer &
  skill at startup
  ([issue #8](https://github.com/kuhrusty/MorbadScorepad/issues/8)).
- Fix a bug with undoing Danger deck card draws where, if you drew a
  card, did an undo, then shuffled, then undid again, you wound up at
  the post-draw state (which you'd already undone & not redone) rather
  than the pre-draw state you were at right before shuffling.
- In the Danger Deck activity, fiddle with the layout of some of the
  buttons.

## [1.0.1] (Android versionCode 2) - 2018-12-31
#### Changed
- Fix Angel of Death & Infamous Butcher stats (thanks Stephan!).
