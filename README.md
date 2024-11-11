# Jar Method Executor

## Purpose

- To execute only the target method inside .jar file

## Usages

- Debug library file (.jar)

## How to Use it

> java JarMethodExecutor {data.txt}
- Pass argument as the data.txt path

| data.txt format
```
// Sample data.txt
-- character that treated as comment : '//', '-'
-- 1. .jar file path
../../build/libs/jarexecutor-1.0-SNAPSHOT.jar
-- 2. class name
Sample
-- 3. method name
str
-- 4. method argument type(Optional)

-- 5. method argument(Optional)

```

> Argument Type
- `bool` : bool, boolean
- `char` : char, character
- `byte` : byte
- `short` : short
- `int` : int, integer
- `long` : long
- `float` : float
- `double` : double
- `string` : string
- `Blank` treats as `Void` type
- Types that not in the list would treats as `Object` type
