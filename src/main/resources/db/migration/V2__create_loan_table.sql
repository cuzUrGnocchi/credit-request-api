CREATE TABLE loan (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
   credit_code UUID NOT NULL,
   credit_amount DECIMAL NOT NULL,
   date_of_first_installment date NOT NULL,
   number_of_installments INT NOT NULL,
   status SMALLINT,
   customer_id BIGINT,
   CONSTRAINT pk_loan PRIMARY KEY (id)
);

ALTER TABLE loan ADD CONSTRAINT uc_loan_credit_code UNIQUE (credit_code);

ALTER TABLE loan ADD CONSTRAINT FK_LOAN_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id);