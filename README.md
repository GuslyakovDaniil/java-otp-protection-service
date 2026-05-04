# OTP Protection Service - Проект для Promo IT

Backend-приложение на языке Java для обеспечения безопасности операций с помощью временных одноразовых кодов (OTP). Сервис поддерживает регистрацию пользователей, генерацию кодов с настраиваемыми параметрами и рассылку через различные каналы связи.

## Технологический стек
- **Java 17** и **Spring Boot 3.2.1**
- **JDBC** (Взаимодействие с БД PostgreSQL без использования ORM)
- **PostgreSQL 17**
- **JWT (JSON Web Tokens)** для аутентификации и авторизации по ролям
- **BCrypt** для безопасного хеширования паролей
- **SMPP (JSMPP)** — интеграция с SMS-эмулятором
- **Jakarta Mail** — отправка уведомлений на реальную почту
- **Telegram Bot API** — мгновенная доставка кодов через бота
- **Quartz/Scheduling** — автоматическая очистка просроченных кодов

## Архитектура проекта
Проект реализован согласно требованиям ТЗ с разделением на слои:
- **API (Controllers):** Обработка HTTP-запросов и валидация входящих данных.
- **Service Layer:** Основная бизнес-логика (генерация OTP, логика рассылки, проверка прав).
- **DAO (Data Access Object):** Выполнение SQL-запросов к PostgreSQL через JDBC.
- **Security:** Использование Interceptor для проверки JWT и разграничения доступа (USER/ADMIN).

---

## Отчет о работе сервиса (Скриншоты)

### 1. Запуск системы
Приложение успешно инициализирует пул соединений с базой данных и запускает планировщик задач.
![App Start](screenshots/01_start.png)

### 2. Регистрация и Аутентификация
Реализована регистрация пользователей с ролями. При логине выдается JWT токен с ограниченным сроком действия.
![Register](screenshots/02_register.png)
![Login and Token](screenshots/03_login.png)

### 3. Генерация OTP кодов (Каналы рассылки)

#### SMS (через эмулятор SMPPsim)
![Postman SMS](screenshots/04_generate_sms.png)

#### Email (Интеграция с Gmail)
Письмо успешно доставляется на реальный почтовый адрес.
![Email Inbox](screenshots/05_email_inbox.png)
![Email Open](screenshots/06_email_open.png)
![Postman Email Success](screenshots/07_generate_email.png)

#### Telegram (Бот)
Реализована отправка через Telegram Bot API.
![Postman TG Success](screenshots/08_generate_tg.png)
![Telegram Message](screenshots/09_tg_bot.png)

#### Сохранение в файл
Дополнительный способ получения кода через локальный файл `otp_codes.txt`.
![Postman File Success](screenshots/10_generate_file.png)
![File Content](screenshots/11_file_txt.png)

### 4. Валидация кода
Проверка кода на соответствие, принадлежность пользователю и срок годности.
![Validation Success](screenshots/12_validate.png)

### 5. API Администратора
Администратор имеет права на изменение длины кода и времени его жизни (TTL).
![Admin Config](screenshots/13_admin_config.png)

### 6. Подробное логирование
В системе реализовано детальное логирование всех этапов обработки запроса, включая работу планировщика по отметке просроченных кодов (статус `EXPIRED`).
![Logs Activity](screenshots/14_logs.png)

---

## Настройка и запуск

1.  **База данных:** Создать БД PostgreSQL и настроить доступы в `application.properties`. Таблицы создаются автоматически через `schema.sql`.
2.  **SMS:** Запустить SMPPsim эмулятор на порту 2775.
3.  **Email:** Указать валидный Gmail и "Пароль приложения" в `email.properties`.
4.  **Telegram:** Создать бота через @BotFather и указать Token/ChatID в `application.properties`.
5.  **Сборка:** Выполнить команду `mvn clean install`.
6.  **Запуск:** Запустить класс `OtpApplication`.
