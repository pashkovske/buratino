# Buratino

Трейдинговый робот через API Тинькофф инвестиций с интеграцией PostgreSQL.

## Возможности

- Интеграция с API Тинькофф
- База данных PostgreSQL для хранения текущих спредов
- REST API для управления спредами
- Поддержка Docker для базы данных
- Kotlin для современной разработки
- Автоматическое получение спредов из рыночных данных

## Требования

- Java 21
- Maven 3.6+
- Docker и Docker Compose

## Быстрый старт

1. Запустить базу данных PostgreSQL:
```bash
docker-compose up -d
```

2. Установить переменную окружения для токена API Тинькофф:
```bash
export TINKOFF_API_TOKEN=your_token_here
```

3. Запустить приложение:
```bash
mvn spring-boot:run
```

## API Endpoints

### Текущие спреды

- `POST /api/spreads` - Сохранить или обновить спред (ручной ввод)
- `POST /api/spreads/from-market/{instrumentUid}` - Получить и сохранить спред из рыночных данных
- `POST /api/spreads/update-all-from-market` - Обновить все спреды из рыночных данных
- `GET /api/spreads/{instrumentUid}` - Получить спред по UID инструмента
- `GET /api/spreads` - Получить все спреды
- `GET /api/spreads/search?minPriceUnits=X&maxPriceUnits=Y` - Поиск спредов по диапазону цен
- `DELETE /api/spreads/{instrumentUid}` - Удалить спред по UID инструмента

### Примеры запросов

#### Ручное сохранение спреда
```bash
curl -X POST http://localhost:8080/api/spreads \
  -H "Content-Type: application/json" \
  -d '{
    "instrumentUid": "BBG0013HGFT4",
    "minPrice": {
      "units": 100,
      "nanos": 500000000
    },
    "maxPrice": {
      "units": 101,
      "nanos": 200000000
    }
  }'
```

#### Получение спреда из рыночных данных
```bash
curl -X POST http://localhost:8080/api/spreads/from-market/BBG0013HGFT4
```

#### Обновление всех спредов из рыночных данных
```bash
curl -X POST http://localhost:8080/api/spreads/update-all-from-market
```

## База данных

Приложение использует PostgreSQL со следующей конфигурацией:
- База данных: `buratino_db`
- Пользователь: `buratino_user`
- Пароль: `buratino_password`
- Порт: `5432`

```bash
psql -U buratino_user -d buratino_db -c "
SELECT
    *
FROM
    fractional_spread_assignment_row
"
```

Схема базы данных управляется миграциями Flyway.
