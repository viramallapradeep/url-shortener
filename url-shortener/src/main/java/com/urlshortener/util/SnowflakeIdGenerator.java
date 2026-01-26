package com.urlshortener.util;
public class SnowflakeIdGenerator {

    // ===== CONFIGURATION =====
    private static final long EPOCH = 1700000000000L; // custom epoch

    private static final long MACHINE_ID_BITS = 10;   // 1024 machines
    private static final long SEQUENCE_BITS = 12;     // 4096 ids/ms

    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    // ===== STATE =====
    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    // ===== CONSTRUCTOR =====
    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(
                    "Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
    }

    // ===== CORE METHOD =====
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // clock moved backwards (rare but critical)
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    "Clock moved backwards. Refusing to generate ID");
        }

        if (currentTimestamp == lastTimestamp) {
            // same millisecond → increment sequence
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // sequence exhausted → wait for next millisecond
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // new millisecond → reset sequence
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    // ===== HELPERS =====
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }

    // ===== QUICK TEST =====
    public static void main(String[] args) {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(99);

        for (int i = 0; i < 5; i++) {
            System.out.println(generator.nextId());
        }
    }
}
