package ru.netology.test;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;

import java.sql.DriverManager;
import java.sql.SQLException;


import static ru.netology.data.DataHelper.*;


public class MoneyTransferTest {

    @Test
    void shouldSuccessLoginAndVerifyAndTransferFromSecondToFirst() throws SQLException {
        val user = DataHelper.registerValidUser();
        val code = DataHelper.validCode();
        returnDemoAccounts10000();
        validTransferFromSecondToFirst();
        returnDemoAccountsAfterSuccessFromSecontToFirst();
    }

    @Test
    void shouldNotLoginInvalidUser(){
        val user = DataHelper.registerInvalidUser();
    }

    @Test
    void shouldNotVerifyInvalidCode(){
        val code = DataHelper.invalidCode();
    }

    @Test
    void shouldSuccessTransferFromFirstToAnother()  {
        returnDemoAccountsAfterSuccessFromSecontToFirst();
        validTransferFromFirstToAnother();
        returnDemoAccountsAfterSuccessFromFirstToAnother();
    }

    @Test
    void shouldNotTransferFromSecondToSecond(){
        invalidTransferFromSecondToSecond();
    }

    @Test
    void shouldNotTransferFromSecondToInvalid(){
        invalidTransferFromSecondToInvalid();
    }

    @Test
    void shouldNotTransferExtendAmount(){
        invalidTransferFromSecondToFirstExtendAmount();
    }

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