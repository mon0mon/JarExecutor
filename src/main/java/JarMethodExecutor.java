import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JarMethodExecutor {
  private static int IDX_PARAM_JAR_FILEPATH = 0;
  private static int IDX_PARAM_CLASS_NAME = 1;
  private static int IDX_PARAM_METHOD_NAME = 2;
  private static int IDX_PARAM_METHOD_ARGS = 3;
  private static int IDX_PARAM_METHOD_PARAMS = 4;

  public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (args == null || args.length < 1) {
      System.out.println("Usage: java JarMethodExecutor <data-txt-path>");
      return;
    }

    List<String> data = readAllLines(args[0]);

    final String JAR_FILEPATH = data.get(0);
    final String CLASS_NAME = data.get(1);
    final String METHOD_NAME = data.get(2);
    final String METHOD_ARGS = data.get(3);

    File jarFile = new File(JAR_FILEPATH);
    String className = CLASS_NAME;
    String methodName = METHOD_NAME;
    Class<?>[] methodArgs = getMethodArgs(METHOD_ARGS);
    List<String> methodParamValues = new ArrayList<>();

    URL[] urls = {jarFile.toURI().toURL()};
    URLClassLoader classLoader = new URLClassLoader(urls);

    Class<?> loadedClass = classLoader.loadClass(className);
    Method method = loadedClass.getMethod(methodName, methodArgs);
    Class<?> returnType = method.getReturnType();

    // static 여부 확인 후, static이 아닌 경우 인스턴스 생성
    Object instance = Modifier.isStatic(method.getModifiers()) ? null : loadedClass.getDeclaredConstructor().newInstance();

    AtomicInteger ai = new AtomicInteger(0);
    methodParamValues = data.stream()
                          .filter((param) -> ai.getAndAdd(1) >= IDX_PARAM_METHOD_PARAMS)
                          .collect(Collectors.toList());
    Object[] invokeArgs = getParamsArray(methodArgs, methodParamValues);

    Object returnValue = method.invoke(instance, invokeArgs);

    System.out.println(className + " " + methodName + " executed successfully.");
    
    printReturnValue(returnType, returnValue);
  }

  private static void printReturnValue(Class<?> returnType, Object returnValue) {
    switch (returnType.getName()) {
      case "void":
        System.out.println("Method executed successfully.");
        break;
      case "boolean":
      case "java.lang.Boolean":
        System.out.println("Return value: " + (boolean) returnValue);
        break;
      case "char":
      case "java.lang.Character":
        System.out.println("Return value: " + (char) returnValue);
        break;
      case "byte":
      case "java.lang.Byte":
        System.out.println("Return value: " + (byte) returnValue);
        break;
      case "short":
      case "java.lang.Short":
        System.out.println("Return value: " + (short) returnValue);
        break;
      case "int":
      case "java.lang.Integer":
        System.out.println("Return value: " + (int) returnValue);
        break;
      case "long":
      case "java.lang.Long":
        System.out.println("Return value: " + (long) returnValue);
        break;
      case "float":
      case "java.lang.Float":
        System.out.println("Return value: " + (float) returnValue);
        break;
      case "double":
      case "java.lang.Double":
        System.out.println("Return value: " + (double) returnValue);
        break;
      default:
        System.out.println("Return value: " + returnValue);
        break;
    }
  }

  private static Object[] getParamsArray(Class<?>[] methodArgs, List<String> methodParams) {
    if (methodArgs == null || methodArgs.length == 0) {
      return new Object[0];
    }

    if (methodParams == null || methodParams.isEmpty()) {
      return new Object[0];
    }

    if (methodArgs.length != methodParams.size()) {
      System.out.println("The number of method arguments and parameters are not matched.");
      return new Object[0];
    }

    Object[] methodParamsArray = new Object[methodParams.size()];
    for (int i = 0; i < methodParams.size(); i++) {
      if (methodParams.get(i).equals("null")) {
        methodParamsArray[i] = null;
        continue;
      }

      switch (methodArgs[i].getName()) {
        case "boolean":
        case "java.lang.Boolean":
          methodParamsArray[i] = Boolean.parseBoolean(methodParams.get(i));
          break;
        case "char":
        case "java.lang.Character":
          methodParamsArray[i] = methodParams.get(i).charAt(0);
          break;
        case "byte":
        case "java.lang.Byte":
          methodParamsArray[i] = Byte.parseByte(methodParams.get(i));
          break;
        case "short":
        case "java.lang.Short":
          methodParamsArray[i] = Short.parseShort(methodParams.get(i));
          break;
        case "int":
        case "java.lang.Integer":
          methodParamsArray[i] = Integer.parseInt(methodParams.get(i));
          break;
        case "long":
        case "java.lang.Long":
          methodParamsArray[i] = Long.parseLong(methodParams.get(i));
          break;
        case "float":
        case "java.lang.Float":
          methodParamsArray[i] = Float.parseFloat(methodParams.get(i));
          break;
        case "double":
        case "java.lang.Double":
          methodParamsArray[i] = Double.parseDouble(methodParams.get(i));
          break;
        case "string":
        case "java.lang.String":
          methodParamsArray[i] = methodParams.get(i);
          break;
        default:
          methodParamsArray[i] = (Object) methodParams.get(i);
          break;
      }
    }

    return methodParamsArray;
  }

  public static List<String> readAllLines(String filePath) throws IOException {
    if (filePath == null || filePath.isEmpty()) {
      return null;
    }

    List<String> inputs = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);

    List<String> results = inputs.stream()
                             .filter((str) -> !(str.startsWith("//") || str.startsWith("-")))
                             .collect(Collectors.toList());

    return results;
  }

  public static Class<?>[] getMethodArgs(String arg) {
    if (arg == null || arg.isEmpty()) {
      return new Class[0];
    }

    String[] args = arg.toLowerCase().split(" ");

    Class<?>[] objectArray = new Class[args.length];

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "bool":
        case "boolean":
          objectArray[i] = Boolean.class;
          break;
        case "char":
        case "character":
          objectArray[i] = Character.class;
          break;
        case "byte":
          objectArray[i] = Byte.class;
          break;
        case "short":
          objectArray[i] = Short.class;
          break;
        case "int":
        case "integer":
          objectArray[i] = Integer.class;
          break;
        case "long":
          objectArray[i] = Long.class;
          break;
        case "float":
          objectArray[i] = Float.class;
          break;
        case "double":
          objectArray[i] = Double.class;
          break;
        case "string":
          objectArray[i] = String.class;
          break;
        default:
          objectArray[i] = Object.class;
          break;
      }
    }

    return objectArray;
  }
}
