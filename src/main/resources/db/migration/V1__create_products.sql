CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200)   NOT NULL,
    description TEXT,
    price       NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL CHECK (stock >= 0),
    updated_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);

INSERT INTO products (name, description, price, stock) VALUES
    ('Mechanical Keyboard', 'Hot-swappable, brown switches', 79.00, 50),
    ('Wireless Mouse', 'Ergonomic, 2.4GHz + Bluetooth', 29.50, 100),
    ('27" Monitor', '1440p IPS, 144Hz', 289.00, 20),
    ('USB-C Hub', '7-in-1, 100W PD passthrough', 39.90, 75),
    ('Noise-cancelling Headphones', 'Over-ear, 30h battery', 199.00, 15);
