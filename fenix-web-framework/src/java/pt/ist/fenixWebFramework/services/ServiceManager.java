package pt.ist.fenixWebFramework.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixframework.pstm.ServiceInfo;
import pt.ist.fenixframework.pstm.Transaction;

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

    public static void initServiceInvocation(final String serviceName, final Object[] args) {
	final User user = UserView.getUser();
	final String username = user == null ? null : user.getUsername();
	ServiceInfo.setCurrentServiceInfo(username, serviceName, args);

	Transaction.setDefaultReadOnly(!ServiceManager.KNOWN_WRITE_SERVICES.containsKey(serviceName));
    }

    public static void beginTransaction() {
        if (Transaction.current() != null) {
            Transaction.commit();
        }
        Transaction.begin();
    }

    public static void commitTransaction() {
        Transaction.checkpoint();
        Transaction.currentFenixTransaction().setReadOnly();
    }

    public static void abortTransaction() {
        Transaction.abort();
        Transaction.begin();
        Transaction.currentFenixTransaction().setReadOnly();
    }

}
