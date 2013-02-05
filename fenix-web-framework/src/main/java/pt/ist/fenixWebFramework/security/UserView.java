package pt.ist.fenixWebFramework.security;

public class UserView {

    private static ThreadLocal<User> user = new ThreadLocal<User>();

    public static <T extends User> T getUser() {
        return (T) user.get();
    }

    public static <T extends User> void setUser(T t) {
        user.set(t);
    }

    public static boolean hasUser() {
        return user.get() != null;
    }

}
