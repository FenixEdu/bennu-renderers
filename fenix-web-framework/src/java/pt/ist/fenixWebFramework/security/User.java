package pt.ist.fenixWebFramework.security;

import org.joda.time.DateTime;

public interface User {

    public boolean hasRole(final String role);

    public String getUsername();

    public String getPrivateConstantForDigestCalculation();

    public DateTime getUserCreationDateTime();

    public DateTime getLastLogoutDateTime();

}
