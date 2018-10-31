# EB solys

EB solys is

* a development tool for collecting, aggregating and correlating data and its visualization. Live and post-mortem.
* a framework to be customized and extended for the best possible integration with the project it's used in.
* a method to identify and localize functional and non-functional defects in an early phase for reducing the efforts spent for testing and bug fixing in later phases.
* a sophisticated approach to analyzing and validating complex automotive software systems from top to bottom.

## High level architecture

The EB solys eco-system consists of the target agent for collecting run-time data on the target system and an Eclipse RCP based analysis tool-chain running on the host environment.

![alt text](./resources/eco_system.png "EB solys eco-system")

The target agent is running on the target device(s) and serves as a plug-in framework. The plug-ins are used to acquire any kind of runtime data, such as resource consumption, application logs & traces, interprocess-communiction, I/O, etc. 
There is a set of  target agent plug-ins available, but you can implement your own plug-in, that retrieves additional proprietary project specific data. The data is sent to the host in a unified way, specified with protobuf.  

The target agent framework is available as open source project for 
* Linux (see https://github.com/Elektrobit/eb-solys-target-agent) and 
* Android (see https://github.com/Elektrobit/eb-solys-android-agent).

The EB solys GUI is built on Eclipse RCP and is running on the host machine and allows an expressive and comprehensive system analysis by correlating and aggregating the collected runtime data across different data sources in a single place.
The host application provides extensible means for data analysis. 
It is equipped with a rich set of visualizations tools like charts, tables and a communication graph out of the box and can be extended through custom HTML visualizations. 

## Extensibility and Adaptability

EB solys is designed with the capability to be adaptable, customizable and extensible to the concrete project specific environment:

* Use the **built-in Script Engine** to add new functionality by accessing the EB solys raw data and resources
* Hook in your own **Decoder**, that transforms arbitrary non-primitive data (e.g. binary payload) into structured readable text
* Provide your own **Importer** for any kind of logs, which are not created originally by the target agent
* Provide your own **Communicator** to be able to connect to other trace providers than EB solys target agent

## Scripting

### Purpose

EB solys comes with a powerful built-in scripting engine, that allows the user to interact with the collected runtime data and the UI resources. This enables the user to aggregate and correlate data from different sources programmatically and visualize them in tables, charts or in arbitrary html views in a very easy and straight-forward manner.

### The Language (Xtend)

As built-in scripting language Xtend was chosen, due to following reasons:

* Xtend is using the Java type-system and hence allows a seamless integration into an Eclipse RCP application, what {solys} is.
* Xtend code will be generated into Java source code on-the-fly in the background, so that no additional interpreter is needed at runtime.
* Xtend is a hybrid language that combines object-oriented and functional programming at the same time, which allows the implementation of very expressive and concise scripts.
* You have the full power of Java with some syntactical sugar on top.
* Eclipse editor functionality like content-assist, code completion, referencing is the same as for Java

If you are not yet familiar with Xtend, but with Java and one of its JVM-based scripting langauges such as Groovy, Scala or Clojure you should be very fast in learning the principles of Xtend.

Visit https://eclipse.org/xtend/[Xtend] for details, tutorials and examples.

### Example

```xtend
@Execute(context=ExecutionContext.GLOBAL, description="")
def createMarkerForHighCpuLoad() {
    getChannel("cpu.system").events.filter[value as Double > 50].
        forEach[createTimemarker("High CPU Load")]
}
```

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
