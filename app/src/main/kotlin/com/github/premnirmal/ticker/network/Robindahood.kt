package com.github.premnirmal.ticker.network

import com.github.premnirmal.ticker.network.data.QuoteNet
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import io.reactivex.Observable

interface Robindahood {

  /**
   * Retrieves a list of stock quotes.
   *
   * @param query comma separated list of symbols.
   *
   * @return A List of quotes.
   */
  @GET("quotes/")
  @Headers("Accept: application/json")
  fun getStocks(@Query(value = "q") query: String): Observable<List<QuoteNet>>
}