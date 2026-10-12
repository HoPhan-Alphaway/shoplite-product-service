# shoplite-product-service

Catalog + tồn kho. Đọc qua Redis (cache-aside, TTL 60s, evict khi ghi). Trừ kho bằng 1 câu `UPDATE ... WHERE stock >= qty` để không bán quá số lượng khi có request đồng thời.

| Method | Path | Auth |
|---|---|---|
| GET | `/api/products`, `/api/products/{id}` | public |
| POST/PUT | `/api/products[/{id}]` | role `admin` |
| POST | `/api/products/{id}/reserve` | role `stock-writer` (service account của order-service) |

Env: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `REDIS_HOST`, `CACHE_TTL`, `OIDC_ISSUER_URI`.

## Chạy

- Cả stack: `shoplite-gitops/local` → `docker compose up -d --build`.
- Test: `./mvnw verify` (cần Docker cho Testcontainers).
- Image: `docker build -t <name> .` (multi-stage, layered jar, non-root UID 10001).

Health: `/actuator/health/liveness`, `/actuator/health/readiness` · Metrics: `/actuator/prometheus` · Trace/log: OpenTelemetry Java agent (inject từ ngoài, không có trong image).
