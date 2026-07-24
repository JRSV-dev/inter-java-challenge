-- Dados demonstrativos carregados na inicialização local.
INSERT INTO USUARIOS (
    NOME_COMPLETO,
    EMAIL,
    IDENTIFICADOR,
    TIPO_USUARIO,
    SENHA,
    DATA_CRIACAO
)
VALUES (
    'Ana Martins',
    'ana.martins@example.com',
    '52998224725',
    'PF',
    '$2a$10$lHtkZ/SgzCBfj99r13Xj9.FYx56rFJDSg9uZFxfj4pxqyEtD6c0yy',
    CURRENT_TIMESTAMP
);

INSERT INTO USUARIOS (
    NOME_COMPLETO,
    EMAIL,
    IDENTIFICADOR,
    TIPO_USUARIO,
    SENHA,
    DATA_CRIACAO
)
VALUES (
    'Empresa Exemplo Ltda.',
    'financeiro@empresa-exemplo.com',
    '11222333000181',
    'PJ',
    '$2a$10$UtzA3Q2sXr9SDexO0bzcMOc3yX9UytZm0uo4RdScAf1faSlnFg6Au',
    CURRENT_TIMESTAMP
);

INSERT INTO CARTEIRAS (
    USUARIO_ID,
    SALDO_REAIS,
    SALDO_DOLARES
)
VALUES (
    1,
    10000.00,
    0.0000
);

INSERT INTO CARTEIRAS (
    USUARIO_ID,
    SALDO_REAIS,
    SALDO_DOLARES
)
VALUES (
    2,
    10000.00,
    0.0000
);
