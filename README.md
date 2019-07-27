# AnyChange
This library gives an ability to make cross converting from one currency to another according to bid/ask rates with the different rates provider.

Motivation:
Make a dynamic calculation between several different currencies (fiat, crypto, etc), that's why I decided to use String instead of the "Currency" data type.

Please feel free to open issues, especially for fix in calculation.
Thanks

Example:

    @Test
    fun should_rate_simple_way() {

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
