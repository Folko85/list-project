# KODA.md — Контекст проекта

## Обзор проекта

**ToDoList Project** — многомодульное веб-приложение для управления списками задач (Todo-лист). Приложение позволяет пользователям регистрироваться, авторизовываться и управлять персональными списками задач.

### Архитектура

Проект состоит из одного модуля:

- **backend** — серверная часть на Java Spring Boot, включающая REST API, фронтенд на AngularJS (статические файлы), базу данных SQLite и систему миграций Liquibase.

Текущий стек на каждом этапе описан в [FLUTTER-MIGRATION-PLAN.md](FLUTTER-MIGRATION-PLAN.md). Фронтенд сейчас работает на AngularJS 1.6, развёртывается как статика через Spring Boot.

### Основные технологии

| Слой | Технология |
|---|---|
| Backend | Java 25, Spring Boot 4.1.0 |
| ORM | Spring Data JPA, Hibernate |
| База данных | SQLite (prod), H2 (test) |
| Миграции | Liquibase |
| Безопасность | Spring Security + JWT (jjwt 0.11.5) |
| Валидация | Jakarta Validation |
| Lombok | 1.18.46 |
| Frontend | AngularJS 1.6 + Bootstrap 3 + Axios |
| Тестирование | JUnit 4, Spring Boot Test, MockMvc |

### Модель данных

- **User** (`user_tab`) — id, username, password, firstname, lastname, role. Роли: `USER`, `ADMIN`. Пароли хешируются BCrypt (strength 12).
- **Task** (`task_tab`) — id, title, date_of_create, user_id (FK). Связь: один пользователь — много задач.

## Сборка и запуск

### Предварительные требования

- Java 25 (SDKMAN/SDKMAN, или любая совместимая JDK)
- Maven 3.9+
- Переменная окружения `JWT_SECRET` — Base64-кодированное значение минимум из 32 случайных байт

### Команды

```bash
# Сборка проекта
mvn clean package

# Запуск приложения (порт 5000)
mvn spring-boot:run -pl backend

# Или напрямую из JAR
java -jar backend/target/backend-2.0.0.jar

# Запуск тестов
mvn test -pl backend
```

### Переменные окружения

| Переменная | Описание |
|---|---|
| `JWT_SECRET` | Секретный ключ для подписи JWT-токенов (обязателен в prod) |

### База данных

- **Production**: SQLite, файл `todo.db` (исключён из Git через `.gitignore`)
- **Test**: H2 in-memory, конфигурация в `backend/src/test/resources/application.yml`

Liquibase не используется в тестовой среде (`liquibase.enabled: false`), в продакшене включён. Миграции находятся в `backend/src/main/resources/db/changelog/`.

## Правила разработки

### Структура пакетов

```
main
├── Main.java                    # Точка входа
├── config/                      # Конфигурация (SecurityConfig, DatabaseConfig)
├── controller/                  # REST-контроллеры (ApiController, UserController)
├── dto/                         # DTO-классы (request/response)
├── exception/                   # Обработка исключений и глобальный advice
├── mapper/                      # Маппинг моделей в DTO
├── model/                       # JPA-модели (User, Task)
├── repository/                  # Репозитории Spring Data
├── security/                    # JWT-фильтр и провайдер
└── service/                     # Сервисный слой (UserService, TaskService)
```

### Фронтенд

Статические файлы AngularJS хранятся в `backend/src/main/resources/static/`:

- `index.html` — главная страница
- `home/` — контроллер и view списка задач
- `login/` — контроллер и view входа
- `register/` — контроллер и view регистрации
- `app.js`, `app-content/app.css` — основной модуль и стили
- `app-services/` — сервисы (authentication, flash, task, user)

### Тестирование

- Фреймворк: JUnit 4, Spring Boot Test, MockMvc
- Абстрактный базовый класс: `AbstractTest` — инициализация MockMvc с Spring Security
- Основной тестовый класс: `ApiControllerTest` — 17 тестов
- Тестовая база: H2 in-memory, `ddl-auto: update`
- Тесты покрывают CRUD-операции с задачами и авторизацию

### Безопасность

- JWT-аутентификация через кастомный `JwtFilter`
- `@PreAuthorize("hasAuthority('user:write')")` на всех эндпоинтах задач
- Изоляция данных: пользователь видит только свои задачи; чужие — `404`; чужой профиль — `403`
- Секрет JWT берётся из переменной окружения `JWT_SECRET`

### Функциональные сценарии

Минимально необходимое поведение зафиксировано в [FUNCTIONAL-SCENARIOS.md](FUNCTIONAL-SCENARIOS.md). Включает регистрацию, вход/выход, профиль, CRUD задач, изоляцию данных и сквозные критерии.

## Переход на Flutter

План миграции с Java Spring Boot + AngularJS на Dart/Flutter описан в [FLUTTER-MIGRATION-PLAN.md](FLUTTER-MIGRATION-PLAN.md). Проект находится на **этапе 0**: бэкенд готов к подключению нового клиента, API стабилен.

### Текущее состояние этапа 0

- Серверные проверки изоляции задач и профиля на месте
- 18 тестов пройдено (включая регрессионные тесты CRUD)
- JWT-секрет вынесен в окружение
- Резерв**ная копия `todo.db` не создавалась — требуется вручную перед миграцией
- Токен Telegram мог остаться в истории Git — необходимо** отозвать через BotFather

## Конфигурационные файлы

| Файл | Назначение |
|---|---|
| `pom.xml` | Родительский POM (multi-module, Spring Boot 4.1.0) |
| `backend/pom.xml` | Зависимости и плейны модуля backend |
| `backend/src/main/resources/application.yml` | Prod-конфигурация: SQLite, порт 5000, JWT из окружения |
| `backend/src/test/resources/application.yml` | Test-конфигурация: H2 in-memory, отключённый Liquibase |
| `backend/system.properties` | Указание версии Java 11 (вероятно устарело, проект использует Java 25) |
| `.gitignore` | Исключает `.idea/`, `backend/target/`, `todo.db` |

## Git-ветки

Активная ветка: `master`

Основные ветки:
- `master` — основная разработка
- `Issue-9` — добавление тестов и устранение уязвимостей
- `issue-6` — изменение структуры проекта
- `remove-telegram-bot` — удаление Telegram-бота
- `remotes/origin/postgres` — эксперимент с PostgreSQL
