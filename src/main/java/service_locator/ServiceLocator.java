package service_locator;

import java.util.HashMap;
import java.util.Map;

public final class ServiceLocator {

    private static Map<Class<?>, Object> services = new HashMap<>();

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

    public static void removeService(Class c) {
        services.remove(c);
    }
}
