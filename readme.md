Twine for Oracle WebCenter Content/UCM (Beta)
=============================================

An annotation based Java framework for Oracle UCM.

It allows you to write UCM services, Idoc script extensions and filters using Java [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object "Plain Old Java Object")s. It handles binder value checking so you never need to write that same glue code again.

Why Twine?
----------

### All in one place

Twine puts all the information in the Java file where the functionality is, so you don't need to edit the .hda file just to add a new service or change the name of an Idoc function.

### Cleaner and more maintainable code

A service is very easy to write in Twine.

```java
public class ExampleServicePackage {

    @ServiceMethod(name = "EXAMPLE_SERVICE")
    public void exampleService(@Binder(name = "name") String name) {
        SystemUtils.trace("system", "Hello " + name + "!");
    }
}
```

We have declared a service called `EXAMPLE_SERVICE`, which requires a single string parameter called `name`.

If you were to do this the old fashioned way (not including the .hda file work), you'd end up writing:

```java
public class ExampleServicePackage extends Service {

    public void exampleService() throws DataException {
        String name = m_binder.getLocal("name");

        if (name == null) {
            throw new DataException("name is required");
        }

        SystemUtils.trace("system", "Hello " + name + "!");
    }
}
```

Now suppose you wanted several parameters and each parameter to be optional; to have a default when not supplied; or to be a parsed into a boolean or Date object?

If you end up parameter checking 4 or 5 values, you can end up with more "glue" code than actual functionality.

Twine handles the glue for you, so your methods are cleaner, more descriptive and **easier to test**.

### Code is testable

Services, filters and Idoc scripts in their original form aren't easy to test. But a Twine POJO makes it simple.

```java
public class ExampleServicePackage {

    @ServiceMethod(name = "EXAMPLE_SERVICE")
    public DataBinder exampleService(@Binder(name = "name") String name, DataBinder outputBinder) {
        outputBinder.putLocal("message", "Hello " + name + "!");

        return outputBinder;
    }
}
```

Here we are returning the `DataBinder` object so we can unit test the method.

```java
public class TestExampleServicePackage {

    @Test
    public void testExampleService_ShouldReturnHelloWorld() {

        DataBinder binder = new DataBinder();
        binder.putLocal("name", "Tim");

        ExampleServicePackage pkg = new ExampleServicePackage();

        binder = pkg.exampleService("Tim", binder);

        assertEquals("Hello Tim!", binder.getLocal("message"));
    }
}
```

### Idoc functions are easy to write

Idoc functions can be so much of a pain to write that most developers will find any other way to implement the functionality before resorting to a custom Idoc function. Twine makes Idoc functions so simple that it's about as complicated as writing the function in Java. In turn you'll write better Idoc script functions, and your template code will be a lot cleaner.

```java
public class ExampleScriptPackage {

    @IdocFunction
    public int strLength(String str) {
        return str.length();
    }

    // <$ strLength("bob") $> = 3
}
```

Compare this to the effort required to create an Idoc function the old way (ommitted due to size) and you can see how much more productive Twine will make UCM customisations.

Idoc script functions can also include injected objects and `@Binder` values which can be defined in any order and will not affect the basic function parameters:

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String specialFunction(String str, UserData u, Long l) {
        return "String was: " + str + ", long was " + l.toString();
    }

    // <$ specialFunction("bob", 3) $> = String was bob, long was 3

    @IdocFunction
    public String specialFunction(String str, UserData u, @Binder(name = "l") Long l) {
        return "String was: " + str + ", binder value was " + l.toString();
    }

    // <$ l = 4 $>
    // <$ specialFunction("bob") $> = String was bob, binder value was 4
}
```

Twine handles type conversion of the parameters and return types automatically so you can define the function as you see fit. _To prevent too much data conversion you should define your parameters as Double, Long or Date, because those are the types UCM uses internally._

### Multiple filters in one class

Because Twine uses method annotations for Idoc scripts and services, we decided to keep to this convention by placing the filter annotations on the method instead of the class. A nice side effect of this is that you can add several filter annotations to a single class. This allows you to group similar filters together.

```java
public class ExampleFilterPackage {

    @Filter(event = "validateStandard")
    public int beforeCheckin(@Binder(name = "dDocTitle") String dDocTitle, @Binder(name = "dSecurityGroup"), UserData u) {
        // do something
        return FilterImplementor.CONTINUE;
    }

    @Filter(event = "updateExtendedAttributes")
    public int afterCheckin(@Binder(name = "dDocName") String dDocName) {
        SystemUtils.trace("twine", "Checked in " + dDocName);

        return FilterImplementor.CONTINUE;
    }
}
```

Quick Start
================

Twine can be used as a library in your own component or - if you plan to have a few Twine based components installed - it can be used as a single shared component.

Use as a shared TwineLib component
----------------------------------

1. Install the TwineLib component and enable it.
1. Follow the next guide from step 4.

Including the Twine jar in your own component
---------------------------------------------

1. Grab the Twine jar from the downloads page or include it as a maven dependency:

    ```xml
    <dependency>
        <groupId>org.ucmtwine</groupId>
        <artifactId>ucm-twine</artifactId>
        <version>0.9.0</version>
    </dependency>
    ```

2. Create a UCM component: MyComponent
3. Include twine in your MyComponent.hda classpath:

    ```
    classpath=$COMPONENT_DIR/lib/mycomponent.jar;$COMPONENT_DIR/lib/ucm-twine-0.9.0.jar;
    ```

4. Include a .properties file in your jar to define your services and script extension classes. If you use maven and place the file in `src/main/resources` it will be at the root of your jar.

    ```
    # <jar root>/mycomponent.properties
    ucm.service.ExampleServicePackage=org.component.example.ExampleServicePackage
    ```

5. Add Twine's Bootstrapper to the **Filters** resultset of your MyComponent.hda file, using the .properties file as the filter's parameter. If the .properties file is not at the root of your jar you may need to add relative path information here.

    ```
    @ResultSet Filters
    4
    type
    location
    parameter
    loadOrder
    extraAfterConfigInit
    org.ucmtwine.Bootstrapper
    mycomponent.properties
    1000
    @end
    ```

6. Create your class and annotate a method:

    ```java
    package org.component.example;

    public class ExampleServicePackage {

        @ServiceMethod(name = "EXAMPLE_SERVICE")
        public void exampleService(@Binder(name = "param1") String param1) {
            SystemUtils.trace("system", "param1 was " + param1);
        }
    }
    ```

7. Build mycomponent.jar

From here you can edit your .properties file each time you add a new class. However, if you are adding a new method to an existing class, there is no more setup needed.

Properties file syntax
----------------------

The .properties file should declare each class that you wish to load with the appropriate key.

Filters: `ucm.filter.uniqueKey1=fully.qualified.class.Name`

Services: `ucm.service.uniqueKey2=fully.qualified.class.Name2`

Idoc Script: `ucm.idocscript.uniqueKey3=fully.qualified.class.Name3`

Make sure to define each key before the = sign with a different name so that each class is picked up.

That's all you need! Now you can begin writing service, filter or Idoc script definitions into your classes.

Reference
=============

Dependency injection types
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

@Binder
----------------------------

**name** The parameter name in the binder or the result set name.

**required** Whether the parameter is required. Default: true.

**missing** If required is set to false, this is the value that will be given if the parameter is not supplied. (not implemented yet)

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

@ServiceMethod
--------

**name** The service name

**template** The template to render. Unfortunately, templates are still defined in the old ComponentWizard way, for now.

**errorMessage** The error message to return when the service fails

**type** The service type **(unimplemented)**

**accessLevel** This correlates to the ComponentWizard dialog security flags. Accepts a binary bitmask of the following: `ACCESS_READ`, `ACCESS_WRITE`, `ACCESS_DELETE`, `ACCESS_ADMIN`, `ACCESS_GLOBAL`, `ACCESS_SCRIPTABLE`. **(likely to change)**

Defaults to `ACCESS_READ | ACCESS_SCRIPTABLE`. **(also likely to change)**

**subjects** The subjects to notify

Example:

```java
public class ExampleServicePackage {

    @ServiceMethod(name = "EXAMPLE_SERVICE", template = "TPL_EXAMPLE")
    public void exampleService(@Binder(name = "param1") String param1) {
        SystemUtils.trace("system", "param1 was " + param1);
    }
}
```

@IdocFunction and @IdocVariable
-----------------------------------

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

@Filter
-------

**loadOrder** The load order. Default: 100.

**event** The filter name e.g. validateStandard. 

**Note:** At the time of writing, the filter injector runs at _extraAfterConfigInit_. Only events that execute *after* this event will work.

**parameter** The parameter to pass to the filter **(unimplemented)**.

Example:

```java
public class ExampleFilterPackage {

    @Filter(event = "validateStandard", loadOrder = 10)
    public void exampleFilter(@Binder(name = "dID") Long dID, UserData u) {
        SystemUtils.trace("system", "The user " + u.getName() + " acted on dID " + dID.toString());
    }
}
```

Roadmap
=======

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