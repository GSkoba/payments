# Cool Payment
Сервис обработки платежей. Основным компонентом является payments-backend, payments-consumer предназначен для демонстрации асинхронной выгрузки данных.
## payments-backend
Сервис использует механизм шардирования для хранения платежей в 3 базах данных.
Информация о платеже сохраняется в одной из трех БД для отказоустойчивости и распределения нагрузки.
### Api
### Загрузка списка платежей в сервис
```
POST http://localhost:8080/payment/transfer
Content-Type: application/json
{
    "payments": [  
        {
        "from" : "Grisha",
        "to" : "Nasty",
        "money" : 10 
        },
        {
        "from" : "Misha",
        "to" : "Vika",
        "money" : 250 
        },
        {
        "from" : "Oleg",
        "to" : "Vova",
        "money" : 10 
        }
    ]
}
```
### Выдача общей суммы потраченных средств по отправителю
Имеется 2 варианта асинхронной выгрузки:
- URL - выгрузка осущевствляется как POST метод по url указанному в payload
- TOPIC - выгрузка данных происходит в kafka topic 
#### URL
```
POST http://localhost:8080/payment/aggregate
Content-Type: application/json
{
    "username": "Misha",
    "url" : "http://localhost:8081/test",
    "payloadType": "URL"
}
```
#### Topic
```
POST http://localhost:8080/payment/aggregate
Content-Type: application/json
{
    "username": "Misha",
    "topic" : "billing",
    "payloadType": "TOPIC"
}
```
## Запуск рабочего кружения 
Для работы **payment-backend** требуется хотя бы один инстанс бд postgres и kafka broker.
В корне проекта имеется docker-compose.yaml с готовым рабочим окружением.
```
docker-compose up
```
## payments-consumer
Принимает данные по асинхронной выгрузке от payments-backend.