-- Token Bucket Lua Script

-- KEYS[1] = bucket key
-- ARGV[1] = capacity
-- ARGV[2] = refill_rate (tokens per second)
-- ARGV[3] = now (epoch seconds)

local bucket = redis.call("HMGET", KEYS[1], "tokens", "last_refill_ts")

local tokens = tonumber(bucket[1])
local last_refill_ts = tonumber(bucket[2])

local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- First request: initialize bucket
if tokens == nil then
    tokens = capacity
    last_refill_ts = now
end

-- Refill logic
local delta = now - last_refill_ts
if delta > 0 then
    local refill = delta * refill_rate
    tokens = math.min(capacity, tokens + refill)
    last_refill_ts = now
end

-- Consume token
if tokens < 1 then
    -- Save state and reject
    redis.call("HMSET", KEYS[1],
        "tokens", tokens,
        "last_refill_ts", last_refill_ts)
    redis.call("EXPIRE", KEYS[1], 120)
    return 0
end

tokens = tokens - 1

-- Save updated state
redis.call("HMSET", KEYS[1],
    "tokens", tokens,
    "last_refill_ts", last_refill_ts)

-- Safety TTL
redis.call("EXPIRE", KEYS[1], 120)

return 1