INSERT INTO users (name, email, password, role, is_active, hotel_id) VALUES
                                                                         ('John Doe', 'user@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'USER', true, NULL),
                                                                         ('Jane Smith', 'receptionist@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'RECEPTIONIST', true, 1),
                                                                         ('Bob Johnson', 'manager@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'MANAGER', true, 1),
                                                                         ('Alice Brown', 'laundry@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'LAUNDRY', true, 1),
                                                                         ('Tom Wilson', 'cab@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'CAB', true, 1),
                                                                         ('Admin User', 'admin@example.com', '$2a$10$8.UnVu1U8e3TwNa6fFK8v.Tm0xcgA4b0g0aCmd/yjB41.KsUn6HOC', 'SUPER_ADMIN', true, NULL);

INSERT INTO hotels (name, city, address, manager_id, number_of_floors) VALUES
    ('Palatin Hotel NYC', 'New York', '123 Broadway', 3, 5);

INSERT INTO rooms (hotel_id, room_number, type, price, view, amenities, status, floor) VALUES
                                                                                           (1, '1F-101', 'SINGLE', 100.0, 'CITY', 'WiFi', 'AVAILABLE', 1),
                                                                                           (1, '1F-102', 'DOUBLE', 150.0, 'SEASIDE', 'WiFi,TV', 'OCCUPIED', 1),
                                                                                           (1, '2F-201', 'SINGLE', 120.0, 'CITY', 'WiFi', 'OUT_OF_ORDER', 2);