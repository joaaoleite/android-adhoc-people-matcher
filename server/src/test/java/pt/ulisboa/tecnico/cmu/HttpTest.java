package pt.ulisboa.tecnico.cmu;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import spark.QueryParamsMap;
import org.json.JSONObject;

import java.util.HashMap;
import pt.ulisboa.tecnico.cmu.server.HTTP;

public class HttpTest extends TestCase {

    private HTTP http;
    private int port;

    protected void setUp(){
        port = 8080;
        http = new HTTP(port);
    }

    public void testServer(){
        http.POST("/test", (params, username)->{
            System.out.println("PARAMS"+params);
            return new JSONObject("{\"status\":\"ok\"}");
        });
    }
}
