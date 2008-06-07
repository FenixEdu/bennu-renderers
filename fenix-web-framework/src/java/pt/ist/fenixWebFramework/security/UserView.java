package pt.ist.fenixWebFramework.security;

public class UserView {

    private static InheritableThreadLocal<Object> user = new InheritableThreadLocal<Object>();

    public static <T> T getUser() {
	return (T) user.get();
    }

    public static <T> void setUser(T t) {
	user.set(t);
    }
    
    public static boolean hasUser() {
	return user.get() != null;
    }

}
