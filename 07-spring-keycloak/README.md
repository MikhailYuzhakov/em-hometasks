# Spring Keycloak Demo Application

This project is a Spring Boot application demonstrating integration with Keycloak for authentication and authorization. It provides a registration page, secure access to various external resources (Jira, GitLab, Grafana) based on user roles (groups) managed in Keycloak.

## Table of Contents

1.  [Prerequisites](#prerequisites)
2.  [Getting Started](#getting-started)
    *   [Local Development Setup](#local-development-setup)
    *   [Running the Application](#running-the-application)
3.  [Application Overview](#application-overview)
    *   [Keycloak Integration](#keycloak-integration)
    *   [Security Configuration](#security-configuration)
    *   [Endpoints](#endpoints)
    *   [User Registration](#user-registration)
4.  [Testing](#testing)
5.  [Release Process](#release-process)

## Prerequisites

Перед началом работы убедитесь, что у вас установлены:

*   **Java Development Kit (JDK) 17 или выше**
*   **Maven 3.6.x или выше**

## Getting Started

### Local Development Setup

1.  **Клонируйте репозиторий:**

    ```bash
    git clone <URL-вашего-репозитория>
    cd spring-keycloak-demo
    ```

2.  **Соберите Spring Boot приложение:**

    ```bash
    mvn clean install
    ```

    Эта команда скомпилирует проект и упакует его в исполняемый JAR файл (`target/spring-keycloak-demo-1.0.0.jar`).

3.  **Запустите Spring Boot приложение:**

    Вы можете запустить приложение напрямую с помощью Maven:

    ```bash
    mvn spring-boot:run
    ```

    Или запустить собранный JAR файл:

    ```bash
    java -jar target/spring-keycloak-demo-1.0.0.jar
    ```

    Приложение будет доступно по адресу: `http://localhost:8081`

    **Важно:** Убедитесь, что `keycloak.auth-server-url` в `src/main/resources/application.yaml` указывает на ваш внешний Keycloak. Текущая конфигурация:
    ```yaml
    keycloak:
      auth-server-url: https://keycloak.effective-mobile.ru/
      realm: effective-mobile
      resource: spring-boot-client
    ```

## Application Overview

### Keycloak Integration

*   **Сервер аутентификации**: `https://keycloak.effective-mobile.ru/` (внешний Keycloak)
*   **Realm**: `effective-mobile`
*   **Client ID**: `spring-boot-client`
*   **Client Secret**: `YXdGEGzL7ax7WYfytTxJ3JkYHsaKnTad` (для административных операций и аутентификации клиента)
*   **Группы для контроля доступа**: Приложение использует группы Keycloak (`jira-access`, `gitlab-access`, `grafana-access`) для контроля доступа к определенным ресурсам.

### Security Configuration (`SecurityConfig.java`)

*   **Аутентификация**: Пользователи аутентифицируются через внешний Keycloak с использованием OAuth2 Login.
*   **Авторизация**:
    *   Статические ресурсы и точка входа `/register/**` общедоступны.
    *   Все остальные запросы требуют аутентификации.
    *   Роли пользователей извлекаются из утверждения Keycloak "groups" и сопоставляются с полномочиями Spring Security с префиксом `GROUP_` (например, `GROUP_jira-access`).
    *   Безопасность на уровне методов (`@PreAuthorize`) используется для защиты определенных точек входа на основе этих групповых полномочий.
*   **Перенаправление после входа**: После успешного входа OAuth2 пользователи перенаправляются на `/api/jira-access`.

### Endpoints

*   **Публичные:**
    *   `GET /register`: Отображает форму регистрации пользователя.
    *   `POST /register`: Обрабатывает регистрацию нового пользователя в Keycloak.

*   **Защищенные (требуется аутентификация и определенные групповые полномочия):**
    *   `GET /api/jira-access`: Перенаправляет на `https://jira.effective-mobile.ru` (требуется `GROUP_jira-access`).
    *   `GET /api/gitlab-access`: Перенаправляет на `https://gitlab.effective-mobile.ru` (требуется `GROUP_gitlab-access`).
    *   `GET /api/grafana-access`: Перенаправляет на `https://grafana.em-gitlab.ru/` (требуется `GROUP_grafana-access`).

### Регистрация пользователя (`RegistrationController.java`)

Точка входа `/register` позволяет создавать новых пользователей в Keycloak. Во время регистрации пользователям могут быть назначены предопределенные группы (`jira-access`, `gitlab-access`, `grafana-access`). `KeycloakAdminService` обрабатывает фактическое создание пользователя и назначение групп в Keycloak.

## Testing

1.  **Unit/Integration Tests (Maven):**
    Для запуска локальных модульных и интеграционных тестов используйте стандартную команду Maven:

    ```bash
    mvn test
    ```

2.  **Ручное тестирование:**
    *   Убедитесь, что ваше Spring Boot приложение запущено (см. раздел [Запуск приложения](#running-the-application)).
    *   **Зарегистрируйте нового пользователя:**
        *   Перейдите по адресу `http://localhost:8081/register`.
        *   Заполните регистрационные данные, убедившись, что вы выбрали одну или несколько групп (например, `jira-access`).
    *   **Войдите и получите доступ к защищенным ресурсам:**
        *   После регистрации попробуйте получить доступ к `http://localhost:8081/api/jira-access`. Если у пользователя есть группа `jira-access`, он должен быть перенаправлен в Jira.
        *   Экспериментируйте с различными назначениями групп и попробуйте получить доступ к соответствующим конечным точкам `/api/*-access`, чтобы проверить авторизацию.
    *   **Проверьте пользователей и группы Keycloak (внешний):**
        *   Доступ к консоли администратора Keycloak зависит от вашего внешнего Keycloak. Используйте предоставленные административные учетные данные для доступа.
        *   Убедитесь, что вновь зарегистрированные пользователи и их групповые назначения корректно отображаются.

## Release Process

Этот проект использует Maven для сборки.

1.  **Соберите JAR приложения:**

    ```bash
    mvn clean package
    ```

    Эта команда генерирует `spring-keycloak-demo-1.0.0.jar` в каталоге `target/`.

2.  **Развертывание:**

    Для развертывания вы можете использовать собранный JAR файл в вашей целевой среде. Поскольку Keycloak является внешним, вам не нужно развертывать его как часть этого приложения.
