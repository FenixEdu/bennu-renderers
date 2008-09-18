package pt.ist.fenixWebFramework.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import pt.ist.fenixWebFramework._development.LogLevel;
import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.IllegalWriteException;
import pt.ist.fenixframework.pstm.ServiceInfo;
import pt.ist.fenixframework.pstm.Transaction;

public class ServiceManager {

    public static final Map<String, String> KNOWN_WRITE_SERVICES = new ConcurrentHashMap<String, String>();

    private static InheritableThreadLocal<String> isInServiceVar = new InheritableThreadLocal<String>();

    private static InheritableThreadLocal<List<Command>> afterCommitCommands = new InheritableThreadLocal<List<Command>>();
    
    public static final String BERSERK_SERVICE = "berserk";

    public static final String ANNOTATION_SERVICE = "annotation";

    public static void resetAfterCommitCommands() {
	afterCommitCommands.set(null);
    }
    
    public static List<Command> getAfterCommitCommands() {
	return afterCommitCommands.get();
    }
    
    public static void registerAfterCommitCommand(Command command) {
	List<Command> commands = getAfterCommitCommands();
	if (commands == null) {
	    commands = new ArrayList<Command> ();
	    afterCommitCommands.set(commands);
	}
	commands.add(command);
    }
    
    public static boolean isInsideBerserkService() {
	String currentManager = isInServiceVar.get();
	return currentManager != null && currentManager.equals(BERSERK_SERVICE);
    }

    public static boolean isInsideService() {
	return isInServiceVar.get() != null;
    }

    public static void enterBerserkService() {
	enterService(BERSERK_SERVICE);
    }

    public static void enterAnnotationService() {
	enterService(ANNOTATION_SERVICE);
    }

    private static void enterService(String manager) {
	String currentManager = isInServiceVar.get();
	if (currentManager == null) {
	    isInServiceVar.set(manager);
	}
    }

    public static void exitBerserkService() {
	exitService(BERSERK_SERVICE);
    }

    public static void exitAnnotationService() {
	exitService(ANNOTATION_SERVICE);
    }

    private static void exitService(String manager) {
	String currentManager = isInServiceVar.get();
	if (currentManager.equals(manager)) {
	    isInServiceVar.remove();
	}
    }

    public static void initServiceInvocation(final String serviceName, final Object[] args) {
	final User user = UserView.getUser();
	final String username = user == null ? null : user.getUsername();
	ServiceInfo.setCurrentServiceInfo(username, serviceName, args);

	Transaction.setDefaultReadOnly(!ServiceManager.KNOWN_WRITE_SERVICES.containsKey(serviceName));
    }

    public static void beginTransaction() {
	if (jvstm.Transaction.current() != null) {
	    jvstm.Transaction.commit();
	}
	Transaction.begin();
    }

    public static void commitTransaction() {
	jvstm.Transaction.checkpoint();
	Transaction.currentFenixTransaction().setReadOnly();
    }

    public static void abortTransaction() {
	Transaction.abort();
	Transaction.begin();
	Transaction.currentFenixTransaction().setReadOnly();
    }

    public static void logTransactionRestart(String service, Throwable cause, int tries) {
	System.out.println("Service " + service + " has been restarted " + tries + " times because of "
		+ cause.getClass().getSimpleName());
    }

    public static void execute(final ServicePredicate servicePredicate) {
	if (isInsideService()) {
	    servicePredicate.execute();
	} else {
	    final String serviceName = servicePredicate.getClass().getName();
	    enterAnnotationService();
	    try {
		ServiceManager.initServiceInvocation(serviceName, new Object[] { servicePredicate });

		boolean keepGoing = true;
		int tries = 0;
		try {
		    while (keepGoing) {
			tries++;
			try {
			    try {
				beginTransaction();
				servicePredicate.execute();
				ServiceManager.commitTransaction();
				List<Command> commands = getAfterCommitCommands();
				    if (commands != null) {
					for (Command command : commands) {
					    command.execute();
					}
				}
				keepGoing = false;
			    } finally {
				if (keepGoing) {
				    ServiceManager.abortTransaction();
				} 
				resetAfterCommitCommands();
			    }
			} catch (jvstm.CommitException commitException) {
			    if (tries > 3) {
				logTransactionRestart(serviceName, commitException, tries);
			    }
			} catch (AbstractDomainObject.UnableToDetermineIdException unableToDetermineIdException) {
			    if (tries > 3) {
				logTransactionRestart(serviceName, unableToDetermineIdException, tries);
			    }
			} catch (IllegalWriteException illegalWriteException) {
			    ServiceManager.KNOWN_WRITE_SERVICES.put(servicePredicate.getClass().getName(), servicePredicate
				    .getClass().getName());
			    Transaction.setDefaultReadOnly(false);
			    if (tries > 3) {
				logTransactionRestart(serviceName, illegalWriteException, tries);
			    }
			}
		    }
		} finally {
		    Transaction.setDefaultReadOnly(false);
		    if (LogLevel.INFO) {
			if (tries > 1) {
			    System.out.println("Service " + serviceName + "took " + tries + " tries.");
			}
		    }
		}
	    } finally {
		ServiceManager.exitAnnotationService();
	    }
	}
    }

}
