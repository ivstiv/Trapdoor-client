package core;

import java.util.HashMap;
import java.util.Map;

public final class ServiceLocator {

    private static Map<Class<?>, Object> services = new HashMap<>();


    // returns always a service. If it is not initialised an instance will be created.
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> c) {
        if(services.containsKey(c)) {
         return (T) services.get(c);
        }else{
            try {
                services.put(c, c.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return (T) services.get(c);
        }
    }

    // returns only if the service is initialised
    @SuppressWarnings("unchecked")
    public static <T> T getInitialisedService(Class<T> c) throws Exception {
        if(services.containsKey(c)) {
            return (T) services.get(c);
        }else{
            throw new Exception("Service of class: "+c.getName()+" is not initialised!");
        }
    }

    // overwrites previously initialised services of the same class
    public static void initialiseService(Object o) {
        Class<?> clazz = o.getClass();
        services.put(clazz, o);
    }

    public static void removeService(Class c) {
        services.remove(c);
    }
}
