import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import composables.ListEntry
import entities.Quote
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

lateinit var quote: Quote

private fun fetchQuote(symbol: String = "IBM", apiKey: String = "demo") {
    quote = runBlocking {
        val httpResponse: HttpResponse =
            client.get("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$symbol&apikey=$apiKey")
        httpResponse.receive()
    }
}

// https://ktor.io/docs/json.html#gson
// https://ktor.io/docs/response.html

fun main() {
    /*
    todos:
    make fetchQuote asyn
     */


    // init
    fetchQuote()

    // run app
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Watchlist - Compose",
            state = rememberWindowState(width = 1024.dp, height = 800.dp)
        ) {
            val count = remember { mutableStateOf(0) }
            MaterialTheme {
                Column {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = "Watchlist")
                                },
                                navigationIcon = {
                                    Button(onClick = { }) {
                                        Icon(Icons.Filled.Home, contentDescription = "Home")
                                    }
                                }
                            )
                        },
                        content = {
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
                                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh Stock Quotes")
                                    Text("Refresh")
                                }
                                ListEntry(quote)
                                Text(quote.globalQuote.toString())
                            }
                        }
                    )
                }
            }
        }
    }
}

// https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=IBM&apikey=demo
// https://stackoverflow.com/questions/58379740/data-fetching-from-an-api-in-android