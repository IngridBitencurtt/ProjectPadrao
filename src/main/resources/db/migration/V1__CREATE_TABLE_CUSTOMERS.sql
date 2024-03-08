CREATE TABLE customers
(
    id                      VARCHAR(36) PRIMARY KEY,
    name                    VARCHAR(255) ,
    external_code           VARCHAR(255),
    document                VARCHAR(11),
    birth_date              DATE,
    phone                   VARCHAR(255),
    email                   VARCHAR(255),
    customer_since          DATE,
    last_update             DATE,
    CONSTRAINT uk_document unique (document)

);
