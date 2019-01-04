# Changelog

All notable changes to this project will be documented in this file.  The
format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/).

## Unreleased
### Changed
- Fix a bug with undoing Danger deck card draws where, if you drew a
  card, did an undo, then shuffled, then undid again, you wound up at
  the post-draw state (which you'd already undone & not redone) rather
  than the pre-draw state you were at right before shuffling.

## [1.0.1] (Android versionCode 2) - 2018-12-31
### Changed
- Fix Angel of Death & Infamous Butcher stats (thanks Stephan!).
