# Changelog

All notable changes to this project will be documented in this file.  The
format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## Unreleased
#### Added
#### Changed
- Fiddle with expansion order (put Hand of Doom KS Exclusives first, as
  it was, chronologically).


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
