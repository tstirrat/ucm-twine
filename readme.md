(This README is a work in progress)

Twine for Oracle WebCenter Content/UCM (ALPHA)
=============================================

An annotation based dependency injection Java framework for Oracle UCM.

It allows you to write services, Idoc script extensions, filters and (eventually) scheduled events with [POJOs](http://en.wikipedia.org/wiki/Plain_Old_Java_Object "Plain Old Java Objects") and it handles the setup, definition and parameter checking for you.

Furthermore Twine allows you to unit test your code much easier!

Quick Start
================

Twine can be used as a library in your own component, or if you plan to have a few Twine based components installed it can be used as a single shared library component.

Use as an addition to the TwineLib component
--------------------------------------------

1. Install the TwineLib component and enable it.
1. Follow the next guide from step 4.

Including in your own component
-------------------------------

1. Get/build the Twine jar (preferably with Maven)
2. Create a UCM component: MyComponent
3. Include twine in your MyComponent.hda classpath:

        classpath=$COMPONENT_DIR/lib/mycomponent.jar;$COMPONENT_DIR/lib/ucm-twine-0.1-SNAPSHOT.jar;

4. Include a .properties file in your jar to define your services and script extension classes. If you use maven and place the file in `src/main/resources` it will be at the root of your jar.

        # <jar root>/mycomponent.properties
        ucm.service.ExampleServicePackage=org.component.example.ExampleServicePackage

5. Add Twine's Bootstrapper to the **Filters** resultset of your MyComponent.hda file, using the .properties file as the filter's parameter. If the .properties file is not at the root of your jar you may need to add relative path information here.

        @ResultSet Filters
        4
        type
        location
        parameter
        loadOrder
        extraAfterConfigInit
        org.stirrat.twine.Bootstrapper
        mycomponent.properties
        1000
        @end

6. Create your class and annotate a method:

        ```java
        package org.component.my;

        public class ExampleServicePackage {

            @ServiceMethod(name = "EXAMPLE_SERVICE")
            public void exampleService(@Binder(name = "param1") String param1) {
                SystemUtils.trace("system", "param1 was " + param1);
            }
        }
        ```

7. Build mycomponent.jar

From here you can edit mycomponent.properties each time you add a new class. However, if you are adding a new method to an existing class, there is no more setup needed.

Examples
--------

A service method:

```java
public class ExampleServicePackage {

    @ServiceMethod(name = "EXAMPLE_SERVICE")
    public void exampleService(@Binder(name = "param1") String param1) {
        SystemUtils.trace("system", "param1 was " + param1);
    }
}
```

An Idoc script function and variable:

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String strUppercase(String value) {
        return value.toUpperCase();
    }

    @IdocVariable
    public long TimeInMillis() {
        return System.currentTimeMillis();
    }
}
```

A filter:

```java
public class ExampleFilterPackage {

    @Filter(event = "updateExtendedAttributes")
    public void exampleFilter(@Binder(name = "dID") String dID) {
        SystemUtils.trace("system", "dID was " + dID);
    }
}
```

Properties file syntax
----------------------

The .properties file should declare each class that you wish to load with the appropriate key.

Filters: `ucm.filter.uniqueKey1=fully.qualified.class.Name`

Services: `ucm.service.uniqueKey2=fully.qualified.class.Name2`

Idoc Script: `ucm.idocscript.uniqueKey3=fully.qualified.class.Name3`

Make sure to define each key before the = sign with a different name so that each class is picked up.

That's all you need! Now you can begin writing service, filter or Idoc script definitions into your classes.

Documentation
=============

Dependency injection basics
---------------------------

Declare the object you need as a parameter to your method and Twine will do its best to provide you with the object.

The following types can be specified so far:

- UserData
- PageMerger
- Workspace
- DataBinder
- HttpImplementor
- ServiceRequestImplementor
- ExecutionContext
- Service

These will probably be implemented:

- SecurityImplementor
- FileStoreProvider
- ServiceData
- Action (current action)
- OutputStream
- ... most other members of the Service object

Define your dependencies anywhere in the method signature and they will be injected for you. i.e. you can define a method with injected parameters in between `@Binder` parameters without issue:
    ```java
    @ServiceMethod(name = "TEST_SVC")
    public void serviceA(@Binder(name = "param1") Long param1, UserData u, DataBinder b, @Binder(name = "param2") String param2 ) {

    }
    ```

Specifying binder parameters
----------------------------

The `@Binder` annotation provides binder parameter checking for filters, services and Idoc scripts.

### Annotation options

**name** The parameter name in the binder or the result set name.

**required** Whether the parameter is required. Default: true.

**missing** If required is set to false, this is the value that will be given. (not implemented yet)

If the required parameter is set to `true`, a `DataException` will be thrown if the binder value is null or an empty string. If the required parameter is left as false - its default state - the parameter will allow an empty or null value.

You can also use `@Binder` with a `DataResultSet` or `Date` type.


Example:

```java
public void serviceA(@Binder(name = "SSContributor", required = false) Boolean ssContributor) {
    if (ssContributor == null) {
        SystemUtils.trace("system", "SSContributor was not supplied");
    }
}

public void rsExists(@Binder(name = "rs", required = false) DataResultSet rs) {
    if (rs == null) {
        SystemUtils.trace("system", "rs was not supplied");
    }
}
```

Services
--------
Use the @ServiceMethod annotation. Example:

```java
public class ExampleServicePackage {

    @ServiceMethod(name = "EXAMPLE_SERVICE", template = "TPL_EXAMPLE")
    public void exampleService(@Binder(name = "param1") String param1) {
        SystemUtils.trace("system", "param1 was " + param1);
    }
}
```

### Parameters
**name** The service name

**template** The template to render. Unfortunately, templates are still defined in the old ComponentWizard way, for now.

**errorMessage** The error message to return when the service fails

**type** The service type **(unimplemented)**

**accessLevel** This correlates to the ComponentWizard dialog security flags. Accepts a binary bitmask of the following: `ACCESS_READ`, `ACCESS_WRITE`, `ACCESS_DELETE`, `ACCESS_ADMIN`, `ACCESS_GLOBAL`, `ACCESS_SCRIPTABLE`. **(likely to change)**

Defaults to `ACCESS_READ | ACCESS_SCRIPTABLE`. **(also likely to change)**

**subjects** The subjects to notify

Idoc Script Functions and Variables
-----------------------------------

Idoc scripts and variables are defined in a very similar way, using the `@IdocFunction` or `@IdocVariable` annotations.

### Annotation options

**name** Specify a name if it differs from the Java method name. e.g. requiring strange capitalisation.

Parameter and return type coercion is handled for you. i.e returning a Boolean or a boolean will coerce the return value to the correct format (a Long) behind the scenes.

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String strUppercase(String value) {
        return value.toUpperCase();
    }

    // in Idoc: <$ strUppercase("value") $> = VALUE
}
```

IdocVariables do not take any parameters, however you could access all of the injected types or binder values.

```java
public class ExampleScriptPackage {

    @IdocVariable
    public long timeInMillis() {
        return System.currentTimeMillis();
    }

    // in Idoc: <$ TimeInMillis $>
}
```

If you like to keep your method names camel case but want a capitalised variable or function you can specify it with **name**.

```java
public class ExampleScriptPackage {

    @IdocVariable(name = "TimeInMillis")
    public long timeInMillis() {
        return System.currentTimeMillis();
    }

    // in Idoc: <$ TimeInMillis $>
}
```

Method parameters can be mixed with dependency injection types.

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String strHello(ExecutionContext ctx, String value, DataBinder b, UserData u) {
        return value + " " + u.getName() + "!";
    }

    // In Idoc: <$ strHello("Hi") $> = Hi tstirrat!
}
```

Idoc function parameters do not use a `@Binder` annotation, however you can use the annotation to extract from the binder.

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String strHello(String value, @Binder(name = "dUser") String dUser) {
        return value + " " + dUser + "!";
    }

    // In Idoc: <$ strHello("Hi") $> = Hi tstirrat!
}
```

Filters
-------
Use the `@Filter` annotation. Example:

```java
public class ExampleFilterPackage {

    @Filter(event = "validateStandard", loadOrder = 10)
    public void exampleFilter(@Binder(name = "dID") Long dID, UserData u) {
        SystemUtils.trace("system", "The user " + u.getName() + " acted on dID " + dID.toString());
    }
}
```

### Annotation options

**loadOrder** The load order. Default: 100.

**event** The filter name e.g. validateStandard. 

**Note:** At the time of writing, the filter injector runs at _extraAfterConfigInit_. Only events that execute *after* this event will work.

**parameter** The parameter to pass to the filter **(unimplemented)**.


The future
==========

Some planned enhancements.

### Scheduled Events

Scheduled events can sometimes be quite tricky. A lot of boilerplate code is needed to make sure events fire  only onece on clusters, at the right time and with the right frequency.

An annotation based scheduler is planned with a cron like syntax something like this:

```java
public class ExampleScheduledEventPackage {

    @ScheduledEvent(cronFrequency = "* 2 * * *")
    public void clearTempFiles(ExecutionContext ctx) {
        // runs at 2am each day
    }
}
```

### Custom object injection

Multiple `@Binder` annotions for a single method can get quite messy, so a method of injecting composed objects is planned:
```java
public class SignUpForm {

    private String name;

    public String getName() { return name; }

    // ...
}

public class ExampleServicePackage {

    @ServiceMethod(name = "SIGN_UP")
    public void signUp(SignUpForm form) {
        SystemUtils.trace("system", "Registrant name is " + form.getName());
    }
}

// executeService("SIGN_UP") with binder name = "Bob" => "Registrant name is Bob"
```

License
=======

Copyright (c) 2012 Tim Stirrat

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.