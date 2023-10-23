local key = KEYS[1]
local val = redis.call("GET", key);
if tonumber(val) >= tonumber(ARGV[1])
then
    redis.call('SET',key,tonumber(val) - tonumber(ARGV[1]))
    return 1

else
    return 0
end