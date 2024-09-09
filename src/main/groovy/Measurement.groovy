import groovy.json.JsonGenerator
import groovy.transform.CompileStatic
import groovy.transform.ToString

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant

/**
 * A measurement from the sensor.
 */
@CompileStatic
@ToString(includeNames = true)
class Measurement {

    /**
     * Constructs a measurement from advertising data.
     * @param mac address of a device from which measurement is received.
     * @param data advertising data.
     * @return measurement.
     */
    @SuppressWarnings('DuplicateNumberLiteral')
    static Measurement from(String mac, byte[] data) {
        Measurement reply
        byte type = data[13]
        switch (type) {
            case 0x0D:
                float t = (float)(decodeValue(data[16..17] as byte[]) / 10.0f)
                float h = (float)(decodeValue(data[18..19] as byte[]) / 10.0f)
                reply = new Measurement(temperature: t, humidity: h)
                break
            case 0x0A:
                float b = data[16]
                reply = new Measurement(batteryLevel: b)
                break
            case 0x06:
                float h = (float)(decodeValue(data[16..17] as byte[]) / 10.0f)
                reply = new Measurement(humidity: h)
                break
            case 0x04:
                float t = (float)(decodeValue(data[16..17] as byte[]) / 10.0f)
                reply = new Measurement(temperature: t)
                break
            default:
                throw new IOException("Not supported type: ${type}")
        }
        reply.mac = mac
        reply.time = Instant.now()
        return reply
    }

    /**
     * Returns JSON presentation of the measurement.
     *
     * @return JSON object in string form.
     */
    String asJson() {
        return generator.toJson(this)
    }

    private static short decodeValue(byte[] data) {
        return ByteBuffer.wrap(data)
                .order(ByteOrder.LITTLE_ENDIAN)
                .short
    }

    private final JsonGenerator generator = new JsonGenerator.Options()
            .excludeNulls()
            .addConverter(Instant) { it.toString() }
            .build()

    /**
     * MAC address of a monitored sensor.
     */
    String mac

    /**
     * A time when measurement was collected.
     */
    Instant time

    /**
     * Temperature.
     */
    Float temperature

    /**
     * Humidity.
     */
    Float humidity

    /**
     * Battery level.
     */
    Float batteryLevel

}
