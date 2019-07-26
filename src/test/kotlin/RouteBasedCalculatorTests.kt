import org.junit.Assert
import org.junit.Test
import stub.FakeBinanceCurrencyProvider
import stub.FakePrivat24CurrencyProvider
import stub.StaticCurrencyProvider
import ua.com.lavi.anychange.AnyCurrencyCalculatorBuilder
import ua.com.lavi.anychange.model.CalculatorType
import ua.com.lavi.anychange.model.CurrencyRouteBuilder
import java.math.BigDecimal
import java.math.RoundingMode

class RouteBasedCalculatorTests {

    private val binanceProvider = FakeBinanceCurrencyProvider()
    private val usdtusdProvider = StaticCurrencyProvider()
    private val privat24Provider = FakePrivat24CurrencyProvider()

    @Test
    fun should_rate_simple_way() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "USD")
                .addDirection("UAHUSD", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.35).compareTo(calculator.rate(BigDecimal.valueOf(1), "USD", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(253.5).compareTo(calculator.rate(BigDecimal.valueOf(10), "USD", "UAH")) == 0)

        Assert.assertTrue(BigDecimal.valueOf(0.03921569).compareTo(calculator.rate(BigDecimal.valueOf(1), "UAH", "USD").setScale(8, RoundingMode.HALF_EVEN)) == 0)
        Assert.assertTrue(BigDecimal.valueOf(3.92156863).compareTo(calculator.rate(BigDecimal.valueOf(100), "UAH", "USD").setScale(8, RoundingMode.HALF_EVEN)) == 0)

    }

    @Test
    fun should_cross_routes() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("BTC", "UAH")
                .addDirection("BTCUSDT", "binance")
                .addDirection("USDTUSD", "static")
                .addDirection("UAHUSD", "privat24")
                .build()

        val uahbtcRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "BTC")
                .addDirection("UAHUSD", "privat24")
                .addDirection("USDTUSD", "static")
                .addDirection("BTCUSDT", "binance")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CalculatorType.ROUTE_BASED)
                .providers(listOf(usdtusdProvider, privat24Provider, binanceProvider))
                .addRoute(btcuahRoute)
                .addRoute(uahbtcRoute)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(256819.3974450).compareTo(calculator.rate(BigDecimal.valueOf(1), "BTC", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(2568193.974450).compareTo(calculator.rate(BigDecimal.valueOf(10), "BTC", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(0.00000385).compareTo(calculator.rate(BigDecimal.valueOf(1), "UAH", "BTC").setScale(8, RoundingMode.HALF_EVEN)) == 0)
        Assert.assertTrue(BigDecimal.valueOf(0.0000385).compareTo(calculator.rate(BigDecimal.valueOf(10), "UAH", "BTC").setScale(8, RoundingMode.HALF_EVEN)) == 0)
    }
}