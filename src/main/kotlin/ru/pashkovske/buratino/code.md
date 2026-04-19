## Структура кодовой базы

Приложение состоит из 3-х основных уровней:

1. Модулей `ru.pashkovske.buratino.*`
2. Стереотипов `ru.pashkovske.buratino.*.*`
3. Классов `ru.pashkovske.buratino.*.*.**`

### Уровень модулей

Приложение разбито на относительно независимые модули. Каждый модуль отвечает за свою _бизнес-задачу_:

- `ru.pashkovske.buratino.account` - авторизация и аутентификация приложения у брокера
- `ru.pashkovske.buratino.assignment` - стратегии торговли на бирже
- `ru.pashkovske.buratino.common` - общие для всего приложения компоненты
- `ru.pashkovske.buratino.configuration` - общие для всего приложения Spring `@Configuration`
- `ru.pashkovske.buratino.instrument` - информация о торгуемых на бирже инструментах
- `ru.pashkovske.buratino.order` - управление торговыми поручениями брокеру
- `ru.pashkovske.buratino.price` - получение и расчёт цен
- `ru.pashkovske.buratino.security` - авторизация и аутентификация клиента в приложении

Более подробные описания каждого модуля можно найти в `code.md` внутри пакета

### Уровень стереотипов

Внутри каждого модуля пакеты разбиты на папки соответствующие общепринятым шаблонам проектирования:

- `ru.pashkovske.buratino.*.adapter` - инкапсулирует особенности работы с внешней системой
- `ru.pashkovske.buratino.*.configuration` - Spring `@Configuration`
- `ru.pashkovske.buratino.*.controller` - Spring `@RestController`
- `ru.pashkovske.buratino.*.dao` - _Data Access Object_ инкапсулирует работу с базами данных
- `ru.pashkovske.buratino.*.exception` - исключения
- `ru.pashkovske.buratino.*.model` - бизнес-модель данных модуля, основные, широко специализированные data-классы
- `ru.pashkovske.buratino.*.service` - службы, которые реализуют бизнес-логику модуля

### Уровень классов

Строгой структуры нет, классы группируются в пакеты исходя из здравого смысла

### Другие стереотипные имена

- `cmd` - command
- `core` - пакет модуля отвечающий за одноимённые с модулем сущности. Например, в
  `ru.pashkovske.buratino.assignment.model.core` лежат сами модели поручений (Assignments)
- `dto` - _Data Transfer Object_ узко специализированные data-классы. Отличие от model:
    - dto неизменяемые; model может иметь изменяемые поля
    - dto передаёт данные между двумя слоями; model может использоваться несколькими службами модуля с разным
      назначением
    - чтобы передать данные между модулями, следует использовать dto
- `mapper` - делает из объекта X объект Y, не мутируя X, кроме того Y и X независимые объекты
- `repo` - repository
- `util` - утилиты
