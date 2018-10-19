# EB solys

EB solys is

* a development tool for collecting, aggregating and correlating data and its visualization. Live and post-mortem.
* a framework to be customized and extended for the best possible integration with the project it's used in.
* a method to identify and localize functional and non-functional defects in an early phase for reducing the efforts spent for testing and bug fixing in later phases.
* a sophisticated approach to analyzing and validating complex automotive software systems from top to bottom.

## High level architecture

EB solys is capable of visualizing data from various sources.
It can be attached to the EB solys Target Agent, which you can find here https://github.com/Elektrobit/eb-solys-target-agent
It can also connect to a DLT daemon via TCP connection.
And it is possible to import various trace file formats.

EB solys is based on Eclipse RCP and can be used in GUI mode or CLI mode.

## Build

### Prerequisites

* Latest eclipse for plugin development installed
* Java 8 or higher
* Maven 3.3.1

### Build GUI Executable

```
cd src/com.elektrobit.ebrace.releng.ui.ecl.aggregator
mvn clean verify
```

## Contact

EB solys development is supported by systemticks GmbH.
For further questions on how to build, contribute or use, please reach out to

eb-solys@systemticks.de

## License and Copyright

Copyright (C) 2018 Elektrobit Automotive GmbH

This program and the accompanying materials are made
available under the terms of the Eclipse Public License 2.0
which is available at https://www.eclipse.org/legal/epl-2.0/
