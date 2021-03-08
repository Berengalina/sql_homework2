package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class DataHelper {

    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();


    public static void setUpLogin(RegistrationDto registeredDto) {
        given()
                .spec(requestSpec)
                .body(registeredDto)
                .when()
                .post("api/auth")
                .then()
                .statusCode(200);
    }

    public static RegistrationDto registerValidUser() {
        val validUser = new RegistrationDto(
                "vasya",
                "qwerty123"
        );
        setUpLogin(validUser);
        return validUser;
    }

    public static void setUpInvalidLogin(RegistrationDto registeredDto) {
        given()
                .spec(requestSpec)
                .body(registeredDto)
                .when()
                .post("api/auth")
                .then()
                .statusCode(400);
    }


    public static RegistrationDto registerInvalidUser() {
        val invalidUser = new RegistrationDto(
                "invalid",
                "invalid"
        );
        setUpInvalidLogin(invalidUser);
        return invalidUser;
    }

    public static void setUpCode(Code code) {
        Response response =
                given()
                        .spec(requestSpec)
                        .body(code)
                        .when()
                        .post("api/auth/verification")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        String auth_token = response.path("token");
        assertThat(auth_token, equalTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY"));
    }


    public static Code validCode() throws SQLException {
        val validCode = new Code(
                "vasya",
                getVerificationCodefromDB()
        );
        setUpCode(validCode);
        return validCode;
    }

    public static void setUpInvalidCode(Code code) {
        given()
                .spec(requestSpec)
                .body(code)
                .when()
                .post("api/auth/verification")
                .then()
                .statusCode(400);
    }

    public static Code invalidCode() {
        val invalidCode = new Code(
                "vasya",
                "0000000"
        );
        setUpInvalidCode(invalidCode);
        return invalidCode;
    }


    public static void setUpTransfer(Cards card) {
        Code code = new Code();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY")
                .body(card)
                .when()
                .post("api/transfer")
                .then()
                .statusCode(200);
    }


    public static void validTransferFromSecondToFirst() {
        val card1 = new Cards(
                "5559 0000 0000 0002",
                "5559 0000 0000 0001",
                "1000"
        );
        setUpTransfer(card1);
    }

    public static void validTransferFromFirstToAnother() {
        val card1 = new Cards(
                "5559 0000 0000 0001",
                "5559 0000 0000 0008",
                "999"
        );
        setUpTransfer(card1);
    }

    public static void setUpInvalidTransfer(Cards card) {
        Code code = new Code();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY")
                .body(card)
                .when()
                .post("api/transfer")
                .then()
                .statusCode(400);
    }

    public static void invalidTransferFromSecondToInvalid() {
        val card1 = new Cards(
                "5559 0000 0000 0002",
                "5559 0000 0000",
                "5000"
        );
        setUpInvalidTransfer(card1);
    }

    public static void invalidTransferFromSecondToSecond() {
        val card1 = new Cards(
                "5559 0000 0000 0002",
                "5559 0000 0000 0002",
                "1000"
        );
        setUpInvalidTransfer(card1);
    }


    public static void invalidTransferFromSecondToFirstExtendAmount() {
        val card1 = new Cards(
                "5559 0000 0000 0002",
                "5559 0000 0000 0001",
                "20000"
        );
        setUpInvalidTransfer(card1);
    }

    public static void invalidTransferFromSecondToFirstInvalidAmount() {
        val card1 = new Cards(
                "5559 0000 0000 0002",
                "5559 0000 0000 0001",
                "-100"
        );
        setUpInvalidTransfer(card1);
    }

    public static void returnDemoAccounts10000() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY")
                .when()
                .get("api/cards")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].number", equalTo("**** **** **** 0002"))
                .body("[0].balance", equalTo(10000))
                .body("[1].number", equalTo("**** **** **** 0001"))
                .body("[1].balance", equalTo(10000));
    }

    public static void returnDemoAccountsAfterSuccessFromFirstToAnother() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY")
                .when()
                .get("api/cards")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].balance", equalTo(9000))
                .body("[1].balance", equalTo(10001));
    }

    public static void returnDemoAccountsAfterSuccessFromSecontToFirst() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpbiI6InZhc3lhIn0.JmhHh8NXwfqktXSFbzkPohUb90gnc3yZ9tiXa0uUpRY")
                .when()
                .get("api/cards")
                .then()
                .statusCode(200)
                .body("", hasSize(2))
                .body("[0].balance", equalTo(9000))
                .body("[1].balance", equalTo(11000));
    }

    public static String getVerificationCodefromDB() throws SQLException {
        val usersSQL = "SELECT code FROM auth_codes WHERE user_id in (SELECT id FROM users WHERE login='vasya');";
        val runner = new QueryRunner();
        val conn = DriverManager.getConnection("jdbc:mysql://localhost:3000/app_db", "app", "pass");
        val auth_code = runner.query(conn, usersSQL, new BeanHandler<>(DB_Code.class));
        return auth_code.getCode();
    }


}

