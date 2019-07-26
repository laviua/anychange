import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import stub.FakeBinanceCurrencyProvider
import stub.FakePrivat24CurrencyProvider
import stub.USDTUSDCurrencyProvider
import ua.com.lavi.anychange.CurrencyCalculatorBuilder
import ua.com.lavi.anychange.model.CalculatorType
import ua.com.lavi.anychange.model.CurrencyRouteBuilder
import java.math.BigDecimal

class RouteBasedCalculatorTests {

    private val binanceProvider = FakeBinanceCurrencyProvider()
    private val usdtusdProvider = USDTUSDCurrencyProvider()
    private val privat24Provider = FakePrivat24CurrencyProvider()

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun should_rate_simple_way() {

        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "USD")
                .addDirection("UAHUSD", "privat24")
                .build()

        val calculator = CurrencyCalculatorBuilder()
                .type(CalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        val uahusdRate1 = calculator.rate(BigDecimal.valueOf(1), "UAH", "USD")
        val uahusdRate10 = calculator.rate(BigDecimal.valueOf(10), "UAH", "USD")
        val usduahRate1 = calculator.rate(BigDecimal.valueOf(1), "USD", "UAH")

        Assert.assertTrue(BigDecimal.valueOf(25.50).compareTo(uahusdRate1) == 0)
        Assert.assertTrue(BigDecimal.valueOf(255.00).compareTo(uahusdRate10) == 0)

        Assert.assertTrue(BigDecimal.valueOf(0.039447732).compareTo(usduahRate1) == 0)
    }

    @Test
    fun should_cross_routes() {

        val uahBtcRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "BTC")
                .addDirection("UAHUSD", "privat24")
                .addDirection("USDTUSD", "usdtusd")
                .addDirection("BTCUSDT", "binance")
                .build()

        val calculator = CurrencyCalculatorBuilder()
                .type(CalculatorType.ROUTE_BASED)
                .addProvider(usdtusdProvider)
                .addProvider(privat24Provider)
                .addProvider(binanceProvider)
                .addRoute(uahBtcRoute)
                .build()

        val btcUah = calculator.rate(BigDecimal.valueOf(1), "BTC", "UAH")
        log.debug("Result of calculation BTC > UAH: $btcUah")

        val uahBtc = calculator.rate(BigDecimal.valueOf(1), "UAH", "BTC")
        log.debug("Result of calculation UAH > BTC: $btcUah")
        Assert.assertEquals(BigDecimal.valueOf(256819.397445), btcUah.stripTrailingZeros())

        //TODO
        Assert.assertEquals(BigDecimal.valueOf(256819.397445), uahBtc.stripTrailingZeros())
    }
}