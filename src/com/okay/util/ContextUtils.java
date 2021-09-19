package com.okay.util;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;
import com.okay.annotations.Inject;
import com.okay.context.ApplicationContext;
import com.okay.exception.CircularDependencyException;
import com.okay.exception.InjectErrorException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContextUtils {

    private static final String CLASS_FILE_EXTENSION = ".class";

    private static HashMap<String, Class<?>> beanMap;

    public static ApplicationContext createApplicationContext(Class<?> clazz) throws Exception {
        try {
            ApplicationContext applicationContext = new ApplicationContext();

            // initialize bean map
            beanMap = new HashMap<>();

            ClassLoader classLoader = clazz.getClassLoader();

            URL root = classLoader.getResource(""); // for find root directory

            Path rootPath = Paths.get(root.toURI());

            // fill all beans
            try (Stream<Path> walkStream = Files.walk(rootPath)) {
                // create bean from path
                List<Path> pathList = walkStream.filter(p -> p.toFile().isFile()).filter(p -> p.toString().endsWith(CLASS_FILE_EXTENSION)).collect(Collectors.toList());
                for (Path path : pathList) {
                    String pathValue = rootPath.relativize(path).toString();
                    fillBeanMap(beanMap, createClassFromPath(classLoader, pathValue));
                }
            }
            applicationContext.setBeanMap(beanMap);

            // check circular dependency and bean injection
            try (Stream<Path> walkStream = Files.walk(rootPath)) {
                List<Path> pathList = walkStream.filter(p -> p.toFile().isFile()).filter(p -> p.toString().endsWith(CLASS_FILE_EXTENSION)).collect(Collectors.toList());
                for (Path path : pathList) {
                    String pathValue = rootPath.relativize(path).toString();
                    Class<?> pathClazz = createClassFromPath(classLoader, pathValue);
                    if (!pathClazz.isInterface() && !pathClazz.isEnum() && pathClazz.isAnnotationPresent(Bean.class)) {
                        // circular dependency check
                        checkCircularDependency(pathClazz, new ArrayList<>());
                    }
                }
            }

            // call init methods
            Iterator<Map.Entry<String, Class<?>>> iterator = beanMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Class<?>> entry = iterator.next();
                callInitMethods(entry.getValue());
            }

            return applicationContext;
        } catch (Exception e) {
            throw e;
        }
    }

    private static Class<?> createClassFromPath(ClassLoader classLoader, String classPath) throws ClassNotFoundException {
        return classLoader.loadClass(classPath.replace("\\", ".").substring(0, classPath.length() - CLASS_FILE_EXTENSION.length()));
    }

    private static boolean checkBeanAnnotation(Class<?> clazz) {
        if (clazz.isInterface()) {
            return true;
        }
        return !clazz.isEnum() && clazz.isAnnotationPresent(Bean.class);
    }

    private static void fillBeanMap(HashMap<String, Class<?>> beanMap, Class<?> clazz) {
        if (!clazz.isInterface() && !clazz.isEnum() && clazz.isAnnotationPresent(Bean.class)) {
            // beanMap yeni sınıf ekleniyor
            beanMap.put(clazz.getSimpleName(), clazz);
        }
    }

    private static void callInitMethods(Class<?> clazz) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!clazz.isInterface() && !clazz.isEnum() && clazz.isAnnotationPresent(Bean.class)) {
            // Init metodları kontrol ediliyor
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(Init.class)) {
                    declaredMethod.invoke(clazz.newInstance(), null);
                }
            }
        }
    }

    private static void checkCircularDependency(Class<?> clazz, List<Class<?>> parentClassList) throws CircularDependencyException, InjectErrorException {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Inject.class)) {
                // listede yer alıyor ise circular dependency var demek
                if (parentClassList.contains(clazz)) {
                    CircularDependencyException exception = new CircularDependencyException();
                    parentClassList.add(clazz);
                    exception.setClassList(parentClassList);
                    throw exception;
                }

                // ilgili classın Bean annotation kontrolü
                if (!checkBeanAnnotation(declaredField.getType())) {
                    InjectErrorException exception = new InjectErrorException();
                    exception.setBeanName(declaredField.getType().getSimpleName());
                    throw exception;
                }

                // yeni bir liste oluşturulup gönderiliyor, diğer türlü içiçe injectlerde hata alınır
                List<Class<?>> classList = new ArrayList<>(parentClassList);
                classList.add(clazz);
                checkCircularDependency(declaredField.getType(), classList);
            }
        }
    }

    // burada recursive bir yapı ile içiçe olan beanleri oluşturuyorum
    public static Object createBeanObject(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        Object object = clazz.newInstance();

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(Inject.class)) {
                // fill object value
                declaredField.setAccessible(true);

                if (declaredField.getType().isInterface()) {
                    // find class from interface
                    Iterator<Map.Entry<String, Class<?>>> iterator = beanMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Class<?>> bean = iterator.next();
                        Class<?>[] interfaces = bean.getValue().getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            if (anInterface == declaredField.getType()) {
                                declaredField.set(object, createBeanObject(bean.getValue()));
                            }
                        }
                    }
                } else {
                    declaredField.set(object, createBeanObject(declaredField.getType()));
                }
            }
        }

        return object;
    }
}