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
import kotlin.system.exitProcess

const val APIKEY_ENVVAR_KEY = "ALPHAVANTAGE_APIKEY"

val client = HttpClient() {
    install(JsonFeature) {
        serializer = GsonSerializer() {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }
}

val symbols: Array<String> = arrayOf("IBM", "ADBE")

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
    @Suppress("UNUSED_VARIABLE")
    val apikey = System.getenv(APIKEY_ENVVAR_KEY)?.let {
        println("Using api key: $it")
    } ?: run {
        println("You have to provide an api key via the env var $APIKEY_ENVVAR_KEY")
        exitProcess(1)
    }
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
                                },
                                actions = {
                                    IconButton(onClick = {
                                        print("Running fetch ... ")
                                        fetchQuote()
                                        println("done")
                                    }) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh Stock Quotes")
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