JACOB (Java-COM bridge) 
* Source was hosted on Sourceforge for for over a decade http://sourceforge.net/project/jacob-project
* Source moved to https://github.com/freemansoft/jacob-project

## JACOB 1.20 (tentative)
### What's New
*   Upgraded from VS2015 to VS 2019
### Tracked Changes
| | |
|-|-|
|Bugs              | |
|n/a               | none |
|Patches           | |
| n/a              |	none |
| Feature Requests | |
| 48               |Update to VS2019 Community and Windows 10 libs |
| Merge Request    | |
| 1                | Support VT_DATE getting from SafeArray |

## Latest Release ##
See [ReleaseNotes](docs/ReleaseNotes.md) for a full history

JACOB 1.19
### What's New
* Upgraded from Java 6 to Java 8 compilation
* Upgraded from junit 3.8.1 to 4.12
* Migrated from CVS to GIT using sourceforge migration instructions https://sourceforge.net/p/forge/documentation/CVS/
### Tracked Changes
| | |
|-|-|
|Bugs              | |
| 132	           | 32 bit ponters not convertd to 64 bit |
| 130              | Name value incorrect in Mainfest.MF affecting tamper check|
|Patches           | |
| n/a              |	none |
| Feature Requests | |
| n/a              |	none |

## Documentation ##
You can find additional information in the [docs](docs) folder
* Information about what's new in this release can be found in [ReleaseNotes](docs/ReleaseNotes.md)
* Instructions on building this project can be found in [BuildingJacobFromSource](docs/BuildingJacobFromSource.md)
Detailed instructions on creating a build configuration file are in build.xml

## Usage ## 
_see docs_

Put the appropriate DLL for your platform into your runtime library path.
jacob for 32 bit windows is located in /x86.

## TODO ##
There is no good usage guide at this time.