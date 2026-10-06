# Нагрузка и наблюдение

## 1. Ручная проверка (без ничего лишнего)

Приложение: `java -jar app/target/app-0.0.3-SNAPSHOT.jar` (БД `platform_*` на `localhost:5432`, логин `postgres`, пароль `root`).

```powershell
# баланс демо-счета neo (ожидается 10000.00 RUB на свежей БД)
Invoke-RestMethod "http://localhost:8080/api/v1/banking/account/balance/408810100001"

# история демо-чата (3 сообщения из сидов)
Invoke-RestMethod "http://localhost:8080/api/v1/messenger/message/history?chatId=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"

# лента neo (2 поста из сидов)
Invoke-RestMethod "http://localhost:8080/api/v1/social/post/feed?authorId=11111111-1111-1111-1111-111111111111"

# перевод 100 RUB neo -> trinity
$body = @{from='408810100001'; to='408810100002'; amount=100.00; type='TRANSFER'; currency='RUB'} | ConvertTo-Json
Invoke-RestMethod -Method Post -ContentType 'application/json' -Body $body "http://localhost:8080/api/v1/banking/transaction/transfer/byAccountNumber"

# здоровье и метрики
Invoke-RestMethod "http://localhost:8080/actuator/health"
Invoke-RestMethod "http://localhost:8080/actuator/prometheus" | Select-String "http_server_requests" | Select-Object -First 5
```

## 2. Нагрузочный тест (k6)

Установка: `winget install k6` (или https://k6.io/docs/get-started/installation/).

```powershell
cd backend/load
k6 run k6-wechat.js
k6 run -e BASE_URL=http://localhost:8080 k6-wechat.js
```

Сценарий `k6-wechat.js`: 5 болтунов (send + иногда history), 5 плательщиков
(случайные переводы 1–20 RUB туда-сюда — заодно гоняет пессимистические
блокировки под конкуренцией), 2 постера (publish + feed). Пороги: ошибок < 1%,
p95 < 800мс.

Заметь: плательщики гоняют общие счета, балансы дрейфуют — для нагрузочного
теста блокировок это нормально. Свежие балансы — пересозданием БД.

## 3. Метрики

- `GET /actuator/health` — жив ли монолит и все 4 пула.
- `GET /actuator/prometheus` — скрейп для Prometheus/Grafana.
- Ключевое: `http_server_requests_seconds_{count,sum}` с тегами `uri`, `status` —
  RPS и латентность по каждому эндпоинту во время k6-прогона.
- `jvm_memory_used_bytes`, `hikaricp_connections_active` — память и пулы.

Дальше: кастомный счетчик исходов переводов (`transfer.outcome{status,reason}`)
и дашборд Grafana.

## 4. Grafana (графики)

```powershell
wsl docker compose -f /mnt/c/Users/aleksey/OpenIDEProjects/platform/backend/docker-compose.yml up -d prometheus grafana
```

- Grafana: `http://<wsl-ip>:3000` (логин `admin` / `admin`), дашборд
  `Platform` в папке `Platform` уже подключен к Prometheus.
- Prometheus: `http://<wsl-ip>:9090`, таргет `platform` должен быть UP.
- Панели: RPS и p95 по эндпоинтам, счетчик переводов, heap JVM,
  активные коннекты Hikari.
- IP узнавать через `wsl hostname -I` (localhost-релей Windows↔WSL шалит).
