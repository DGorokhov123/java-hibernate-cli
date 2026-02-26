# Консольное приложение с Hibernate без Spring

Консольное приложение на Java, использующее Hibernate для взаимодействия с PostgreSQL, без использования Spring. 
Приложение поддерживает базовые операции CRUD (Create, Read, Update, Delete) над сущностью User.

### Описание
- Использовать Hibernate в качестве ORM
- База данных — PostgreSQL
- Hibernate без Spring, используется hibernate.cfg.xml
- Реализованы CRUD-операции для сущности User (создание, чтение, обновление, удаление) 
- User состоит из полей: id, name, email, age, created_at
- Консольный интерфейс для взаимодействия с пользователем.
- Maven для управления зависимостями.
- Логирование через logback
- Транзакции для операций с базой данных.
- DAO-паттерн для отделения логики работы с БД.
- Обработка исключений

### Запуск приложения

1. Клонируйте репозиторий:
```bash
git clone https://github.com/DGorokhov123/java-hibernate-cli.git
cd java-hibernate-cli
```
2. Запустите PostgreSQL
```bash
docker compose up -d
```
2. Соберите и запустите проект:
```bash
./mvnw clean package
./mvnw exec:java -Dexec.mainClass=ru.dgorokhov.Main
```
