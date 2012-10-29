Twine for Oracle UCM
====================
[![Build Status](https://travis-ci.org/tstirrat/ucm-twine.png)](https://travis-ci.org/tstirrat/ucm-twine)

An annotation based Java framework for Oracle UCM.

- Write UCM services, Idoc script extensions and filters using Java [POJO](http://en.wikipedia.org/wiki/Plain_Old_Java_Object "Plain Old Java Object")s. 
- Binder value validation and conversion e.g ask for a Date or ResultSet instead of doing the work manually each time
- Dependency injection of Service context members (PageMerger, HttpImplementor) making your code easy to unit test
- Classes are registered once in a single properties file, instead of having to edit the old HTML tables to add each service

Getting Started
---------------

View the guide on [creating a Twine component](https://github.com/tstirrat/ucm-twine/wiki/Creating-a-Twine-component) in the wiki.


Why Twine?
----------

### All in one place

Twine puts all the information in the Java file where the functionality is, so you don't need to edit .hda and .htm files just to add a service, filter or Idoc function

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

Idoc functions can be so much of a pain to write that most developers will avoid creating them at all costs. Twine makes Idoc functions so simple that you'll prefer writing them in favour of large blocks of Idoc. 

```java
public class ExampleScriptPackage {

    @IdocFunction
    public int strLength2(String str) {
        return str.length();
    }

    // <$ strLength2("bob") $> = 3
}
```

You'll be able to test them, too.

```java
public class TestExampleScriptPackage {

    @Test
    public void testStrLength2 {
    	assertEqual("Hello".length(), script.strLength2("Hello"));
    }
}

```

Compare this to the effort required to create an Idoc function the old way (ommitted due to size) and you can see how much more productive Twine will make UCM customisations.

Idoc script functions can also include injected objects. These can be defined in any order and will not affect the basic function parameters:

```java
public class ExampleScriptPackage {

    @IdocFunction
    public String specialFunction(String str, UserData u, Long l) {
        return "String was: " + str + ", long was " + l.toString();
    }

    // <$ specialFunction("bob", 3) $> = String was bob, long was 3
}
```

Twine handles type conversion of the parameters and return types automatically so you can define the function as you see fit.

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

Reference
=============

Configuration file
----------------------

The twine.properties file should declare each class that you wish to load with the appropriate key.

Filters: `ucm.filter.uniqueKey1=fully.qualified.class.Name`

Services: `ucm.service.uniqueKey2=fully.qualified.class.Name2`

Idoc Script: `ucm.idocscript.uniqueKey3=fully.qualified.class.Name3`

Make sure to define each key before the = sign with a different name so that each class is picked up.

That's all you need! Now you can begin writing service, filter or Idoc script definitions into your classes.

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
- Provider

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

Scheduled events can sometimes be quite tricky. A lot of boilerplate code is needed to make sure events fire only once on clusters, at the right time and with the right frequency.

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

### Some parts of this software are licensed under separate licenses

#### src/main/java/net/balusc/util/ObjectConverter.java

see: http://balusc.blogspot.com/2007/08/generic-object-converter.html

Copyright (C) 2007 BalusC

This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 
This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
You should have received a copy of the GNU Lesser General Public License along with this library.
If not, see <http://www.gnu.org/licenses/>.
