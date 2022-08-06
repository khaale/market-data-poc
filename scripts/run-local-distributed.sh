(trap 'kill 0' SIGINT; \
   ./run-realtime-processor.sh \
   & ./run-realtime-processor.sh \
   & ./run-realtime-processor.sh \
   & (sleep 15 && ./run-realtime-gateway.sh -Dserver.port=8080) \
   & (sleep 15 && ./run-realtime-gateway.sh -Dserver.port=8081) \
   & (sleep 30 && ./run-feed-emulator.sh) \
)