import org.junit.Assert
import org.junit.Test
import ua.com.lavi.anychange.provider.stub.FakePrivat24CurrencyProvider
import ua.com.lavi.anychange.provider.stub.StaticCurrencyProvider
import ua.com.lavi.anychange.AnyCurrencyCalculatorBuilder
import ua.com.lavi.anychange.exception.UnsupportedConversionException
import ua.com.lavi.anychange.model.ExchangeSide
import ua.com.lavi.anychange.model.CurrencyCalculatorType
import ua.com.lavi.anychange.CurrencyRouteBuilder
import ua.com.lavi.anychange.provider.stub.FakeBinanceCurrencyProvider
import java.math.BigDecimal

class RouteBasedCalculatorTests {

    private val binanceProvider = FakeBinanceCurrencyProvider()
    private val staticProvider = StaticCurrencyProvider()
    private val privat24Provider = FakePrivat24CurrencyProvider()

    @Test
    fun should_get_all_rates() {

        val btcuahRoute = CurrencyRouteBuilder()
                .addPoints("BTC", "UAH")
                .addDirection("BTCUSDT", "binance", BigDecimal.valueOf(0.2))
                .addDirection("USDTUSD", "static")
                .addDirection("USDUAH", "privat24")
                .scale(6)
                .build()

        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addProviders(listOf(staticProvider, privat24Provider, binanceProvider))
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
        Assert.assertTrue(BigDecimal.valueOf(257333.03624).compareTo(rates[1].bid) == 0)
        Assert.assertTrue(BigDecimal.valueOf(260227.169408).compareTo(rates[1].ask) == 0)
    }

    @Test
    fun should_make_simple_eurusd_sell_exchange() {
        val eurUsdRoute = CurrencyRouteBuilder()
                .addPoints("EUR", "USD")
                .addDirection("EURUSD", "static")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(eurUsdRoute)
                .addProvider(staticProvider)
                .build()

        // we buy 1 EUR for 1.3655 USD
        Assert.assertTrue(BigDecimal.valueOf(1.3655).compareTo(calculator.exchange("EURUSD", ExchangeSide.BUY, BigDecimal.valueOf(1))) == 0)

        // we sell 1 EUR and got 1.3652 USD
        Assert.assertTrue(BigDecimal.valueOf(1.3652).compareTo(calculator.exchange("EURUSD", ExchangeSide.SELL, BigDecimal.valueOf(1))) == 0)
    }

    @Test
    fun should_make_simple_sell_exchange() {
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
        Assert.assertTrue(BigDecimal.valueOf(2535).compareTo(calculator.exchange("USDUAH", ExchangeSide.SELL, BigDecimal.valueOf(100))) == 0)
    }

    @Test
    fun should_make_simple_buy_exchange() {
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .addDirection("USDUAH", "privat24")
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        // we buy 10 USD and give 255 UAH
        Assert.assertTrue(BigDecimal.valueOf(255).compareTo(calculator.exchange("USDUAH", ExchangeSide.BUY, BigDecimal.valueOf(10))) == 0)
    }

    @Test
    fun should_make_reverse_buy_exchange() {
        val ethBtc = CurrencyRouteBuilder()
                .addPoints("BTC", "ETH")
                .addDirection("ETHBTC", "binance", true)
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(ethBtc)
                .addProvider(binanceProvider)
                .build()

        val rate = calculator.rate("BTCETH")!!

        Assert.assertTrue("BTC" == rate.baseAsset)
        Assert.assertTrue("ETH" == rate.quoteAsset)
        Assert.assertTrue(BigDecimal("46.670089139870257152191160685117").compareTo(rate.bid) == 0)
        Assert.assertTrue(BigDecimal("46.674445740956826137689614935823").compareTo(rate.ask) == 0)

        // we buy 1 BTC and give 46.6744.. ETH
        val exchangeRate = calculator.exchange("BTCETH", ExchangeSide.BUY, BigDecimal.ONE)
        Assert.assertTrue(BigDecimal("46.674445740956826137689614935823").compareTo(exchangeRate) == 0)
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
                .addProviders(listOf(staticProvider, privat24Provider, binanceProvider))
                .addRoute(btcuahRoute)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(259707.75390).compareTo(calculator.exchange("BTCUAH", ExchangeSide.BUY, BigDecimal.valueOf(1))) == 0)
        Assert.assertTrue(BigDecimal.valueOf(256819.3974450).compareTo(calculator.exchange("BTCUAH", ExchangeSide.SELL, BigDecimal.valueOf(1))) == 0)
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

        Assert.assertTrue(BigDecimal.valueOf(255.510).compareTo(calculator.exchange("USDUAH", ExchangeSide.BUY, BigDecimal.valueOf(10))) == 0)
        Assert.assertTrue(BigDecimal.valueOf(254.0070).compareTo(calculator.exchange("USDUAH", ExchangeSide.SELL, BigDecimal.valueOf(10))) == 0)
    }

    @Test
    fun should_rate_simple_way_with_negative_correlation() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .scale(2)
                .addDirection("USDUAH", "privat24", BigDecimal.valueOf(0.2).negate())
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(254.5).compareTo(calculator.exchange("USDUAH", ExchangeSide.BUY, BigDecimal.valueOf(10))) == 0)
        Assert.assertTrue(BigDecimal.valueOf(253).compareTo(calculator.exchange("USDUAH", ExchangeSide.SELL, BigDecimal.valueOf(10))) == 0)
    }

    @Test(expected = UnsupportedConversionException::class)
    fun should_throw_exception() {

        // 25.35/25.50
        val uahusdsimpleRoute = CurrencyRouteBuilder()
                .addPoints("USD", "UAH")
                .scale(2)
                .addDirection("USDUAH", "privat24", BigDecimal.valueOf(0.2).negate())
                .build()

        val calculator = AnyCurrencyCalculatorBuilder()
                .type(CurrencyCalculatorType.ROUTE_BASED)
                .addRoute(uahusdsimpleRoute)
                .addProvider(privat24Provider)
                .build()

        Assert.assertTrue(BigDecimal.valueOf(254.49).compareTo(calculator.exchange("EURUSD", ExchangeSide.BUY, BigDecimal.valueOf(10))) == 0)
    }
}