[![Build Status](https://travis-ci.org/Elektrobit/eb-solys.svg?branch=master)](https://travis-ci.org/Elektrobit/eb-solys)

# EB solys

## Key features and benefits

EB solys is a customizable framework and construction kit for building tools to identify and localize functional and non-functional defects in complex software projects.
It offers functionality for filtering, searching, aggregating  and correlating runtime data across different data sources in a single place.
This enables you to create a joint system understanding and to isolate errors with significantly less workforce and to gain greater insight into the operational activity of your software system. It is applicable for live and post-mortem analysis.

## High level architecture

The EB solys eco-system consists of the target agent for collecting run-time data on the target system and an Eclipse RCP based analysis tool-chain running on the host environment.

![alt text](./resources/eco_system.png "EB solys eco-system")

The target agent is running on the target device(s) and serves as a plug-in framework. The plug-ins are used to acquire any kind of runtime data, such as resource consumption, application logs & traces, interprocess-communiction, I/O, etc.
There is a set of  target agent plug-ins available, but you can implement your own plug-in, that retrieves additional proprietary project specific data. The data is sent to the host in a unified way, specified with protobuf.  

The target agent framework is available as open source project for
* Linux (see https://github.com/Elektrobit/eb-solys-target-agent) and
* Android (see https://github.com/Elektrobit/eb-solys-android-agent).

The EB solys GUI is built on Eclipse RCP and is running on the host machine. It allows for an expressive and comprehensive system analysis by correlating and aggregating the collected runtime data across different data sources in a single place.
The host application provides extensible means for data analysis.
It is equipped with a rich set of visualization tools like charts, tables and a communication graph out of the box and can be extended through custom HTML visualizations.

## Extensibility and Adaptability

EB solys is designed to be adaptable, customizable and extensible to the concrete project specific environment:

* Use the **built-in Script Engine** to add new functionality by accessing the EB solys raw data and resources
* Hook in your own **Decoder**, that transforms arbitrary non-primitive data (e.g. binary payload) into structured readable text
* Provide your own **Importer** for any kind of logs, which are not created originally by the target agent
* Provide your own **Communicator** to be able to connect to other trace providers than EB solys target agent

## Scripting

### Purpose

EB solys comes with a powerful built-in scripting engine, that allows the user to interact with the collected runtime data and the UI resources. This enables the user to aggregate and correlate data from different sources programmatically and visualize them in tables, charts or in arbitrary html views in a very easy and straight-forward manner.

### The Language (Xtend)

As built-in scripting language Xtend was chosen, due to following reasons:

* Xtend is using the Java type-system and hence allows a seamless integration into an Eclipse RCP application, what EB solys is.
* Xtend code will be generated into Java source code on-the-fly in the background, so that no additional interpreter is needed at runtime.
* Xtend is a hybrid language that combines object-oriented and functional programming at the same time, which allows the implementation of very expressive and concise scripts.
* You have the full power of Java with some syntactical sugar on top.
* Eclipse editor functionality like content-assist, code completion, referencing is the same as for Java

If you are not yet familiar with Xtend, but with Java and one of its JVM-based scripting langauges such as Groovy, Scala or Clojure you should be very fast in learning the principles of Xtend.

Visit https://eclipse.org/xtend for details, tutorials and examples.

### Example

```xtend
@Execute(context=ExecutionContext.GLOBAL, description="")
def createMarkerForHighCpuLoad() {
    getChannel("cpu.system").events.filter[value as Double > 50].
        forEach[createTimemarker("High CPU Load")]
}
```

## Setup development environment

### Prerequisites

*currently this is only tested on Windows*

* Install Oracle Java SDK 8 from https://www.oracle.com/technetwork/java/javase/downloads/index.html
* Install Maven >= 3.3.1 from https://maven.apache.org/download.cgi
* Download Eclipse installer for your platform from https://www.eclipse.org/downloads/
* Run Eclipse installer
    - change to advanced mode
    - select Eclipse for RCP and RAP Developers > Next
    - add project setup file via '+'
    - select Github Projects
    - paste https://raw.githubusercontent.com/Elektrobit/eb-solys/master/devenv/eb-solys.setup > OK
    - select EB Solys entry under GithubProjects > <User> > Next
    - adapt installation folder if required ('Show all variables') > Next > Finish
    - During installation accept unsigned content and licenses as requested
    - Eclipse will start automatically after a while and will run some setup tasks (you can monitor the progress by observing the Eclipse task bar or clicking on the icons in the bottom right corner)
    - Click Finish to restart Eclipse
    - There is more installation to be done ;-) again observe the Eclipse task bar or click on the icons
    - If you encounter a build problem, click OK and trigger a clean build (Project > Clean)

## Starting EB solys

* open /com.elektrobit.ebrace.releng.ui.ecl.product/com.elektrobit.ebrace.releng.ui.ecl.product
* click Launch an Eclipse application

## Maven CLI build of GUI Executable

```
cd src/com.elektrobit.ebrace.releng.ui.ecl.aggregator
mvn clean verify
```

## Running under Linux

You need to install openjfx otherwise the built-in HTML and javascript features will not work properly.
```
sudo apt install openjfx
```

## Contact

EB solys development is supported by systemticks GmbH

www.systemticks.de

For further questions on how to build, contribute or use, please reach out to

eb-solys@systemticks.de

## License and Copyright

Copyright (C) 2018 Elektrobit Automotive GmbH

This program and the accompanying materials are made
available under the terms of the Eclipse Public License 2.0
which is available at https://www.eclipse.org/legal/epl-2.0/
