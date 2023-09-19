package net.zanckor.questapi.eventmanager;

import net.zanckor.questapi.eventmanager.annotation.EventSubscriber;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.List;
import java.util.Set;

public class RegisterEventListener {
    public static List<Class<?>> getAnnotatedClasses() {
        Reflections reflections = new Reflections("net.zanckor.questapi", new SubTypesScanner(false));
        Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);

        //Remove all classes that doesn't have @EventSubscriber as an annotation
        allClasses.removeIf(clazz -> clazz.getAnnotation(EventSubscriber.class) == null);

        return allClasses.stream().toList();
    }
}