## Interesting stuff in the code

- Parsing expressions like "MRL <5 or STR 6" into a tree of operations
  which is applied to Adventurers to determine whether a given skill is
  applicable (mostly in the unfortunately named `Req` class)
- Loading Drawables from names generated at runtime
  (`SkillListActivity.loadImage()`)
- Loading audio files from names generated at runtime
  (`DangerDeckActivity.playCardSound()`)
- Dynamically generating preferences based on data read from file
  (`SettingsActivity.updateExpansionPrefs()`)
- Working around my inability to get the Android `Spinner` class to do
  what I want (`SpinnerAlternative`) 

### Unit test stuff

- Dropping in a no-op `Log` in non-instrumented tests, copied from
  [here](https://stackoverflow.com/questions/36787449/how-to-mock-method-e-in-log)
- MockParcel stuff originally copied from
  [here](https://gist.github.com/Sloy/d59a36e6c51214d0b131) for
  confirming that Parcelable objects survive being written to & from a
  Parcel (`MockParcel`).  The code for this is
  [here](https://github.com/kuhrusty/ParcelOrGSON);
  `ExpansionTest.testParcel()` is one example of it being used in a unit
  test.
