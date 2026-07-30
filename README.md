# Collaborative Resource Lock Manager

A backend service that lets multiple clients safely coordinate access to a shared resource — preventing two clients from acting on the same resource at the same time, with automatic recovery if a client crashes mid-operation.

> **Status: actively in progress.** This README reflects what's built so far and what's coming next — see [Progress](#progress) below.

## The Problem

Whenever multiple independent clients (users, services, workers) can act on the same resource concurrently — booking the same seat, editing the same document, claiming the same job — there's a race condition: two clients can both "check" the resource is free at the same instant and both proceed, corrupting state. This service exists to make that impossible.

## The Approach

- **Redis `SET key value NX PX ttl`** provides an atomic check-and-create operation — the core primitive that actually prevents the race condition, not application-level checks.
- **TTL-based auto-expiry** means a crashed or disconnected client can never permanently block a resource — the lock cleans itself up if not renewed.
- **Ownership-checked renew/release** — only the client that acquired a lock can extend or release it.
- **Stateless JWT authentication** — this service verifies tokens locally via a secret shared with a separate Auth Vault service, rather than calling back to it on every request. Auth Vault issues tokens; this service only verifies them.
- **Audit trail, not duplicated state** — the active lock state lives entirely in Redis (fast, temporary). Only the historical record of lock events (acquired/renewed/released) is persisted to a database, since that's the only part that genuinely needs to survive a restart.

## Tech Stack

Java · Spring Boot · Spring Data JPA · Redis · MySQL · JWT

## API (planned)

| Action | Method | Endpoint |
|---|---|---|
| Acquire lock | `POST` | `/locks/{resourceId}` |
| Renew lock | `PATCH` | `/locks/{resourceId}/renew` |
| Release lock | `DELETE` | `/locks/{resourceId}` |
| Check status | `GET` | `/locks/{resourceId}` |
| View history | `GET` | `/locks/{resourceId}/history` |

## Progress

- [x] Project scaffolding
- [x] `HistoryEntity` — persisted audit record for lock lifecycle events
- [x] Repository layer
- [x] Redis connection verified
- [x] Service layer: acquire / renew / release / status logic
- [x] Controller layer (unsecured, tested via Postman)
- [x] JWT verification via Auth Vault shared secret
- [x] Edge case handling + tests
- [ ] Final documentation

## Related Project

This service's locking pattern is reused in [Distributed Job Scheduler](#) to prevent duplicate job execution across multiple scheduler instances.

## Why This Exists

Built as part of hands-on backend engineering practice — the goal isn't just a working CRUD app, but reasoning through real concurrency, failure-recovery, and distributed-systems trade-offs end to end.
