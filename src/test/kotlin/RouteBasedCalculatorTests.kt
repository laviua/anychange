import org.junit.Assert
import org.junit.Test
import stub.FakeBinanceCurrencyProvider
import stub.FakePrivat24CurrencyProvider
import stub.StaticCurrencyProvider
import ua.com.lavi.anychange.AnyCurrencyCalculatorBuilder
import ua.com.lavi.anychange.model.CurrencyCalculatorType
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
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.35).compareTo(calculator.rate(BigDecimal.valueOf(1), "USD", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(253.5).compareTo(calculator.rate(BigDecimal.valueOf(10), "USD", "UAH")) == 0)

        Assert.assertTrue(BigDecimal.valueOf(0.03921569).compareTo(calculator.rate(BigDecimal.valueOf(1), "UAH", "USD").setScale(8, RoundingMode.HALF_EVEN)) == 0)
        Assert.assertTrue(BigDecimal.valueOf(3.92156863).compareTo(calculator.rate(BigDecimal.valueOf(100), "UAH", "USD").setScale(8, RoundingMode.HALF_EVEN)) == 0)

    }

    @Test
    fun should_rate_simple_way_with_positive_correlation() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "USD")
                .addDirection("UAHUSD", "privat24", BigDecimal.valueOf(0.2))
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.4007).compareTo(calculator.rate(BigDecimal.valueOf(1), "USD", "UAH")) == 0)
    }

    @Test
    fun should_rate_simple_way_with_negative_correlation() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "USD")
                .addDirection("UAHUSD", "privat24", BigDecimal.valueOf(0.2).negate())
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.2993).compareTo(calculator.rate(BigDecimal.valueOf(1), "USD", "UAH")) == 0)
    }

    @Test
    fun should_cross_routes() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "BTC")
                .addDirection("BTCUSDT", "binance")
                .addDirection("USDTUSD", "static")
                .addDirection("UAHUSD", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addProviders(listOf(usdtusdProvider, privat24Provider, binanceProvider))
                .addRoute(btcuahRoute)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(256819.3974450).compareTo(calculator.rate(BigDecimal.valueOf(1), "BTC", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(2568193.974450).compareTo(calculator.rate(BigDecimal.valueOf(10), "BTC", "UAH")) == 0)
        Assert.assertTrue(BigDecimal.valueOf(0.00000385).compareTo(calculator.rate(BigDecimal.valueOf(1), "UAH", "BTC").setScale(8, RoundingMode.HALF_EVEN)) == 0)
        Assert.assertTrue(BigDecimal.valueOf(0.0000385).compareTo(calculator.rate(BigDecimal.valueOf(10), "UAH", "BTC").setScale(8, RoundingMode.HALF_EVEN)) == 0)
    }

    @Test
    fun should_get_all_rates() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "BTC")
                .addDirection("BTCUSDT", "binance", BigDecimal.valueOf(0.2))
                .addDirection("USDTUSD", "static")
                .addDirection("UAHUSD", "privat24")
                .build()

        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("UAH", "USD")
                .addDirection("UAHUSD", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addProviders(listOf(usdtusdProvider, privat24Provider, binanceProvider))
                .addRoute(uahusdsimpleRoute)
                .addRoute(btcuahRoute)
                .build()

        val rates = calculator.rates()
        Assert.assertTrue(2 == rates.size)

        Assert.assertTrue("UAH" == rates[0].baseAsset)
        Assert.assertTrue("USD" == rates[0].quoteAsset)
        Assert.assertTrue(BigDecimal.valueOf(25.35).compareTo(rates[0].bid) == 0)
        Assert.assertTrue(BigDecimal.valueOf(25.5).compareTo(rates[0].ask) == 0)

        Assert.assertTrue("UAH" == rates[1].baseAsset)
        Assert.assertTrue("BTC" == rates[1].quoteAsset)
        Assert.assertTrue(BigDecimal.valueOf(257333.0362398900).compareTo(rates[1].bid) == 0)
        Assert.assertTrue(BigDecimal.valueOf(260227.16940780).compareTo(rates[1].ask) == 0)

        Assert.assertTrue(rates[1].matches("UAH"))
        Assert.assertTrue(rates[1].matches("BTC"))

    }
}