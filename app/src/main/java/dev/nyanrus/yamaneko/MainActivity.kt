package dev.nyanrus.yamaneko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.nyanrus.yamaneko.ui.theme.YamanekoTheme
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.engine.gecko.NestedGeckoView
import mozilla.components.concept.engine.EngineSession
import mozilla.components.concept.engine.EngineView
import org.mozilla.geckoview.GeckoRuntime
import org.mozilla.geckoview.GeckoRuntimeSettings
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = GeckoRuntimeSettings.Builder()
        val runtimeSettings = builder
            .aboutConfigEnabled(true)
            .build()
        val runtime = GeckoRuntime.create(applicationContext, runtimeSettings)

        val engine = GeckoEngine(applicationContext,null,runtime)
        val session = engine.createSession(false)
        session.loadUrl("qiita.com")

        setContent {
            YamanekoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(bottomBar = {
                        BottomAppBar(modifier=Modifier.height(80.dp)) {
                            Box {
                                Text("hello")
                            }
                        }
                    },
                    ) { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            AndroidView(
                                factory = { context ->
                                    engine.createView(context).asView()
                                },
                                update = { context ->
                                    val engineView = context as EngineView
                                    engineView.render(session)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YamanekoTheme {
        Greeting("Android")
    }
}