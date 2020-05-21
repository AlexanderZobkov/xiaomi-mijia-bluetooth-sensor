import groovy.transform.CompileStatic

/**
 * A tool to collect measurements from Xiaomi Mijia bluetooth temperature/humidity sensor.
 */
@CompileStatic
class MijiaCollector {

    static void main(String... args) {
        if (!args) {
            println 'No MAC addresses were specified.'
            return
        }
        HCIParser parser = new HCIParser()
        new BufferedReader(new InputStreamReader(System.in)).eachLine { String line ->
            HCIData data = parser.readLine(line)
            if (!data) {
                return
            }
            String mac = data.mac
            if (mac in args) {
                byte[] advertisementData = data.findAdvertisementDataByType(22).dataBytes()
                println Measurement.from(mac, advertisementData).asJson()
            }
        }
        return
    }
}
