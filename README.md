# market-data-poc

A PoC of near-realtime market data distribution platform

# Intro

## Architecture
TBD

# Running

## Local non-distributed run

1. Build components with ``mvn install``
2. Run local Apache Kafka and Zookeeper at standard ports:
   - **zookeeper** - *localhost:2181*
   - **kafka** - *localhost:8082*
3. Run services:
   - `./run-realtime-processor.sh`
   - `./run-realtime-gateway.sh`
4. Open *localhost:8080* and press *Get candles* button
5. Run feed emulator to start getting trades into system: `./run-feed-emulator.sh`
6. Check *localhost:8080* for received messages statistics.
   There will be something like 
```
AFLT:, latency=2, totalMessagesReceived=32086
Latency stats for last 100 messages: MIN = 1, p50 = 1, p90 = 2, p95 = 3, p99 = 3, MAX = 3
```