package util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import support.PropertyReader;

public class ApiClient {
    static {
        PropertyReader.loadProperties("config/test.properties");
    }


    static {
        RestAssured.baseURI = PropertyReader.getProperty("baseUrlApi");
        RestAssured.useRelaxedHTTPSValidation();
    }

    public static Response get(String path) {
        return RestAssured
                .given()
                .header("User-Agent", "Mozilla/5.0")
                .accept("application/json")
                .when()
                .get(path)
                .then()
                .extract().response();
    }

    public static Response postVerifyLogin(String email, String password) {
        return RestAssured
                .given()
                .header("User-Agent", "Mozilla/5.0")
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .accept("application/json")
                .formParam("email", email)
                .formParam("password", password)
                .when()
                .post("/verifyLogin")
                .then()
                .extract().response();
    }
}
