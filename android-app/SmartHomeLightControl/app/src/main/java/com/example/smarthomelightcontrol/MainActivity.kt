package com.example.smarthomelightcontrol

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.call.body
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //TO MAKE SURE APP IS UNDER SYSTEM BARS BY DEFAULT
        setContent { SmartHomeScreen() }
    }
}

@Composable
fun SmartHomeScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var isConnected by remember { mutableStateOf(isNetworkAvailable(ctx)) }

    //BUTTON STATES
    var livingOn by remember { mutableStateOf(false) }
    var bathroomOn by remember { mutableStateOf(false) }
    var bedroomOn by remember { mutableStateOf(false) }
    var laundryOn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        //FOR GETTING ROOM STATUS AS SOON AS THE APP IS OPENED
        livingOn = getLightStatus(ctx, "LivingRoom")
        bathroomOn = getLightStatus(ctx, "Bathroom")
        bedroomOn = getLightStatus(ctx, "Bedroom")
        laundryOn = getLightStatus(ctx, "LaundryRoom")
    }

    LaunchedEffect(Unit) {
        while (true) {
            //CONTINUOUSLY CHECK FOR WIFI CONNECTIVITY
            isConnected = isNetworkAvailable(ctx)
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 40.dp, vertical = 20.dp)
            //horizontal for left-right; vertical for top-bottom
            //.background(Color(0xFFF5F6F8))

    ) {
        if (!isConnected) {
            //IF THERE IS NO INTERNET CONNECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_wifi_off),
                    contentDescription = "No Connection",
                    tint = Color.Gray,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "No connection",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )

                Spacer(Modifier.height(35.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "My Home",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F),
                        modifier = Modifier.padding(bottom = 15.dp)
                    )
                    Text(
                        "All Devices",
                        fontSize = 20.sp,
                        color = Color(0xFF9AA0A6),
                        modifier = Modifier.padding(bottom = 35.dp)
                    )
                }
                Spacer(Modifier.height(35.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ufo_no_connection),
                        contentDescription = "UFO Image",
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "Home is on a UFO!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom=3.dp, top=3.dp)
                    )
                    Text(
                        text = "Cannot control devices\nremotely.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } //IF THERE IS INTERNET CONNECTION
        else{
            Text("My Home", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            WelcomeHeader(
                name = "Belen Ladesma",
                modifier = Modifier.offset(y = (-12).dp) //REDUCING SPACE BETWEEN WELCOME BANNER AND 'MY HOME' TEXT
            )
            Text("All Devices", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9AA0A6), modifier = Modifier.padding(bottom = 35.dp))

            //BUTTON DESIGNS WHICH IS BY DISPLAYED BY ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //LIVING ROOM'S LIGHT
                LightButton(
                    label = "Living Room",
                    isOn = livingOn,
                    onToggle = {
                        scope.launch {
                            setLight(ctx, "LivingRoom", !livingOn)
                            livingOn = getLightStatus(ctx, "LivingRoom")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                //BATHROOM'S LIGHT
                LightButton(
                    label = "Bathroom",
                    isOn = bathroomOn,
                    onToggle = {
                        scope.launch {
                            setLight(ctx, "Bathroom", !bathroomOn)
                            bathroomOn = getLightStatus(ctx, "Bathroom")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            //BEDROOM'S LIGHT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LightButton(
                    label = "Bedroom",
                    isOn = bedroomOn,
                    onToggle = {
                        scope.launch {
                            setLight(ctx, "Bedroom", !bedroomOn)
                            bedroomOn = getLightStatus(ctx, "Bedroom")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                //LAUNDRY ROOM'S LIGHT
                LightButton(
                    label = "Laundry Room",
                    isOn = laundryOn,
                    onToggle = {
                        scope.launch {
                            setLight(ctx, "LaundryRoom", !laundryOn)
                            laundryOn = getLightStatus(ctx, "LaundryRoom")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                //Spacer(Modifier.weight(1f))
            }
        }

    }
}


//LIGHT BUTTONS
@Composable
fun LightButton(
    label: String,
    isOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // animated background color depending on ON/OFF state
    val bg by animateColorAsState(
        targetValue = if (isOn) Color(0xFF3E99F4) else Color(0xFFE8E8E8),
        label = "lightBg"
    )

    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(bg)
            .clickable { onToggle() }        // tap anywhere on the card to toggle
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isOn) Color.White else Color(0xFF1F1F1F)
                )
                Text(
                    text = if (isOn) "On" else "Off",
                    fontSize = 14.sp,
                    color = if (isOn) Color(0xFFE0F2FF) else Color(0xFF616161)
                )
            }

            // the actual toggle switch on the right
            Switch(
                checked = isOn,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2E7DFF),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFB0BEC5)
                )
            )
        }
    }
}




//DESIGN HEADER NAME
@Composable
fun WelcomeHeader(
    modifier: Modifier = Modifier,
    name: String = "Human" //DEFAULT
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp) //IMAGE ADJUSTMENTS
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFFFF)) //BG COLOR OF PNG FILE
    ) {
        Image(
            painter = painterResource(id = R.drawable.name_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 40.dp, top = 85.dp)
        ) {
            Text("Welcome Home", fontSize = 21.sp, color = Color(0xFF1F1F1F))
            Text(name, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F1F1F))
        }
    }
}


//UPDATE LOCAL HOST HERE!!! VAUGHN & CHARLES
//DATABASE
//UPDATE STATUS FIELD
suspend fun setLight(context: Context, room: String, isOn: Boolean) {
    //REPLACE YOUR LAN IP ADDRESS
    val ipAddress = "192.168.5.108"
    val folderUrl = "http://$ipAddress/IT155/setLightStatus.php"
    val state = if (isOn) '1' else '0'
    val client = HttpClient(CIO)
    try {
        // Send request with room and status
        val resp: HttpResponse = client.get("$folderUrl?room=$room&status=$state")
        //val resp: HttpResponse = client.get("$folderUrl?room=Living%20Room&status=1")
        val body: String = resp.body()

        // Show server response in a Toast
        context.toast(body.ifBlank { "OK" })
    } catch (e: Exception) {
        context.toast("Error: ${e.message}")
    } finally {
        client.close()
    }
}
//RETRIEVE CONTENT FROM STATUS FIELD
suspend fun getLightStatus(context: Context, room: String): Boolean {
    val ipAddress = "192.168.5.108"
    val folderUrl = "http://$ipAddress/IT155/getLightStatus.php"
    val client = HttpClient(CIO)
    return try {
        val resp: HttpResponse = client.get("$folderUrl?room=$room")
        val body: String = resp.body()
        body.trim() == "1" //true if ON, false if OFF
    } catch (e: Exception) {
        context.toast("Error: ${e.message}")
        false
    } finally {
        client.close()
    }
}


fun Context.toast(message: CharSequence) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


//DETERMINES IF THERE IS INTERNET CONNECTION SO THAT
//NO CONNECTION UI WILL DISPLAY :)
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}