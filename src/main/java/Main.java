import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        /*
        TODO: View requests with a group dropdown
        Leave groups by clicking on the group list
        Give options to reject and accept requests
        Change owner if requests aren't accepted or declined for 10 minutes
        Allow notifications through text/email and see if possible to download a notification app that allows third party
        Make UI for accepting/rejecting requests better , multiple vex pop ups , three buttons(cancel , ok , reject) close x
         */
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
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS requests(id text , description text , timestamp timestamp NOT NULL DEFAULT NOW() , joinedInfo text[][] , owner text , joiningReq text[][] DEFAULT '{}' , groups text[] , joinedUsers text[])");
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
            //TODO: make groups section where user can view their id for their groups
            //TODO: verification for creating request , filling some role and group and description both client and server
            //TODO: add better ui for accepting user , one button to view request then multiple vex dialogs with each req
            //TODO: reject user , accept user , or also cancel and make decision later
            //TODO: handle cancel request if already been accepted , handle leaving request if does not exist anymore , etc
            // think of all possible options ^
            //TODO: leaving request changes joined info of all requests double check
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
                        PreparedStatement statement = connection1.prepareStatement("SELECT name from groups WHERE id = ?");
                        for(String s : groups)
                        {
                            statement.setString(1 , s);
                            rs = statement.executeQuery();
                            while(rs.next())
                                groupsArray.add(new String[]{s , rs.getString(1)});
                        }
                        statement = connection1.prepareStatement("SELECT description , joinedInfo , owner , joiningReq from requests WHERE id = ?");
                        for(String s : joinedRequests) {
                            statement.setString(1 , s);
                            rs = statement.executeQuery();
                            Map<String , Object> requestItem = new HashMap<>();
                            if(rs.next()) {
                                requestItem.put("id", s);
                                requestItem.put("description", rs.getString(1));
                                requestItem.put("joinedInfo", rs.getArray(2).getArray());
                                if (userId.equals(rs.getString(3))) {
                                    JSONArray array = new JSONArray(rs.getArray(4).getArray());
                                    requestItem.put("joiningReq", array.length() == 0 ? "" : array.toString());
                                    requestItem.put("owner" , true);
                                }
                                else
                                    requestItem.put("owner" , false);
                                joinedRequestsList.add(requestItem);
                            }
                        }
                        statement = connection1.prepareStatement("SELECT description , joinedInfo from requests WHERE id = ?");
                        for(String s : joiningRequests) {
                            statement.setString(1 , s);
                            rs = statement.executeQuery();
                            Map<String , Object> requestItem = new HashMap<>();
                            if(rs.next()) {
                                requestItem.put("id", s);
                                requestItem.put("description", rs.getString(1));
                                requestItem.put("joinedInfo", rs.getArray(2).getArray());
                                joiningRequestsList.add(requestItem);
                            }
                        }
                        statement = connection1.prepareStatement("SELECT id , description , joinedInfo from requests WHERE groups && ? AND NOT id = ANY(?) AND NOT id = ANY(?) ORDER BY id DESC LIMIT 21 OFFSET ?");
                        statement.setArray(1 , joinedGroups);
                        statement.setArray(2 , joinedReq);
                        statement.setArray(3 , joiningReq);
                        statement.setInt(4 , 0);
                        rs = statement.executeQuery();
                        while(rs.next()) {
                            Map<String , Object> requestItem = new HashMap<>();
                            requestItem.put("id" , rs.getString(1));
                            requestItem.put("description" , rs.getString(2));
                            requestItem.put("joinedInfo" , rs.getArray(3).getArray());
                            requests.add(requestItem);
                        }
                        if(requests.size() == 21) {
                            attributes.put("moreRequests" , true);
                            requests.remove(20);
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
            attributes.put("firstInd" , 0);
            attributes.put("lessRequests" , false);
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
                if ((boolean) user.get("loggedIn")) {
                    user = (Map<String, Object>) user.get("claims");
                    String newId = newID();
                    String userId = (String) user.get("user_id");
                    if (jsonReq.getString("type").equals("addGroup"))
                        addGroup(jsonResponse, newId, userId, jsonReq.getString("name"), true);
                    else if (jsonReq.getString("type").equals("joinGroup"))
                        addGroup(jsonResponse, jsonReq.getString("id"), userId, "", false);
                    else if (jsonReq.getString("type").equals("assignName"))
                        assignName(jsonResponse, jsonReq.getString("name"), userId);
                    else if(jsonReq.getString("type").equals("refresh")) {
                        int offset = jsonReq.getInt("current");
                        if(jsonReq.getBoolean("change")) {
                            if(jsonReq.getBoolean("prev"))
                                offset -= 20;
                            else
                                offset += 20;
                        }
                        refresh(jsonResponse , userId , jsonReq.getJSONArray("groups") , offset);
                    }
                    else if (jsonReq.getString("type").equals("createRequest"))
                        createRequest(jsonResponse, jsonReq.getJSONArray("groups"), jsonReq.getJSONArray("roles"), jsonReq.getString("description"), userId);
                    else if(jsonReq.getString("type").equals("leaveRequest"))
                        leaveRequest(jsonResponse , userId , jsonReq.getString("id") , false);
                    else if(jsonReq.getString("type").equals("cancelRequest"))
                        leaveRequest(jsonResponse , userId , jsonReq.getString("id") , true);
                    else if(jsonReq.getString("type").equals("joinRequest"))
                        joinRequest(jsonResponse , userId , jsonReq.getString("id") , jsonReq.getJSONArray("roles"));
                    else if(jsonReq.getString("type").equals("acceptRequest"))
                        acceptRequest(jsonResponse , userId , jsonReq.getString("user") , jsonReq.getString("id"));
                    else if(jsonReq.getString("type").equals("leaveGroup"))
                        leaveGroup(jsonResponse , userId , jsonReq.getString("id"));
                    else if(jsonReq.getString("type").equals("removeUser")) {
                        Connection con = null;
                        try {
                            con = DatabaseUrl.extract().getConnection();
                            PreparedStatement stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from requests WHERE owner = ? AND id = ?)");
                            stmt.setString(1 , userId);
                            stmt.setString(2 , jsonReq.getString("id"));
                            ResultSet rs = stmt.executeQuery();
                            if(rs.next() && rs.getBoolean(1)) {
                                leaveRequest(jsonResponse , jsonReq.getString("user") , jsonReq.getString("id") , false);
                            }
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
                    else if(jsonReq.getString("type").equals("rejectRequest")) {
                        Connection con = null;
                        try {
                            con = DatabaseUrl.extract().getConnection();
                            PreparedStatement stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from requests WHERE owner = ? AND id = ?)");
                            stmt.setString(1 , userId);
                            stmt.setString(2 , jsonReq.getString("id"));
                            ResultSet rs = stmt.executeQuery();
                            if(rs.next() && rs.getBoolean(1))
                                leaveRequest(jsonResponse , jsonReq.getString("user") , jsonReq.getString("id") , true);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        finally {
                            if(con != null) try { con.close(); } catch(SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
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

    private static void refresh(JSONObject response , String userID , JSONArray groups , int offset ) {
        Connection con = null;
        if(offset < 0)
            offset = 0;
        System.out.println("Offset: " + offset);
        try {
            con = DatabaseUrl.extract().getConnection();
            String[] groupArray = new String[groups.length()];
            for(int i = 0 ; i < groups.length(); i++)
                groupArray[i] = groups.getString(i);
            Array sqlGroups = con.createArrayOf("text" , groupArray);
            PreparedStatement stmt = con.prepareStatement("SELECT id , description , joinedInfo from requests , users WHERE userID = ? AND requests.groups && ? AND NOT id = ANY(users.joinedRequests) AND NOT id = ANY(users.joiningRequests) ORDER BY id DESC LIMIT 21 OFFSET ?");
            stmt.setArray(2 , sqlGroups);
            stmt.setString(1 , userID);
            stmt.setInt(3 , offset);
            ResultSet rs = stmt.executeQuery();
            JSONArray requests = new JSONArray();
            response.put("firstInd" , offset);
            if(offset != 0)
                response.put("previous" , true);
            else
                response.put("previous" , false);
            while (rs.next()) {
                JSONObject requestItem = new JSONObject();
                requestItem.put("id" , rs.getString(1));
                requestItem.put("description" , rs.getString(2));
                requestItem.put("joinedInfo" , rs.getArray(3).getArray());
                requests.put(requestItem);
            }
            if(requests.length() == 21) {
                response.put("next", true);
                requests.remove(20);
            }
            else
                response.put("next" , false);
            response.put("requests" , requests);
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

    private static void leaveGroup(JSONObject response , String userID , String groupID) {
        Connection con = null;
        try {
            con = DatabaseUrl.extract().getConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE users SET groups = array_remove(groups , ?::text) WHERE userID = ?");
            stmt.setString(1 , groupID);
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

    private static void joinRequest(JSONObject response , String userID , String reqID  , JSONArray roles) {
        Connection con = null;
        try {
            if(!checkAlphaNumeric(reqID))
                return;
            if(roles.length() == 0)
                return;
            con = DatabaseUrl.extract().getConnection();
            String out = "";
            for(int i = 0 ; i < roles.length() ; i++) {
                if (!rolesList.contains(roles.getString(i)))
                    return;
                out += roles.getString(i) + "/";
            }
            out = out.substring(0 , out.length() - 1);
            PreparedStatement stmt = con.prepareStatement("SELECT name from users WHERE userID = ?");
            stmt.setString(1 , userID);
            String name;
            ResultSet rs = stmt.executeQuery();
            if(rs.next())
                name = rs.getString(1);
            else
                return;
            stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 FROM requests WHERE id = ?)");
            stmt.setString(1 , reqID);
            rs = stmt.executeQuery();
            if(!rs.next() || !rs.getBoolean(1))
                return;
            stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from requests , users WHERE ? = users.userID AND ? = requests.id AND requests.groups && users.groups)");
            stmt.setString(1 , userID);
            stmt.setString(2 , reqID);
            rs = stmt.executeQuery();
            if(!rs.next() || !rs.getBoolean(1))
                return;
            String[][] joiningInfo = new String[1][];
            joiningInfo[0] = new String[]{userID , name , out};
            Array sqlArray = con.createArrayOf("text" , joiningInfo);
            con.setAutoCommit(false);
            stmt = con.prepareStatement("UPDATE requests SET joiningReq = array_cat(joiningReq , ?) WHERE id = ?");
            stmt.setArray(1 , sqlArray);
            stmt.setString(2 , reqID);
            stmt.executeUpdate();
            stmt = con.prepareStatement("UPDATE users SET joiningRequests = array_append(joiningRequests , ?::text) WHERE userID = ?");
            stmt.setString(1 , reqID);
            stmt.setString(2 , userID);
            stmt.executeUpdate();
            con.commit();
            response.put("success" , true);
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

    private static void acceptRequest(JSONObject response , String userID , String requestingUser , String reqID) {
        Connection con = null;
        try {
            con = DatabaseUrl.extract().getConnection();
            if(!checkAlphaNumeric(requestingUser) || !checkAlphaNumeric(reqID))
                return;
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("SELECT ? = ANY(joiningRequests) from users WHERE userID = ?");
            stmt.setString(1 , reqID);
            stmt.setString(2 , requestingUser);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next() || !rs.getBoolean(1))
                return;
            stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 FROM requests WHERE owner = ? AND id = ?)");
            stmt.setString(1 , userID);
            stmt.setString(2 , reqID);
            rs = stmt.executeQuery();
            if(!rs.next() || !rs.getBoolean(1))
                return;
            stmt = con.prepareStatement("UPDATE users SET joinedRequests = array_append(joinedRequests , ?::text) WHERE userID = ?");
            stmt.setString(1 , reqID);
            stmt.setString(2 , requestingUser);
            stmt.executeUpdate();
            stmt = con.prepareStatement("UPDATE users SET joiningRequests = array_remove(joiningRequests, ?::text) WHERE userID = ?");
            stmt.setString(1 , reqID);
            stmt.setString(2 , requestingUser);
            stmt.executeUpdate();
            String[][] array = new String[1][1];
            array[0][0] = requestingUser;
            Array sqlArray = con.createArrayOf("text" , array);
            stmt = con.prepareStatement("SELECT a.joiningReq[i:i] FROM requests a JOIN LATERAL generate_subscripts(a.joiningReq , 1) i " +
                    "on a.joiningReq[i:i][1:1] = ? WHERE id = ? LIMIT 1");
            stmt.setArray(1 , sqlArray);
            stmt.setString(2 , reqID);
            rs = stmt.executeQuery();
            if(!rs.next())
                return;
            Array joinedInfo = rs.getArray(1);
            stmt = con.prepareStatement("UPDATE requests SET joinedInfo = array_cat(joinedInfo , ?) WHERE id = ?");
            stmt.setArray(1 , joinedInfo);
            stmt.setString(2 , reqID);
            stmt.executeUpdate();
            stmt = con.prepareStatement("UPDATE requests SET joiningReq = ARRAY(SELECT ARRAY(SELECT unnest(a.joiningReq[i:i])) FROM " +
                    "requests a JOIN LATERAL generate_subscripts(a.joiningReq , 1) i on a.joiningReq[i:i][1:1] != " +
                    "? WHERE id = ?)");
            stmt.setArray(1 , sqlArray);
            stmt.setString(2 , reqID);
            stmt.executeUpdate();
            stmt = con.prepareStatement("UPDATE requests SET joinedUsers = array_append(joinedUsers , ?::text) WHERE id = ?");
            stmt.setString(1 , requestingUser);
            stmt.setString(2 , reqID);
            stmt.executeUpdate();
            con.commit();
            response.put("success" , true);
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

    private static String newID() {
        return new BigInteger(60, random).toString(32);
    }

    private static boolean checkAlphaNumeric(String s) {
        for(int i = 0 ; i < s.length() ; i++) {
            char c = s.charAt(i);
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c) && c != '.' && c != '!' && c != '$'  && c != '|' && c != '-')
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
            if(!checkAlphaNumeric(description) || description.length() > 100 || description.length() == 0 || roles.length() == 0 || groups.length() == 0)
                return;
            System.out.println("Description ok");
            con = DatabaseUrl.extract().getConnection();
            String[] groupsArray = new String[groups.length()];
            PreparedStatement stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from users where ? = userID AND ? = ANY(groups))");
            for(int i = 0 ; i < groups.length() ; i ++)
            {
                stmt.setString(1 , userID);
                stmt.setString(2 , groups.getString(i));
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
            String name;
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
            String[] joinedUsers = new String[1];
            joinedUsers[0] = userID;
            joinedInfo[0] = new String[]{userID , name , out};
            Array joinedSQL = con.createArrayOf("text" , joinedUsers);
            Array joinedInfoArray = con.createArrayOf("text" , joinedInfo);
            Array groupsSQL = con.createArrayOf("text" , groupsArray);
            con.setAutoCommit(false);
            stmt = con.prepareStatement("INSERT INTO requests (id , description , joinedInfo , owner , groups , joinedUsers) VALUES(? , ? , ? , ? , ? , ?)");
            stmt.setString(1 , reqID);
            stmt.setString(2 , description);
            stmt.setArray(3 , joinedInfoArray);
            stmt.setString(4 , userID);
            stmt.setObject(5 , groupsSQL);
            stmt.setArray(6 , joinedSQL);
            stmt.executeUpdate();
            System.out.println("1 update done");
            joinedInfoArray.free();
            groupsSQL.free();
            joinedSQL.free();
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

    private static void leaveRequest(JSONObject response , String userID , String reqID , boolean canceling) {
        Connection con = null;
        System.out.println("Leaving , cancelling: " + canceling);
        try {
            if(!checkAlphaNumeric(reqID))
                return;
            con = DatabaseUrl.extract().getConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt;
            if(!canceling) {
                stmt = con.prepareStatement("SELECT owner , joinedUsers FROM requests WHERE id = ? AND ? = ANY(joinedUsers)");
                stmt.setString(1 , reqID);
                stmt.setString(2 , userID);
            }
            else {
                stmt = con.prepareStatement("SELECT EXISTS(SELECT 1 from users WHERE userID = ? AND ? = ANY(joiningRequests))");
                stmt.setString(1 , userID);
                stmt.setString(2 , reqID);
                System.out.println(stmt);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                    if (!rs.getBoolean(1))
                        return;
                }
                else
                    return;
                stmt = con.prepareStatement("SELECT owner FROM requests WHERE id = ?");
                stmt.setString(1 , reqID);
            }
            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                String owner = rs.getString(1);
                System.out.println("Owner for requesT: " + reqID + " is " + owner + " , posting user is: " + userID);
                if(owner.equals(userID)) {
                    String[] joined = (String[])rs.getArray(2).getArray();
                    if(joined.length == 1)
                    {
                        con.setAutoCommit(false);
                        stmt = con.prepareStatement("DELETE from requests WHERE id = ?");
                        stmt.setString(1 , reqID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("UPDATE users SET joinedRequests = array_remove(joinedRequests , ?::text)");
                        stmt.setString(1 , reqID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("UPDATE users SET joiningRequests = array_remove(joiningRequests , ?::text)");
                        stmt.setString(1 , reqID);
                        stmt.executeUpdate();
                        con.commit();
                    }
                    else {
                        String s = ""; //Who new owner is
                        for(String join : joined) {
                            System.out.println("Joined user: " + join);
                            if (!join.equals(owner)) {
                                System.out.println("NOt equal to owner " + join);
                                s = join;
                                break;
                            }
                        }
                        System.out.println(s);
                        con.setAutoCommit(false);
                        stmt = con.prepareStatement("UPDATE requests SET owner = ? WHERE id = ?");
                        stmt.setString(1 , s);
                        stmt.setString(2 , reqID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("UPDATE users SET joinedRequests = array_remove(joinedRequests , ?::text) WHERE userID = ?");
                        stmt.setString(1 , reqID);
                        stmt.setString(2 , userID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("UPDATE users SET joiningRequests = array_remove(joiningRequests , ?::text) WHERE userID = ?");
                        stmt.setString(1 , reqID);
                        stmt.setString(2 , userID);
                        stmt.executeUpdate();
                        stmt = con.prepareStatement("UPDATE requests SET joinedUsers = array_remove(joinedUsers , ?::text) WHERE id = ?");
                        stmt.setString(2 , reqID);
                        stmt.setString(1 , userID);
                        stmt.executeUpdate();
                        String[][] array = new String[1][1];
                        array[0][0] = userID;
                        Array sqlArray = con.createArrayOf("text" , array);
                        PreparedStatement deleteUser = con.prepareStatement("UPDATE requests SET joinedInfo = ARRAY(SELECT ARRAY(SELECT unnest(a.joinedInfo[i:i])) FROM " +
                                "requests a JOIN LATERAL generate_subscripts(a.joinedInfo , 1) i on a.joinedInfo[i:i][1:1] != " +
                                "? WHERE id = ?) WHERE id = ?");
                        PreparedStatement deleteJoining = con.prepareStatement("UPDATE requests SET joiningReq = ARRAY(SELECT ARRAY(SELECT unnest(a.joiningReq[i:i])) FROM " +
                                "requests a JOIN LATERAL generate_subscripts(a.joiningReq , 1) i on a.joiningReq[i:i][1:1] != " +
                                "? WHERE id = ?) WHERE id = ?");
                        deleteUser.setArray(1 , sqlArray);
                        deleteUser.setString(2 , reqID);
                        deleteUser.setString(3 , reqID);
                        deleteJoining.setArray(1 , sqlArray);
                        deleteJoining.setString(2 , reqID);
                        deleteJoining.setString(3 , reqID);
                        deleteUser.executeUpdate();
                        deleteJoining.executeUpdate();
                        con.commit();
                    }
                }
                else {
                    con.setAutoCommit(false);
                    stmt = con.prepareStatement("UPDATE users SET joinedRequests = array_remove(joinedRequests , ?::text) WHERE userID = ?");
                    stmt.setString(1 , reqID);
                    stmt.setString(2 , userID);
                    stmt.executeUpdate();
                    stmt = con.prepareStatement("UPDATE users SET joiningRequests = array_remove(joiningRequests , ?::text) WHERE userID = ?");
                    stmt.setString(1 , reqID);
                    stmt.setString(2 , userID);
                    stmt.executeUpdate();
                    stmt = con.prepareStatement("UPDATE requests SET joinedUsers = array_remove(joinedUsers , ?::text) WHERE id = ?");
                    stmt.setString(2 , reqID);
                    stmt.setString(1 , userID);
                    stmt.executeUpdate();
                    String[][] array = new String[1][1];
                    array[0][0] = userID;
                    Array sqlArray = con.createArrayOf("text" , array);
                    PreparedStatement deleteUser = con.prepareStatement("UPDATE requests SET joinedInfo = ARRAY(SELECT ARRAY(SELECT unnest(a.joinedInfo[i:i])) FROM " +
                            "requests a JOIN LATERAL generate_subscripts(a.joinedInfo , 1) i on a.joinedInfo[i:i][1:1] != " +
                            "? WHERE id = ?) WHERE id = ?");
                    PreparedStatement deleteJoining = con.prepareStatement("UPDATE requests SET joiningReq = ARRAY(SELECT ARRAY(SELECT unnest(a.joiningReq[i:i])) FROM " +
                            "requests a JOIN LATERAL generate_subscripts(a.joiningReq , 1) i on a.joiningReq[i:i][1:1] != " +
                            "? WHERE id = ?) WHERE id = ?");
                    deleteUser.setArray(1 , sqlArray);
                    deleteUser.setString(2 , reqID);
                    deleteUser.setString(3 , reqID);
                    deleteJoining.setArray(1 , sqlArray);
                    deleteJoining.setString(2 , reqID);
                    deleteJoining.setString(3 , reqID);
                    deleteUser.executeUpdate();
                    deleteJoining.executeUpdate();
                    con.commit();
                }
                response.put("success" , true);
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
}