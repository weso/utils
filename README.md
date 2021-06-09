# Utils

Scala utilities

[![Build Status](https://github.com/weso/utils/actions/workflows/ci.yml/badge.svg)](https://github.com/weso/utils/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/weso/utils/branch/master/graph/badge.svg)](https://codecov.io/gh/weso/utils)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/es.weso/utils_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/es.weso/utils_2.13)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/92aef2157e844f48bca96e44b38bb0a7)](https://www.codacy.com/gh/weso/utils?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=weso/utils&amp;utm_campaign=Badge_Grade)

## Introduction

This project contains some common utilities in Scala.

## Installation and compilation

The project uses [sbt](http://www.scala-sbt.org/) for compilation as well as Java 1.8.

* `sbt test` compiles and runs the tests

## Author & contributors

* Author: [Jose Emilio Labra Gayo](http://labra.weso.es)

Contributors:

* [Eric Prud'hommeaux](https://www.w3.org/People/Eric/)
* [Bogdan Roman](https://github.com/bogdanromanx)
* [Toni Cebrían](http://www.tonicebrian.com/)
* [Andrew Berezovskyi](https://github.com/berezovskyi)

## Adopters

* [RDFShape](http://rdfshape.weso.es): An online demo powered by this library.
* [Wikishape](http://wikishape.weso.es): An online demo powered by this library for Wikidata.

## Contribution

Contributions are greatly appreciated.
Please fork this repository and open a
pull request to add more features or [submit issues](https://github.com/weso/utils/issues)

## Publishing to OSS-Sonatype

This project uses [the sbt ci release](https://github.com/olafurpg/sbt-ci-release) plugin for publishing to [OSS Sonatype](https://oss.sonatype.org/).

##### SNAPSHOT Releases
Open a PR and merge it to watch the CI release a -SNAPSHOT version

##### Full Library Releases
1. Push a tag and watch the CI do a regular release
2. `git tag -a v0.1.0 -m "v0.1.0"`
3. `git push origin v0.1.0`
_Note that the tag version MUST start with v._
