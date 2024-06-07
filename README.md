# <span style="color:blue;">Общая Картина Взаимодействия и Поиск по Параметрам Клиентов для Митинга в Zoom</span>

## <span style="color:green;">Введение</span>
Приложение предназначено для автоматического создания и управления заявками на встречи в Zoom. Основные функции включают создание заявок на встречи, проверку совпадений между заявками, автоматическое создание встреч в Zoom для совпадающих заявок и предоставление информации о созданных встречах.

## <span style="color:green;">Архитектура Приложения</span>
Приложение состоит из следующих компонентов:

### <span style="color:orange;">Конфигурационный Компонент</span>
- **`AppConfig`**: Конфигурационный класс, который создает `RestTemplate` для выполнения HTTP-запросов к API Zoom.

### <span style="color:orange;">Модель Данных</span>
- **`MeetingRequest`**: Сущность, представляющая заявку на встречу. Включает поля для хранения информации о теме, времени начала, длительности встречи, статусе заявки, а также URL-адресов для присоединения и начала встречи в Zoom.

### <span style="color:orange;">Контроллер</span>
- **`MeetingRequestController`**: Обрабатывает HTTP-запросы для создания заявок на встречи и встреч в Zoom, а также для получения всех заявок на встречи.

### <span style="color:orange;">Сервис</span>
- **`MeetingRequestService`**: Управляет логикой создания заявок на встречи, проверки совпадений между заявками и создания встреч в Zoom для совпадающих заявок. Также содержит методы для получения всех заявок на встречи.
- **`ZoomApiService`**: Обеспечивает взаимодействие с API Zoom, включая создание встреч и получение токенов аутентификации.

### <span style="color:orange;">Компонент Аутентификации</span>
- **`ZoomAuthenticationHelper`**: Управляет получением и хранением токенов аутентификации для доступа к API Zoom.

## <span style="color:green;">Процесс Взаимодействия</span>
### <span style="color:purple;">Создание Заявки на Встречу</span>
1. Пользователь отправляет запрос на создание заявки на встречу через **`MeetingRequestController`**.
2. **`MeetingRequestService`** сохраняет заявку в базе данных со статусом "ожидание".

### <span style="color:purple;">Периодическая Проверка Совпадений</span>
1. **`MeetingRequestService`** периодически проверяет заявки со статусом "ожидание" на совпадения по теме, времени начала и длительности.
2. Если находятся совпадающие заявки, создается встреча в Zoom для этих заявок.

### <span style="color:purple;">Создание Встречи в Zoom</span>
1. **`ZoomApiService`** отправляет запрос к API Zoom для создания встречи.
2. В случае успешного создания встречи, URL-адреса для присоединения и начала встречи сохраняются в обеих заявках.
3. Статус заявок обновляется на "сопоставлено".

### <span style="color:purple;">Получение Списка Всех Заявок</span>
- Пользователь может получить список всех заявок на встречи через **`MeetingRequestController`**.

## <span style="color:green;">Взаимодействие с API Zoom</span>
### <span style="color:purple;">Аутентификация</span>
- **`ZoomAuthenticationHelper`** управляет получением токена аутентификации, необходимого для выполнения запросов к API Zoom.

### <span style="color:purple;">Создание Встречи</span>
1. **`ZoomApiService`** формирует и отправляет запрос на создание встречи в Zoom с указанными параметрами (тема, длительность, время начала).
2. Ответ от API Zoom обрабатывается и соответствующие URL-адреса сохраняются в заявках на встречу.

## <span style="color:green;">Технические Детали</span>
- **`Spring Framework`**: Используется для создания RESTful веб-сервисов и управления зависимостями.
- **`JPA (Java Persistence API)`**: Для взаимодействия с базой данных и управления сущностями.
- **`Scheduled Tasks`**: Для периодической проверки совпадений заявок и создания встреч в Zoom.
- **`HTTP-клиент (RestTemplate)`**: Для выполнения запросов к API Zoom.

## <span style="color:green;">Заключение</span>
Данное приложение автоматизирует процесс создания и управления заявками на встречи в Zoom, обеспечивая удобный и эффективный способ организации онлайн-встреч. Интеграция с API Zoom позволяет автоматически создавать встречи на основе совпадающих заявок, минимизируя ручные действия и повышая удобство пользователей.



