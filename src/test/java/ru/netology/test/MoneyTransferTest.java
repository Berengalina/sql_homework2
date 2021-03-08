package ru.netology.test;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;


import java.sql.DriverManager;
import java.sql.SQLException;


import static ru.netology.data.DataHelper.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MoneyTransferTest {

    @Order(1)
    @Test
    void shouldSuccessLoginAndVerifyAndTransferFromSecondToFirst() throws SQLException {
        val user = DataHelper.registerValidUser();
        val code = DataHelper.validCode();
        returnDemoAccounts10000();
        validTransferFromSecondToFirst();
        returnDemoAccountsAfterSuccessFromSecontToFirst();
    }

    @Order(2)
    @Test
    void shouldNotLoginInvalidUser(){
        val user = DataHelper.registerInvalidUser();
    }

    @Order(3)
    @Test
    void shouldNotVerifyInvalidCode(){
        val code = DataHelper.invalidCode();
    }

    @Order(4)
    @Test
    void shouldISuccessTransferFromFirstToAnother()  {
        returnDemoAccountsAfterSuccessFromSecontToFirst();
        validTransferFromFirstToAnother();
        returnDemoAccountsAfterSuccessFromFirstToAnother();
    }

    @Order(5)
    @Test
    void shouldNotTransferFromSecondToSecond(){
        invalidTransferFromSecondToSecond();
    }

    @Order(6)
    @Test
    void shouldNotTransferFromSecondToInvalid(){
        invalidTransferFromSecondToInvalid();
    }

    @Order(7)
    @Test
    void shouldNotTransferExtendAmount(){
        invalidTransferFromSecondToFirstExtendAmount();
    }

    @Order(8)
    @Test
    void shouldNotTransferInvalidAmount(){
        invalidTransferFromSecondToFirstInvalidAmount();
    }

    @AfterAll
    static void cleaningDB() throws SQLException {
        String dataSQL_users = "DELETE FROM users";
        String dataSQL_cards = "DELETE FROM cards";
        String dataSQL_auth_codes = "DELETE FROM auth_codes";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection("jdbc:mysql://localhost:3000/app_db", "app", "pass");
        ) {
            runner.update(conn, dataSQL_cards);
            runner.update(conn, dataSQL_auth_codes);
            runner.update(conn, dataSQL_users);
        }
    }

}