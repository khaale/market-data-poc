(trap 'kill 0' SIGINT; \
   ./run-realtime-processor.sh \
   & (sleep 15 && ./run-realtime-gateway.sh) \
   & (sleep 30 && ./run-feed-emulator.sh) \
)