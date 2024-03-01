package dev.nyanrus.yamaneko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import dev.nyanrus.yamaneko.ui.theme.YamanekoTheme
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.state.engine.EngineMiddleware
import mozilla.components.browser.state.helper.Target
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.compose.engine.WebContent
import mozilla.components.feature.search.middleware.SearchMiddleware
import mozilla.components.feature.search.region.RegionMiddleware
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.service.location.LocationService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val engine = GeckoEngine(applicationContext)

        val locationService by lazy { LocationService.default() }

        val nekoViewModel = NekoViewModel(BrowserStore(
            middleware = listOf(
                RegionMiddleware(applicationContext, locationService),
                SearchMiddleware(applicationContext),
            ) + EngineMiddleware.create(engine),
        ))

        //val nekoStore = makeNekoStore()

        val session = SessionUseCases(nekoViewModel.browserStore)
        session.loadUrl("google.com")

        setContent {
            YamanekoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        Scaffold(bottomBar = {
                            BottomBar(nekoViewModel,Target.SelectedTab, Modifier.height(80.dp))
                        },
                        ) { innerPadding ->
                            Box(Modifier.padding(innerPadding)) {
                                WebContent(engine = engine, store = nekoViewModel.browserStore, target = Target.SelectedTab)
                            }
                        }
                        SearchWindow(nekoViewModel,Target.SelectedTab)
                    }
                }
            }
        }
    }
}
@Composable
fun SearchWindow(nekoViewModel: NekoViewModel,target: Target) {
    val isEditing = nekoViewModel.isEditing.collectAsState()
    if (isEditing.value) {
        //https://searchfox.org/mozilla-mobile/rev/1dc5f1289afb580e32b66303a78ab5d92ce30ca5/firefox-android/android-components/components/compose/engine/src/main/java/mozilla/components/compose/engine/WebContent.kt#27
        val selectedTab = target.observeAsComposableStateFrom(nekoViewModel.browserStore, observe = { tab ->
            tab?.content?.url
        })
        val url = selectedTab.value!!.content.url
        var text by remember {
            mutableStateOf(url)
        }
        //https://stackoverflow.com/questions/59133100/how-to-close-the-virtual-keyboard-from-a-jetpack-compose-textfield
        val focusManager = LocalFocusManager.current
        val session = SessionUseCases(nekoViewModel.browserStore)

        val focusRequester = remember { FocusRequester() }
        val focused = remember { mutableStateOf(false) }

        TextField(value = text,
            modifier = Modifier.focusRequester(focusRequester).onFocusChanged {
                if (focused.value && !it.isFocused) {
                    nekoViewModel.setIsEditing(false)
                }
            },
            onValueChange = {text = it},
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                session.loadUrl(text)
                nekoViewModel.setIsEditing(false)
            })
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            focused.value = true
        }
    }
}

@Composable
fun BottomBar(nekoViewModel: NekoViewModel,target: Target,modifier: Modifier = Modifier) {
    BottomAppBar(modifier=modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            UrlBar(nekoViewModel,target)
            ReloadButton(nekoViewModel)
        }
    }
}


@Composable
fun UrlBar(nekoViewModel: NekoViewModel,target: Target) {
    //https://searchfox.org/mozilla-mobile/rev/1dc5f1289afb580e32b66303a78ab5d92ce30ca5/firefox-android/android-components/components/compose/engine/src/main/java/mozilla/components/compose/engine/WebContent.kt#27
    val selectedTab = target.observeAsComposableStateFrom(nekoViewModel.browserStore, observe = { tab ->
        tab?.content?.url
    })
    //もうちょっと後に持ってきたらhttpsみたいなURL得られるけどめんどい
    val text = {
        val url = selectedTab.value!!.content.url
        val list = url.split("//",limit=2)
        if (list.size < 2) {
            url
        } else {
            list[1]
        }
    }

    val tmp = text()
    val list = tmp.split("/")
    val list1 = list[0].split(".")
    val tmp1 = list1.slice(0..<list1.size-2).joinToString(".")+"."
    val tmp2 = list1.slice(list1.size-2..<list1.size).joinToString(".")
    ClickableText(
        buildAnnotatedString {
            //append(nekoStore.value.isEditing.toString())
            withStyle(style = SpanStyle(color = Color.LightGray)) {
                append(tmp1)
            }
            withStyle(style = SpanStyle(color = Color.White)) {
                append(tmp2)
            }
            withStyle(style = SpanStyle(color = Color.LightGray)) {
                append("/"+list.slice(1..<list.size).joinToString("/"))
            }
        },
        onClick = {
            nekoViewModel.setIsEditing(true)
            //nekoStore.value.isEditing = true
        },
        modifier = Modifier
            .background(Color.Gray)
            .width(300.dp)
            .padding(10.dp, 10.dp),
        overflow = TextOverflow.Clip,
        style = TextStyle(
            lineBreak = LineBreak.Heading
        ),
        maxLines = 1
    )
}

@Composable
fun ReloadButton(nekoViewModel: NekoViewModel) {
    val session = SessionUseCases(nekoViewModel.browserStore)
    TextButton(onClick = {session.reload()}) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload"
        )
    }
}