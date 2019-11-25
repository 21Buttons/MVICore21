MVICore21
=

[![CircleCI](https://circleci.com/gh/21Buttons/MVICore21.svg?style=shield)](https://circleci.com/gh/21Buttons/MVICore21) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

## Description
This library is based on [MVICore](https://github.com/badoo/MVICore) library from Badoo. 
Its purpose is to create a lightweight framework to implement a MVI architecture in Android.
Refer to [MVICore documentation](https://badoo.github.io/MVICore/) and [this post](https://badootech.badoo.com/a-modern-kotlin-based-mvi-architecture-9924e08efab1)
to have a better insight on the usage of this framework.

## Download

```gradle
implementation 'com.21buttons:mvicore21:0.1.0'
```

## Main differences with MVICore
- **No Reducer class:** In MVICore21, the **Effect** class knows how to 'Reduce' itself, thus creating a new state
- **No work done when creating the feature:** MVICore had a problem for us that was that just after creating an instance, it started the state machine. This made testing of the whole feature difficult
- **Based on `Flowable` instead of `Observable`**

### Limitations
- **No time travel debugger**
- **No reactive component binding**
- **No middleware**

## Diagram
Here's a diagram showing how MVICore21 (using the **Feature** class) interacts with your presenter and your view

![mvicore21](https://user-images.githubusercontent.com/1402183/69535789-efb1f500-0f7c-11ea-8b09-3bb465eba610.png)
