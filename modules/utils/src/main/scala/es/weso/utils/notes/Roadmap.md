# Roadmap

We think that this whole repo should dissapear and be replaced for single purpose repos/components. In order to do that, we will include a list of the modules and potential repos:

## Current modules

- `typing`: Contains a generic `Typing` type class, it can be extracted to its own repo
- `validating`: contains a generic `Checker` trait that has been implemented in Cats. It was originally implemented using the `Eff` library.
- `utilsTest`: this module includes JsonMatchers which are no longer used
- `testSuite`: this module could be extracted as a single repo


The `utils` folder contains the following packages:
- `json`: contains several utilities to work with Json entities in Scala like `DecoderUtils`, `JsonCompare`, `JsonTest` and `JsonTwirl`
- `EitherUtils` contains two helper methods to work with Either values, `sequence` and `takeSingle`
- `FileUtils` contains some helper methods to work with files: `getContents`, `getFileFromFolderWithExt`, `writeFile`, `formatLines`, etc.
 

