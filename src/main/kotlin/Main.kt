// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.gson.annotations.SerializedName
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

val client = HttpClient() {
    install(JsonFeature) {
        serializer = GsonSerializer() {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }
}

data class GlobalQuote(
    @SerializedName("01. symbol")
    val symbol: String,
    @SerializedName("02. open")
    val open: Float,
    @SerializedName("03. high")
    val high: Float,
    @SerializedName("04. low")
    val low: Float,
    @SerializedName("05. price")
    val price: Float,
    @SerializedName("06. volume")
    val volume: Float,
    @SerializedName("07. latest trading day")
    val latestTradingDay: String,
    @SerializedName("08. previous close")
    val previousClose: Float,
    @SerializedName("09. change")
    val change: Float,
    @SerializedName("10. change percent")
    val changePercent: String,
)

data class Quote(
    @SerializedName("Global Quote")
    val globalQuote: GlobalQuote
)

lateinit var quote: Quote

private fun fetchQuote() {
    quote = runBlocking {
        val httpResponse: HttpResponse =
            client.get("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo")
        httpResponse.receive<Quote>()
    }
}

// https://ktor.io/docs/json.html#gson
// https://ktor.io/docs/response.html

fun main() {
    fetchQuote()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Watchlist - Compose",
            state = rememberWindowState(width = 1024.dp, height = 800.dp)
        ) {
            val count = remember { mutableStateOf(0) }
            MaterialTheme {
                Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            count.value++
                        }) {
                        Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
                    }
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            count.value = 0
                        }) {
                        Text("Reset")
                    }
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            print("Running fetch ... ")
                            fetchQuote()
                            println("done")
                        }) {
                        Text("Refresh")
                    }
                    Text(quote.globalQuote.toString())
                }
            }
        }
    }
}

// https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
// https://stackoverflow.com/questions/58379740/data-fetching-from-an-api-in-android