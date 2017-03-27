import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

import com.auth0.jwt.JWTVerifier;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
public class Main
{
    private static String clientId = System.getenv("AUTH0_CLIENT_ID");
    private static String clientDomain = System.getenv("AUTH0_DOMAIN");
    private static SecureRandom random = new SecureRandom();
    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";
    private static final int yearInSeconds = 24 * 365 * 60 * 60;
    public static void main(String[] args)
    {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/spark/template/freemarker");
        get("/", (request, response) ->
        {
            Map<String, Object> attributes = new HashMap<>();
            Map<String , Object> user = getUser(request);
            attributes.put("loggedIn" , user.get("loggedIn"));
            if((boolean) attributes.get("loggedIn")) {

            }
            attributes.put("user" , user.get("claims"));
            attributes.put("loggedIn" , user.get("loggedIn"));
            attributes.put("clientId" , clientId);
            attributes.put("clientDomain" , clientDomain);
            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());

        get("/generic", (request, response) ->
        {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "generic.ftl");
        }, new FreeMarkerEngine());

        get("/elements", (request, response) ->
        {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "elements.ftl");
        }, new FreeMarkerEngine());

        before("/login", (request, response) -> {
            if (request.raw().getHeader(X_FORWARDED_PROTO) != null) {
                if (request.raw().getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
                    response.redirect("https://" + request.raw().getServerName() + (request.raw().getPathInfo() == null ? "" : request.raw().getPathInfo()));
                }
            }
            request.session().attribute("token" , request.queryParams("token"));
            response.redirect("/");
        });

        before((request, response) -> {
            response.cookie("JSESSIONID" , request.cookie("JSESSIONID") , yearInSeconds);
            if (request.raw().getHeader(X_FORWARDED_PROTO) != null) {
                if (request.raw().getHeader(X_FORWARDED_PROTO).indexOf("https") != 0) {
                    response.redirect("https://" + request.raw().getServerName() + (request.raw().getPathInfo() == null ? "" : request.raw().getPathInfo()));
                }
            }
        });
    }

    private static Map<String, Object> getUser(Request request) { //Returns a map with a loggedIn value. If the loggedIn value is true also contains a value with key "claims"
        Map<String, Object> user = new HashMap<>();
        Map<String, Object> userInfo;
        if (request.session().attribute("token") == null) {
            user.put("loggedIn", false);
        } else {
            String token = request.session().attribute("token");
            userInfo = checkToken(token);
            if (userInfo.containsKey("loggedIn"))
                user = userInfo;
            else
                user.put("loggedIn", false);
        }
        return user;
    }

    private static Map<String, Object> checkToken(String token) {
        Map<String, Object> values = new HashMap<>();
        final String secret = System.getenv("AUTH0_CLIENT_SECRET");
        try {
            final JWTVerifier verifier = new JWTVerifier(secret);
            final Map<String, Object> claims = verifier.verify(token);
            values.put("loggedIn", true);
            values.put("claims", claims);
            return values;
        } catch (Exception e) {
            System.out.println("Invalid token");
            return values;
        }
    }
}