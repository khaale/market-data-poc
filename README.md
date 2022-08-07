# market-data-poc

A PoC of near-realtime market data distribution platform, based on Apache Ignite.

# Architecture

## Solution components
![architecture-components](/docs/diagrams/architecture-components.png)

# Running

## Prepare for local running
1. Build components with ``mvn install``
2. Run local Apache Kafka and Zookeeper at standard ports:
   - **zookeeper** - *localhost:2181*
   - **kafka** - *localhost:8082*

## Local non-distributed run

1. Run services via scripts from `scripts` directory: `./run-local-nondistributed.sh`
2. Wait for some time, open *localhost:8080* and press *Get candles* button
3. Check *localhost:8080* for received messages statistics.
   There will be something like 
```
AFLT:, latency=2, totalMessagesReceived=32086
Latency stats for last 100 messages: MIN = 1, p50 = 1, p90 = 2, p95 = 3, p99 = 3, MAX = 3
```

## Local distributed run

1. Run services via scripts from `scripts` directory: `./run-local-distributed.sh`
2. Wait for some time, open *localhost:8080* and press *Get candles* button
3. Check *localhost:8080* for received messages statistics.
   There will be something like
```
AFLT:, latency=2, totalMessagesReceived=18316
Latency stats for last 100 messages: MIN = 1, p50 = 2, p90 = 3, p95 = 3, p99 = 3, MAX = 3

Ignite stats:
CACHE NODE: [ id=ff7db834-2b68-4319-bbfa-d64934c50edd, consistentId=0:0:0:0:0:0:0:1,127.0.0.1,172.30.240.1,172.31.48.1,192.168.0.101,192.168.56.1:5555] Metrics: sentMsgsCnt=83002, sentBytesCnt=25867930, rcvdMsgsCnt=71480, rcvdBytesCnt=18155672, outMesQueueSize=0
CACHE NODE: [ id=c4a2e749-e900-4faa-babc-f9a913f5ea1d, consistentId=0:0:0:0:0:0:0:1,127.0.0.1,172.30.240.1,172.31.48.1,192.168.0.101,192.168.56.1:5557] Metrics: sentMsgsCnt=76145, sentBytesCnt=22885128, rcvdMsgsCnt=65863, rcvdBytesCnt=16805141, outMesQueueSize=0
CACHE NODE: [ id=d54cdb1d-3b5c-48fd-b34d-a8ec4d61c1e6, consistentId=0:0:0:0:0:0:0:1,127.0.0.1,172.30.240.1,172.31.48.1,192.168.0.101,192.168.56.1:5556] Metrics: sentMsgsCnt=82497, sentBytesCnt=23985410, rcvdMsgsCnt=73596, rcvdBytesCnt=18827030, outMesQueueSize=0
CLIENT NODE: [ id=502bb51b-a964-407d-80d6-7af2095660db, consistentId=0:0:0:0:0:0:0:1,127.0.0.1,172.30.240.1,172.31.48.1,192.168.0.101,192.168.56.1:5558] Metrics: sentMsgsCnt=60, sentBytesCnt=18670, rcvdMsgsCnt=15489, rcvdBytesCnt=9532670, outMesQueueSize=0
CLIENT NODE: [ id=103a199e-6678-4f81-a85e-3dc3ea2b88e4, consistentId=0:0:0:0:0:0:0:1,127.0.0.1,172.30.240.1,172.31.48.1,192.168.0.101,192.168.56.1:5559] Metrics: sentMsgsCnt=61, sentBytesCnt=19004, rcvdMsgsCnt=15644, rcvdBytesCnt=9629418, outMesQueueSize=0
```
