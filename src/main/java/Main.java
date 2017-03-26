import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
public class Main
{
    //static String clientId = System.getenv("AUTH0_CLIENT_ID");
    //static String clientDomain = System.getenv("AUTH0_DOMAIN");
    private static SecureRandom random = new SecureRandom();
    public static final String X_FORWARDED_PROTO = "x-forwarded-proto";
    public static void main(String[] args)
    {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/spark/template/freemarker");
        get("/", (request, response) ->
        {
            Map<String, Object> attributes = new HashMap<>();
            Map<String , Object> user = new HashMap<>();
//            user = getUser(request);
            attributes.put("user" , user.get("claims"));
            attributes.put("loggedIn" , user.get("loggedIn"));
//            attributes.put("clientId" , clientId);
//            attributes.put("clientDomain" , clientDomain);
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
    }
}