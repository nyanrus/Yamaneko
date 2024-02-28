package dev.nyanrus.yamaneko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.nyanrus.yamaneko.ui.theme.YamanekoTheme
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.compose.engine.WebContent
import mozilla.components.feature.search.middleware.SearchMiddleware
import mozilla.components.feature.search.region.RegionMiddleware
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.service.location.LocationService
import mozilla.components.browser.state.helper.Target

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val builder = GeckoRuntimeSettings.Builder()
//        val runtimeSettings = builder
//            .aboutConfigEnabled(true)
//            .build()
        //val runtime = GeckoRuntime.create(applicationContext, runtimeSettings)

        val engine = GeckoEngine(applicationContext)

//        val session = engine.createSession(false)
//        session.loadUrl("qiita.com")

        val locationService by lazy { LocationService.default() }

        val store by lazy {
            BrowserStore(
                middleware = listOf(
                    RegionMiddleware(applicationContext, locationService),
                    SearchMiddleware(applicationContext),
                ) + EngineMiddleware.create(engine),
            )
        }

        val session = SessionUseCases(store)
        session.loadUrl("google.com")

        setContent {
            YamanekoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(bottomBar = {
                        BottomBar(store,Target.SelectedTab, Modifier.height(80.dp))
                    },
                    ) { innerPadding ->
                        Box(Modifier.padding(innerPadding)) {
                            WebContent(engine = engine, store = store, target = Target.SelectedTab)
//                            AndroidView(
//                                factory = { context ->
//                                    engine.createView(context).asView()
//                                },
//                                update = { context ->
//                                    val engineView = context as EngineView
//                                    engineView.render(session)
//                                }
//                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(store: BrowserStore,target: Target,modifier: Modifier = Modifier) {
    BottomAppBar(modifier=modifier) {
        Row {
            SearchBar(store,target)
            ReloadButton(store)
        }
    }
}


@Composable
fun SearchBar(store: BrowserStore,target: Target) {
    //https://searchfox.org/mozilla-mobile/rev/1dc5f1289afb580e32b66303a78ab5d92ce30ca5/firefox-android/android-components/components/compose/engine/src/main/java/mozilla/components/compose/engine/WebContent.kt#27
    val selectedTab = target.observeAsComposableStateFrom(store, observe = { tab ->
        tab?.content?.url
    })
    //もうちょっと後に持ってきたらhttpsみたいなURL得られるけどめんどい
    var text by remember { mutableStateOf(selectedTab.value!!.content.url) }

    //https://stackoverflow.com/questions/59133100/how-to-close-the-virtual-keyboard-from-a-jetpack-compose-textfield
    val focusManager = LocalFocusManager.current
    val session = SessionUseCases(store)
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            session.loadUrl(text)
            focusManager.clearFocus()
        }),
        maxLines = 1
    )
}

@Composable
fun ReloadButton(store: BrowserStore) {
    val session = SessionUseCases(store)
    TextButton(onClick = {session.reload()}) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload"
        )
    }
}