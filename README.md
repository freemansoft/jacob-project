# Java COM Bridge

This document reflects the _next_ release _1.21_ which is moving from Java 8 to Java 17.

Jacob is a Java library that lets Java applications communicate with Microsoft Windows DLLs or COM libraries. It does this through the use of a custom DLL that the Jacob Java classes communicate with via JNI. The Java library and dll isolate the Java developer from the underlying windows libraries so that the Java developer does not have to write custom JNI code.Jacob is not used for creating ActiveX plugins or other modules that live inside of Microsoft Windows applications.

## Repositories

JACOB (Java-COM bridge)

* Source was hosted on Sourceforge for for over a decade on the [jacob-project Sourceforge Repository](http://sourceforge.net/project/jacob-project)
* The Discussion forums are still up on [Sourceforge jacob-project Discussions](https://sourceforge.net/p/jacob-project/discussion)
* The root repository for source is now located [On GitHub](https://github.com/freemansoft/jacob-project)

## Documentation

You can find additional information in the [docs](docs) folder

* [Using Jacob](docs/UsingJacob.md)
* [ReleaseNotes](docs/ReleaseNotes.md)
* [Building Jacob From Source](docs/BuildingJacobFromSource.md)
  * Detailed instructions on creating a [build configuration file are in build.xml](build.xml)

## Usage

* [Using Jacob](docs/UsingJacob.md)

Put the appropriate DLL for your platform into your runtime library path.

* jacob for 32 bit windows is located in /x86.
* jacob for 64 bit windows is located in /64.

### TODO

There is no good usage guide at this time.

## Release Notes

See [ReleaseNotes](docs/ReleaseNotes.md) for a full history.

### Jacob 1.21 (latest)

#### What's New in 1.21

* Upgraded from VS 2019 to VS 2022 - can use Community
* Formatting done using VS Code - developed using VSCode ANT and Java Extensions

#### Tracked Changes 1.21

| Item                                                     | Description                                     |
| -------------------------------------------------------- | ----------------------------------------------- |
| **Bugs**                                                 |                                                 |
| <https://github.com/freemansoft/jacob-project/issues/35> | Add Iterable to EnumVariant                     |
| <https://github.com/freemansoft/jacob-project/issues/36> | Memory Leak                                     |
| <https://github.com/freemansoft/jacob-project/issues/38> | Implement Comparable on Currency                |
| <https://github.com/freemansoft/jacob-project/issues/40> | Incorrect delete in Dispatch JNI Invoke()       |
| <https://github.com/freemansoft/jacob-project/issues/42> | ArrayIndexOutOfBounds SafeArray                 |
| <https://github.com/freemansoft/jacob-project/issues/43> | Memory Leaks in DispatchEvents.cpp              |
| <https://github.com/freemansoft/jacob-project/issues/45> | SaveArray init0                                 |
| <https://github.com/freemansoft/jacob-project/issues/48> | Incorrect multi dimensional array element count |
|                                                          |                                                 |
| **Patches**                                              |                                                 |
| none                                                     | none                                            |
|                                                          |                                                 |
| **Feature Requests**                                     |                                                 |
| none                                                     | none                                            |

