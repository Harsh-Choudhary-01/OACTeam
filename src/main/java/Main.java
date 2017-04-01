import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static spark.Spark.*;

import com.auth0.jwt.JWTVerifier;
import com.heroku.sdk.jdbc.DatabaseUrl;
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
        Connection connection = null;
        try {
            connection = DatabaseUrl.extract().getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users(userID text , name text , groups text[] , joinedRequests text[] ,  joiningRequests text[])");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groups(name text , id text)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS requests(id text , description text , timestamp timestamp NOT NULL DEFAULT NOW() , joinedInfo text[][] , owner text , joiningReq text[][] , groups text[])");
            //Joined info is 2d array with each row being 1 person : row[0] - id , row[1] - name , row[2] - 1 or 0 bool tank , row[3] - 1 or 0 bool dps , row[4] - 1 or 0 bool heal , row[5] int rep class(concat diff type) 12 1 for warrior 2 for monk etc
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(connection != null) try { connection.close(); } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        get("/", (request, response) ->
        {
            Map<String, Object> attributes = new HashMap<>();
            Map<String , Object> user = getUser(request);
            attributes.put("loggedIn" , user.get("loggedIn"));
            if((boolean) attributes.get("loggedIn")) {
                user = (Map<String , Object>) user.get("claims");
                String userId = (String)user.get("user_id");
                Connection connection1 = null;
                boolean nameGiven = false;
                Map<String , String> groupNames = new LinkedHashMap<>(); //Contains groupID as key and group name as valu
                ArrayList<Map<String , Object>> joinedRequestsList = new ArrayList<>();
                ArrayList<Map<String , Object>> joiningRequestsList = new ArrayList<>();
                ArrayList<Map<String , Object>> requests = new ArrayList<>();
                try {
                    connection1 = DatabaseUrl.extract().getConnection();
                    Statement stmt = connection1.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT name , groups , joinedRequests , joiningRequests from users WHERE userID = '" + userId + "'");
                    if(rs.next()) {
                        nameGiven = true;
                        attributes.put("name" , rs.getString(1));
                        Array joinedGroups = rs.getArray(2);
                        Array joinedReq = rs.getArray(3);
                        Array joiningReq = rs.getArray(4);
                        String[] groups = (String[])joinedGroups.getArray();
                        String[] joinedRequests = (String[])joinedReq.getArray();
                        String[] joiningRequests = (String[])joiningReq.getArray();
                        for(String s : groups)
                        {
                            rs = stmt.executeQuery("SELECT name from groups where id = '" + s + "'"); //Get groupNames
                            groupNames.put(s , rs.getString(1));
                        }
                        for(String s : joinedRequests) {
                            rs = stmt.executeQuery("SELECT description , joinedInfo , owner , joiningReq from requests WHERE id = '" + s + "'");
                            Map<String , Object> requestItem = new HashMap<>();
                            requestItem.put("id" , s);
                            requestItem.put("description" , rs.getString(1));
                            requestItem.put("joinedInfo" , rs.getArray(2).getArray(2 , 5));
                            if(userId.equals(rs.getString(3)))
                                requestItem.put("joiningReq" , rs.getArray(3).getArray(2 , 5));
                            joinedRequestsList.add(requestItem);
                        }
                        for(String s : joiningRequests) {
                            rs = stmt.executeQuery("SELECT description , joinedInfo , owner , joiningReq from requests WHERE id = '" + s + "'");
                            Map<String , Object> requestItem = new HashMap<>();
                            requestItem.put("id" , s);
                            requestItem.put("description" , rs.getString(1));
                            requestItem.put("joinedInfo" , rs.getArray(2).getArray(2 , 5));
                            if(userId.equals(rs.getString(3)))
                                requestItem.put("joiningReq" , rs.getArray(3).getArray(2 , 5));
                            joiningRequestsList.add(requestItem);
                        }
                        PreparedStatement statement = connection1.prepareStatement("SELECT id , description , joinedInfo , owner , joiningReq from requests ORDER BY id DESC LIMIT 20 WHERE groups && ? AND NOT ? @> id AND NOT ? @> id");
                        statement.setArray(1 , joinedGroups);
                        statement.setArray(2 , joinedReq);
                        statement.setArray(3 , joiningReq);
                        rs = statement.executeQuery();
                        while(rs.next()) {
                            Map<String , Object> requestItem = new HashMap<>();
                            requestItem.put("id" , rs.getString(1));
                            requestItem.put("description" , rs.getString(2));
                            requestItem.put("joinedInfo" , rs.getArray(3).getArray());
                            requests.add(requestItem);
                        }
                    }
                    attributes.put("nameGiven" , nameGiven);
                }
                catch (Exception e) {
                   e.printStackTrace();
                }
                finally {
                    if(connection1 != null) try { connection1.close(); } catch(SQLException e) {
                        e.printStackTrace();
                    }
                }
                attributes.put("groups" , groupNames);
                attributes.put("joinedRequests" , joinedRequestsList);
                attributes.put("joiningRequests" , joiningRequestsList);
                attributes.put("requests" , requests);
            }
            attributes.put("user" , user);
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

    private String newID() {
        return new BigInteger(30, random).toString(32);
    }
}