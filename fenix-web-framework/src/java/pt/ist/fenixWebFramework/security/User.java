package pt.ist.fenixWebFramework.security;

public interface User {

    public boolean hasRole(final String role);

    public String getUsername();

    public String getPrivateConstantForDigestCalculation();
}
