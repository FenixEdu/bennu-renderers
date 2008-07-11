package pt.ist.fenixWebFramework.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {

    public static final Map<String,String> KNOWN_WRITE_SERVICES = new ConcurrentHashMap<String,String>();

    private static InheritableThreadLocal<Boolean> isInServiceVar = new InheritableThreadLocal<Boolean>();

    public static boolean isInsideService() {
	final Boolean isInService = isInServiceVar.get();
	return isInService == null ? false : isInService.booleanValue();
    }

    public static void enterService() {
	isInServiceVar.set(Boolean.TRUE);
    }

    public static void exitService() {
	isInServiceVar.remove();
    }

}
