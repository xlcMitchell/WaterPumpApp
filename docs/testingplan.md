# Water Pump App - MQTT Test Plan

## 1. Test Objectives
- Verify secure connection to HiveMQ broker over WebSockets.
- Validate publish and subscribe functionality.
- Ensure proper handling of network errors and disconnects.
- Confirm UI updates correctly when receiving messages.
- Verify security credentials are handled safely.

---

## 2. Test Environment
- **Devices:** Android 10+, 12+, emulators and real devices
- **Broker:** HiveMQ Cloud (test credentials)
- **MQTT Topics:** `test/waterpump/status`, `test/waterpump/control`
- **Network Conditions:** Normal Wi-Fi, LTE, intermittent connectivity

---

## 3. Test Scenarios

### 3.1 Connection Tests
- [ ] Connect to broker with valid credentials → Success, logs show "Connected successfully"
- [ ] Connect with invalid credentials → Connection fails gracefully, error shown
- [ ] Connect with unreachable broker → Connection fails gracefully, retry mechanism works

### 3.2 Publish/Subscribe Tests
- [ ] Subscribe to `test/waterpump/status` → Messages received correctly
- [ ] Publish "ON"/"OFF" to `test/waterpump/control` → Message sent successfully
- [ ] Subscribe + publish → Messages delivered correctly, no duplicates

### 3.3 Message Handling & UI
- [ ] Incoming messages displayed correctly in UI
- [ ] Empty payload → App does not crash, shows placeholder
- [ ] Large payload (>1KB) → App handles safely, no crash

### 3.4 Network / Disconnect Tests
- [ ] Disconnect during operation → App detects disconnection
- [ ] Reconnect after network restore → Subscriptions restored, messages received
- [ ] Slow network → Messages eventually delivered, UI shows loading/error

### 3.5 Security Tests
- [ ] Credentials handling → Not exposed in logs or UI
- [ ] TLS connection → Connection succeeds, certificate verified
- [ ] Unauthorized access → Connection rejected, proper error handled

### 3.6 Performance / Stress Tests
- [ ] High frequency messages (10/sec) → App handles without crash or lag
- [ ] Long uptime (24h) → App remains connected or reconnects after timeout

---

## 4. Tools
- HiveMQ Web UI (manual publishing)
- MQTT client: MQTT.fx, MQTT Explorer
- Android Logcat for debugging
- Network simulation tools: Emulator network settings, Charles Proxy

---

## 5. Acceptance Criteria
- App connects securely and reliably
- Messages published/subscribed correctly
- UI reflects messages accurately
- Network interruptions handled gracefully
- No crashes under normal or stress conditions
- Credentials and TLS handled securely