-- Character set for generating random characters
local charset = "abcdef0123456789"
-- Counter for simulating a more precise timestamp
local counter = 0
-- Get the process ID (can be obtained via an external command in Lua)
local pid = tonumber(io.popen("echo $$"):read("*a"))

-- Function to generate a UUID
function uuid()
    -- Get the current timestamp
    local timestamp = os.time()
    -- Increment the counter
    counter = counter + 1
    -- Generate the random part
    local random_part = ""
    for i = 1, 12 do
        random_part = random_part .. string.sub(charset, math.random(1, #charset), math.random(1, #charset))
    end
    -- Convert the PID to hexadecimal and take a part of it
    local pid_hex = string.format("%x", pid)
    pid_hex = string.sub(pid_hex, -4)
    -- Combine the timestamp and the counter and convert to hexadecimal
    local combined_timestamp = timestamp * 1000 + counter
    local timestamp_hex = string.format("%x", combined_timestamp)
    -- Pad the timestamp part to the required length
    while #timestamp_hex < 12 do
        timestamp_hex = "0" .. timestamp_hex
    end
    -- Concatenate according to the UUID format
    return string.format("%s-%s-%s-%s-%s%s",
        string.sub(timestamp_hex, 1, 8),
        string.sub(timestamp_hex, 9, 12),
        "4" .. string.sub(timestamp_hex, 13, 14),
        string.sub("89ab", math.random(1, 4), math.random(1, 4)) .. string.sub(random_part, 1, 3),
        string.sub(random_part, 4),
        pid_hex
    )
end

function request()
    -- Generate a UUID as the orderId
    local orderId = uuid()
    -- Construct the request body
    local body = '{"type":"TRANSFER","orderId":"' .. orderId .. '","currency":"CNY","amount":1500.00,"date":"2025-04-02T10:10:00Z","cardId":"CARD_001","toCardId":"CARD_002","description":"CARD_001 to CARD_002","status":"PROCESSING","channel":"MOBILE_APP"}'
    -- Return a formatted POST request
    return wrk.format("POST", nil, {["Content-Type"] = "application/json"}, body)
end