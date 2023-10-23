local key = KEYS[1]
redis.debug('hello',key)
redis.log(redis.LOG_WARNING, "foo bar")
return 1
