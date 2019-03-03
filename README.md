# vending-machine
Component that handles some user and supplier operations of a Vending Machine.

## Remarks
* Supplier operations: Defined in interface VendingMachineSupplierOperations.
* User operations: Defined in interface VendingMachineUserOperations.
* VendingMachineApi class - Contains the implementation of the user and supplier operations.
* VendingMachineApiIntegrationTest class - Allows to test some scenarios.

## Getting started

### Import into IDE
This is a Gradle project (build.gralde.kts)
* IntelliJ - https://www.jetbrains.com/help/idea/gradle.html#gradle_import
* Eclipse - https://www.vogella.com/tutorials/EclipseGradle/article.html#import-an-existing-gradle-project

### Build project

Execute the following gradle task:
```
gradle build
```

### Running the tests

Execute the following gradle task:
```
gradle test
```