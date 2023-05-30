DROP ALIAS IF EXISTS gen_random_id;

CREATE ALIAS gen_random_id as '
    import java.util.UUID;
    @CODE
    java.util.UUID getRandomUuid() throws Exception {
      return UUID.randomUUID();
    }
';

DROP TABLE IF EXISTS "USERS";
CREATE TABLE "USERS" (
                         user_id INTEGER NOT NULL,
                         user_name VARCHAR(100) NOT NULL,
                         user_account uuid NOT NULL,
                         PRIMARY KEY (user_id)
);

INSERT INTO USERS (user_id,user_name, user_account) VALUES (1020486382,'Andres Villa', gen_random_id());
INSERT INTO USERS (user_id,user_name, user_account) VALUES (1020486383,'Carlos Giraldo', gen_random_id());

DROP TABLE IF EXISTS "ACCOUNT";
CREATE TABLE "ACCOUNT" (
                           account_number uuid DEFAULT gen_random_id(),
                           account_owner INTEGER NOT NULL,
                           account_balance INTEGER NOT NULL,
                           PRIMARY KEY (account_number)
);

INSERT INTO ACCOUNT (account_number,account_owner, account_balance) VALUES (SELECT user_account FROM USERS
                                                                                   WHERE user_name='Andres Villa',SELECT user_id FROM USERS WHERE user_name='Andres Villa',1000);
INSERT INTO ACCOUNT (account_number,account_owner, account_balance) VALUES (SELECT user_account FROM USERS
                                                                                   WHERE user_name='Carlos Giraldo',SELECT user_id FROM USERS WHERE user_name='Carlos Giraldo',500);

DROP TABLE IF EXISTS "LOAN";
CREATE TABLE "LOAN" (
                        loan_id uuid DEFAULT gen_random_id(),
                        loan_owner INTEGER NOT NULL,
                        total_amount INTEGER NOT NULL,
                        pending_amount INTEGER NOT NULL,
                        payments_number INTEGER NOT NULL,
                        PRIMARY KEY (loan_id)
);

INSERT INTO LOAN (loan_owner, total_amount, pending_amount, payments_number) VALUES (SELECT user_id FROM USERS WHERE
                                                                                            user_name='Andres Villa',500,250,6)
