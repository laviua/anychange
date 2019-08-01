# AnyChange
This library gives an ability to make cross converting from one currency to another according to bid/ask rates with the different rates provider.

Motivation:
Make a dynamic calculation between several different currencies (fiat, crypto, etc), that's why I decided to use String instead of the "Currency" data type.

Use cases:

For example we need to get a rate of the pair BTCUAH, hence we need to calculate pairs via several providers:
BTCUSDT (binance) -> USDTUSD (~static price) -> USDUAH (privat24).

Or we want to get: BTCETH, because only ETHBTC exists in the Binance.

Please feel free to open issues, especially for fix in calculation.
Thanks