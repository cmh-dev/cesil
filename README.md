# CESIL

[![Tests](https://github.com/cmh-dev/cesil/actions/workflows/tests.yml/badge.svg)](https://github.com/cmh-dev/cesil/actions/workflows/tests.yml)
[![Build and release](https://github.com/cmh-dev/cesil/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/cmh-dev/cesil/actions/workflows/build-and-release.yml)

CESIL (Computer Education in Schools Instruction Language) was developed by ICL to be used to teach computer programming in UK secondary schools.

This project is an attempt to create, in Kotlin, an interpreter for CESIL providing both a web application and command line application.

Both applications can be downloaded from the latest [release](https://github.com/cmh-dev/cesil/releases).

### Usage

To run both applications you will need a JVM version 11+ installed.

###### Web Application

Unzip the release (cesil-web.zip) into a location of your choice. From the command line navigate to the directory containing the extracted files and run either the UNIX/LINUX shell script or DOS batch file (depending on your platform).

Example:

`./cesilweb`

The web application will run on port 8080 and can be accessed from a browser by going to http://localhost:8080

###### Command Line Application

Unzip the release (cesil-cli.zip) into a location of your choice. From the command line navigate to the directory containing the extracted files and run either the UNIX/LINUX shell script or DOS batch file (depending on your platform) passing a file containing your CESIL source code as an argument.

Example:

`./cesil test-cesil-program.cesil`