# 🏨 Palatin Hotel Booking System

> **Meaning:** *Palatin* signifies elegance, prestige, and high-class service.

## 📘 Project Overview

**Palatin** is a multi-role hotel management system designed to streamline room bookings, check-ins/check-outs, invoicing, and service handling across hotel branches. Built using **Spring Boot**, **Thymeleaf**, and **RestTemplate**, it supports secure **JWT-based authentication**, **SMTP email**, and **PayPal integration**.

## 🎯 Purpose & Goals

- Centralized hotel and service management  
- Book **specific rooms** and **multiple rooms** in one transaction  
- JWT-secured login with SMTP-based email communication  
- Online payments via PayPal  
- Dynamic service management (e.g., laundry)  
- User can **download invoices** after booking 
- Scalable frontend using Spring MVC + Thymeleaf + RestTemplate  

---

## 📌 Tech Stack

| Layer       | Technologies Used                                        |
|-------------|----------------------------------------------------------|
| Frontend    | HTML, CSS, JS, Bootstrap, Thymeleaf (Spring MVC)        |
| Backend     | Spring Boot, Spring Security (JWT)          |
| Database    | MySQL                                                   |
| API Comm    | Spring RestTemplate                                      |
| Utilities   | SMTP Email, PayPal SDK                                   |
| Tools       | GitHub, Postman                                          |

---

## 🔐 Roles & Functionalities

### 👑 Admin
- Dashboard for room status and reservations  
- Manage hotels, rooms, staff, and pricing  
- Generate reports (revenue, performance, occupancy)  

### 🧑‍💼 Manager
- Manage one specific hotel branch  
- Oversee rooms, pricing, and assign staff  

### 🛎️ Receptionist
- View & manage reservations, check-ins/check-outs  
- Generate invoices and accept online payments  
- Book rooms in-person  
- Assign laundry staff to requests  

### 🧺 Laundry
- View and update laundry service status  

### 👤 User
- Register/Login securely  
- Book **specific** and **multiple** rooms  
- Make payments (PayPal)  
- Download booking invoice  
- Manage personal bookings and request services  

### 👀 Guest
- Explore room availability and pricing  
- Limited access (can register to book)  

---

## 🔄 Data Structure

Main Tables:  
- `Users`  
- `Hotels`  
- `Rooms`  
- `Bookings`  
- `Service_Requests`  
- `Feedback`  

---

## ⚠️ Risk Management

| Risk              | Mitigation Strategy                                 |
|-------------------|------------------------------------------------------|
| Booking Conflicts | Lock mechanisms and availability checks              |
| Payment Failures  | Retry mechanisms + PayPal fallback handling          |
| Unauthorized Use  | JWT + Role-Based Access Control (RBAC)              |
| Data Breach       | DTO isolation, encrypted data, request validation    |

---

## 📌 Conclusion

The **Palatin Hotel Booking System** offers a robust, secure, and feature-rich solution for managing hotel operations across roles. With capabilities like **multi-room and specific room selection**, **online payments**, and **invoice downloads**, Palatin sets a benchmark for smart hotel management solutions.

---
