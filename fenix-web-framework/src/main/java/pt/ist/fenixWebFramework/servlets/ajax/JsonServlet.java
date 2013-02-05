package pt.ist.fenixWebFramework.servlets.ajax;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.CharEncoding;
import org.joda.time.DateTime;

import pt.ist.fenixWebFramework.security.User;
import pt.ist.fenixWebFramework.security.UserView;
import pt.ist.fenixWebFramework.servlets.json.JsonObject;

public class JsonServlet extends HttpServlet {

    private static final String JAVASCRIPT_LIBRARY_ENCODING = CharEncoding.UTF_8;

    public static final String PURGE_METHOD = "purge";
    public static final String REQUEST_OBJECT_METHOD = "request";
    public static final String TOKEN_PARAMETER_NAME = "token";
    public static final String ACTION_PARAMETER_NAME = "action";

    private static ConcurrentHashMap<String, JsonObject> tokenMap = new ConcurrentHashMap<String, JsonObject>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) {
        String actionToPerform = req.getParameter(ACTION_PARAMETER_NAME);
        if (actionToPerform.equals(PURGE_METHOD)) {
            purge(req, resp);
        } else if (actionToPerform.equals(REQUEST_OBJECT_METHOD)) {
            serve(req, resp);
        }
    }

    private void serve(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json; charset=" + JAVASCRIPT_LIBRARY_ENCODING);
        String token = req.getParameter(TOKEN_PARAMETER_NAME);
        if (token != null) {
            JsonObject object = tokenMap.get(token);
            try {
                if (object != null) {
                    resp.getWriter().write(object.getJsonString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void purge(HttpServletRequest req, HttpServletResponse resp) {
        String token = req.getParameter(TOKEN_PARAMETER_NAME);
        if (token != null) {
            tokenMap.remove(token);
        }
    }

    public static String getTokenFor(JsonObject jsonObject) {
        User user = UserView.getUser();
        if (user == null) {
            throw new RuntimeException("access.control.error.only.authenticated.users.can.request.tokens");
        }
        StringBuilder hash = new StringBuilder(user.getPrivateConstantForDigestCalculation());
        hash.append(user.getUsername());
        hash.append(user.getUserCreationDateTime().toString());
        hash.append(new DateTime());

        String digest;
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(hash.toString().getBytes());
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (byte element : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & element));
            }
            digest = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        tokenMap.put(digest, jsonObject);
        return digest;
    }

}
