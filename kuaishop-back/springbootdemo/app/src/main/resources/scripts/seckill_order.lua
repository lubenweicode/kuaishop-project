local stockKey = KEYS[1]
local userLimitKey = KEYS[2]
local userOrderKey = KEYS[3]
local userId = ARGV[1]
local quantity = tonumber(ARGV[2])
local userLimitDefault = tonumber(ARGV[3])
local stock = redis.call('get', stockKey)
if not stock then
    return 1
end
local currentStock = tonumber(stock)
if currentStock < quantity then
    return 2
end
local hasOrdered = redis.call('exists', userOrderKey)
if hasOrdered == 1 then
    return 4
end
local userBuyCount = redis.call('hget', userLimitKey, userId)
if not userBuyCount then
    userBuyCount = 0
else
    userBuyCount = tonumber(userBuyCount)
end
if userBuyCount + quantity > userLimitDefault then
    return 3
end
redis.call('decrby', stockKey, quantity)
redis.call('hset', userLimitKey, userId, userBuyCount + quantity)
redis.call('set', userOrderKey, '1', 'EX', 3600)
return 0