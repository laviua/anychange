# AnyChange
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0) [![Maven Central](https://img.shields.io/maven-central/v/ua.com.lavi/anychange.svg?style=plastic)]() [![Code Climate](https://codeclimate.com/github/laviua/anychange/badges/gpa.svg)](https://codeclimate.com/github/laviua/anychange)

This library gives an ability to make cross converting from one currency to another according to bid/ask rates with the different rates provider.

Motivation:
Make a dynamic calculation between several different currencies (fiat, crypto, etc), that's why I decided to use String instead of the "Currency" data type.

Use cases:

For example we need to get a rate of the pair BTCUAH, hence we need to calculate pairs via several providers:
BTCUSDT (binance) -> USDTUSD (~static price) -> USDUAH (privat24).

Or we want to get: BTCETH, because only ETHBTC exists in the Binance.

Please feel free to open issues, especially for fix in calculation.
Thanks
