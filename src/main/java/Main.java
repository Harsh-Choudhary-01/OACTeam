import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;

import com.auth0.jwt.JWTVerifier;
import com.heroku.sdk.jdbc.DatabaseUrl;
import org.json.JSONArray;
import org.json.JSONObject;
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
    private static ArrayList<String> rolesList = new ArrayList<>();
    public static void main(String[] args)
    {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/spark/template/freemarker");
        rolesList.add("Divine");
        rolesList.add("Martial");
        rolesList.add("Assassin");
        rolesList.add("Marksman");
        rolesList.add("Blazer");
        rolesList.add("Garrison");
        rolesList.add("Elemental");
        rolesList.add("Stargazer");
        rolesList.add("Bloodseeker");
        rolesList.add("Guard");
        Connection connection = null;
        try {
            connection = DatabaseUrl.extract().getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users(userID text , name text , groups text[] DEFAULT '{}' , joinedRequests text[] DEFAULT '{}' ,  joiningRequests text[] DEFAULT '{}')");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS groups(name text , id text)");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS requests(id text , description text , timestamp timestamp NOT NULL DEFAULT NOW() , joinedInfo text[][] , owner text , joiningReq text[][] DEFAULT ARRAY[[NULL , NULL , NULL]]::text[][] , groups text[])");
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
                ArrayList<String[]> groupsArray = new ArrayList<>();
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
                            while(rs.next())
                                groupsArray.add(new String[]{s , rs.getString(1)});
                        }
                        for(String s : joinedRequests) {
                            rs = stmt.executeQuery("SELECT description , joinedInfo , owner , joiningReq from requests WHERE id = '" + s + "'");
                            Map<String , Object> requestItem = new HashMap<>();
                            if(rs.next()) {
                                requestItem.put("id", s);
                                requestItem.put("description", rs.getString(1));
                                requestItem.put("joinedInfo", rs.getArray(2).getArray());
                                if (userId.equals(rs.getString(3)))
                                    requestItem.put("joiningReq", rs.getArray(4).getArray());
                                joinedRequestsList.add(requestItem);
                            }
                        }
                        for(String s : joiningRequests) {
                            rs = stmt.executeQuery("SELECT description , joinedInfo from requests WHERE id = '" + s + "'");
                            Map<String , Object> requestItem = new HashMap<>();
                            if(rs.next()) {
                                requestItem.put("id", s);
                                requestItem.put("description", rs.getString(1));
                                requestItem.put("joinedInfo", rs.getArray(2).getArray());
                                joiningRequestsList.add(requestItem);
                            }
                        }
                        PreparedStatement statement = connection1.prepareStatement("SELECT id , description , joinedInfo from requests WHERE groups && ? AND NOT id = ANY(?) AND NOT id = ANY(?) ORDER BY id DESC LIMIT 20");
                        statement.setArray(1 , joinedGroups);
                        statement.setArray(2 , joinedReq);
                        statement.setArray(3 , joiningReq);
                        System.out.println(statement);
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
                attributes.put("groups" , groupsArray);
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

        post("/" , (request , response) -> {
            Map<String , Object> user = getUser(request);
            JSONObject jsonResponse = new JSONObject();
            JSONObject jsonReq = null;
            try {
                jsonReq = new JSONObject(request.body());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(jsonReq != null && (boolean) user.get("loggedIn")) {
                user = (Map<String , Object>)user.get("claims");
                String newId = newID();
                String userId = (String)user.get("user_id");
                if(jsonReq.getString("type").equals("addGroup"))
                    addGroup(jsonResponse , newId , userId , jsonReq.getString("name") , true);
                else if(jsonReq.getString("type").equals("joinGroup"))
                    addGroup(jsonResponse , jsonReq.getString("id") , userId , "" , false);
                else if(jsonReq.getString("type").equals("assignName"))
                    assignName(jsonResponse , jsonReq.getString("name") , userId);
                else if(jsonReq.getString("type").equals("createRequest")) {
                    try {
                        createRequest(jsonResponse, jsonReq.getJSONArray("groups"), jsonReq.getJSONArray("roles"), jsonReq.getString("description"), userId);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(jsonResponse.opt("success") == null)
                jsonResponse.put("success" , false);
            return String.valueOf(jsonResponse);
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

    private static String newID() {
        return new BigInteger(60, random).toString(32);
    }

    private static boolean checkAlphaNumeric(String s) {
        for(int i = 0 ; i < s.length() ; i++) {
            char c = s.charAt(i);
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c) && c != '.' && c != '!' && c != '$' && c != '%')
                return false;
        }
        return true;
    }

    private static void addGroup(JSONObject response , String groupID , String userId , String name , boolean creating) {
        Connection con = null;
        try {
            con = DatabaseUrl.extract().getConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO groups(name , id) VALUES (? , ?)");
            if (checkAlphaNumeric(name)) {
                if(creating) {
                    stmt.setString(1, name);
                    stmt.setString(2, groupID);
                    stmt.executeUpdate();
                    stmt = con.prepareStatement("UPDATE users SET groups = array_append(groups , ?::text) WHERE userID = ?");
                    stmt.setString(1, groupID);
                    stmt.setString(2, userId);
                    stmt.executeUpdate();
                    con.commit();
                }
                if(!creating) {
                    if(!checkAlphaNumeric(groupID))
                        return;
                    stmt = con.prepareStatement("SELECT name from groups WHERE id = ?");
                    stmt.setString(1 , groupID);
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next())
                        name = rs.getString(1);
                    if(name.equals(""))
                        return;
                    stmt = con.prepareStatement("UPDATE users SET groups = array_append(groups , ?::text) WHERE userID = ? AND NOT ? = ANY(groups)");
                    stmt.setString(1, groupID);
                    stmt.setString(2, userId);
                    stmt.setString(3 , groupID);
                    int update = stmt.executeUpdate();
                    if(update == 0) {
                        con.rollback();
                        return;
                    }
                    con.commit();
                }
                response.put("success" , true);
                response.put("name" , name);
                response.put("id" , groupID);
            }
        }
        catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            }
            catch (SQLException s) {
                s.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            if(con != null) try { con.close(); } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void assignName(JSONObject response , String userName , String userID) {
        Connection con = null;
        try {
            if(!checkAlphaNumeric(userName))
                return;
            con = DatabaseUrl.extract().getConnection();
            PreparedStatement stmt = con.prepareStatement("INSERT INTO users (name , userID) VALUES(? , ?)");
            stmt.setString(1 , userName);
            stmt.setString(2 , userID);
            stmt.executeUpdate();
            response.put("success" , true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(con != null) try { con.close(); } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createRequest(JSONObject response , JSONArray groups , JSONArray roles , String description , String userID) {
        Connection con = null;
        try {
            if(!checkAlphaNumeric(description) && description.length() > 100)
                return;
            System.out.println("Description ok");
            con = DatabaseUrl.extract().getConnection();
            String[] groupsArray = new String[groups.length()];
            PreparedStatement stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from users where ? = ANY(groups))");
            for(int i = 0 ; i < groups.length() ; i ++) //TODO: check that valid group and user is joined
            {
                stmt.setString(1 , groups.getString(i));
                groupsArray[i] = groups.getString(i);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    if (!rs.getBoolean(1))
                        return;
                }
                else
                    return;
            }
            System.out.println("Groups are ok");
            stmt = con.prepareStatement("SELECT name from users WHERE userID = ?");
            stmt.setString(1 , userID);
            ResultSet rs = stmt.executeQuery();
            String name = "";
            if(rs.next())
                name = rs.getString(1);
            else
                return;
            System.out.println("Name is gotten: " + name);
            String out = "";
            for(int i = 0 ; i < roles.length() ; i++) {
                if (!rolesList.contains(roles.getString(i)))
                    return;
                out += roles.getString(i) + "/";
            }
            out = out.substring(0 , out.length() - 1);
            String reqID = newID();
            String[][] joinedInfo = new String[1][];
            joinedInfo[0] = new String[]{userID , name , out};
            Array joinedInfoArray = con.createArrayOf("text" , joinedInfo);
            Array groupsSQL = con.createArrayOf("text" , groupsArray);
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO requests (id , description , joinedInfo , owner , groups) VALUES(? , ? , ? , ? , ?)");
            stmt.setString(1 , reqID);
            stmt.setString(2 , description);
            stmt.setArray(3 , joinedInfoArray);
            stmt.setString(4 , userID);
            stmt.setObject(5 , groupsSQL);
            stmt.executeUpdate();
            System.out.println("1 update done");
            joinedInfoArray.free();
            groupsSQL.free();
            stmt = con.prepareStatement("UPDATE users SET joinedRequests = array_append(joinedRequests , ?::text) WHERE userID = ?");
            stmt.setString(1 , reqID);
            stmt.setString(2 , userID);
            stmt.executeUpdate();
            con.commit();
            System.out.println("Second update done");
            response.put("success" , true);
        }
        catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            }
            catch(Exception s) {
                s.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            if(con != null) try { con.close(); } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}