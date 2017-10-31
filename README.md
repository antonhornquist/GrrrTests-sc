# GrrrTests-sc
Unit tests for [Grrr-sc](http://github.com/antonhornquist/Grrr-sc)

## Running tests

Evaluate the following in SuperCollider:

``` supercollider
GRControllerTests.runAllTests;
GRViewTests.runAllTests;
GRContainerViewTests.runAllTests;
GRTopViewTests.runAllTests;
GRButtonTests.runAllTests;
GRToggleTests.runAllTests;
GRKeyboardTests.runAllTests;
GRMultiButtonViewTests.runAllTests;
GRMultiToggleViewTests.runAllTests;
```

## Requirements

GrrrTest-sc requires [Grrr-sc](http://github.com/antonhornquist/Grrr-sc) and the [Test-sc](http://github.com/antonhornquist/Test-sc) library. This code has been developed and tested in SuperCollider 3.8.0.

## Installation

Install the [Grrr-sc](http://github.com/antonhornquist/Grrr-sc) and [Test-sc](http://github.com/antonhornquist/Test-sc) dependencies.

Copy the GrrrTests-sc folder to the user-specific or system-wide extension directory. Recompile the SuperCollider class library.

The user-specific extension directory may be retrieved by evaluating Platform.userExtensionDir in SuperCollider, the system-wide by evaluating Platform.systemExtensionDir.

