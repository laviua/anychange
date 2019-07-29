import org.junit.Assert
import org.junit.Test
import stub.FakeBinanceCurrencyProvider
import stub.FakePrivat24CurrencyProvider
import stub.StaticCurrencyProvider
import ua.com.lavi.anychange.AnyCurrencyCalculatorBuilder
import ua.com.lavi.anychange.model.AnychangeSide
import ua.com.lavi.anychange.model.CurrencyCalculatorType
import ua.com.lavi.anychange.model.CurrencyRouteBuilder
import java.math.BigDecimal

class RouteBasedCalculatorTests {

    private val binanceProvider = FakeBinanceCurrencyProvider()
    private val usdtusdProvider = StaticCurrencyProvider()
    private val privat24Provider = FakePrivat24CurrencyProvider()

    @Test
    fun should_rate_simple_way() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.35).compareTo(calculator.calculate(BigDecimal.valueOf(1), "USD", "UAH").amount) == 0)
        Assert.assertTrue(BigDecimal.valueOf(253.5).compareTo(calculator.calculate(BigDecimal.valueOf(10), "USD", "UAH").amount) == 0)
    }

    @Test
    fun should_rate_simple_way_with_positive_correlation() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24", BigDecimal.valueOf(0.2))
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.4007).compareTo(calculator.calculate(BigDecimal.valueOf(1), "USD", "UAH").amount) == 0)
    }

    @Test
    fun should_rate_simple_way_with_negative_correlation() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24", BigDecimal.valueOf(0.2).negate())
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(25.2993).compareTo(calculator.calculate(BigDecimal.valueOf(1), "USD", "UAH").amount) == 0)
    }

    @Test
    fun should_cross_routes() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("BTC", "UAH")
                .addDirection("BTCUSDT", "binance")
                .addDirection("USDTUSD", "static")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addProviders(listOf(usdtusdProvider, privat24Provider, binanceProvider))
                .addRoute(btcuahRoute)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(256819.3974450).compareTo(calculator.calculate(BigDecimal.valueOf(1), "BTC", "UAH").amount) == 0)
        Assert.assertTrue(BigDecimal.valueOf(2568193.974450).compareTo(calculator.calculate(BigDecimal.valueOf(10), "BTC", "UAH").amount) == 0)
    }

    @Test
    fun should_get_all_rates() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("BTC", "UAH")
                .addDirection("BTCUSDT", "binance", BigDecimal.valueOf(0.2))
                .addDirection("USDTUSD", "static")
                .addDirection("USDUAH", "privat24")
                .build()

        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addProviders(listOf(usdtusdProvider, privat24Provider, binanceProvider))
                .addRoutes(arrayListOf(uahusdsimpleRoute, btcuahRoute))
                .build()

        val rates = calculator.rates()
        Assert.assertTrue(2 == rates.size)

        Assert.assertTrue("USD" == rates[0].baseAsset)
        Assert.assertTrue("UAH" == rates[0].quoteAsset)
        Assert.assertTrue(BigDecimal.valueOf(25.35).compareTo(rates[0].bid) == 0)
        Assert.assertTrue(BigDecimal.valueOf(25.5).compareTo(rates[0].ask) == 0)

        Assert.assertTrue("BTC" == rates[1].baseAsset)
        Assert.assertTrue("UAH" == rates[1].quoteAsset)
        Assert.assertTrue(BigDecimal.valueOf(257333.0362398900).compareTo(rates[1].bid) == 0)
        Assert.assertTrue(BigDecimal.valueOf(260227.16940780).compareTo(rates[1].ask) == 0)
    }

    @Test
    fun should_make_sell_exchange() {
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        // we sell 100 USD and got 2535 UAH
        val result = calculator.exchange("USDUAH", AnychangeSide.SELL, BigDecimal.valueOf(100))
        Assert.assertTrue(BigDecimal.valueOf(2535).compareTo(result.amount) == 0)
        Assert.assertTrue(result.currency == "UAH")
    }

    @Test
    fun should_make_buy_exchange() {
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        // we buy x UAH by 10 USD, after calculation we got 255 UAH
        val result = calculator.exchange("USDUAH", AnychangeSide.BUY, BigDecimal.valueOf(10))
        Assert.assertTrue(BigDecimal.valueOf(255).compareTo(result.amount) == 0)
        Assert.assertTrue(result.currency == "UAH")
    }
}