package com.example.locationapp

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtil = LocationUtil(context)
    LocationDisplay(locationUtil = locationUtil, viewModel, context = context)

}

@Composable
fun LocationDisplay(
    locationUtil: LocationUtil,
    locationViewModel: LocationViewModel,
    context: Context
){
    val location = locationViewModel.location.value

    var address = location?.let{
        locationUtil.reverseGeocodeLocation(location)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            permissions->
            if((permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true)
                && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {

                locationUtil.requestLocationUpdate(viewModel = locationViewModel)

            } else {
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(
                                context as MainActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                if(rationalRequired) {
                    Toast.makeText(context, "Location permission is required for accessing the feature", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Location permission is not enabled. please enable this from setting", Toast.LENGTH_LONG).show()
                }
            }

        }
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        if(location != null) {
            Text(text = "Address is ${location.latitude}, ${location.longitude} \n $address")
        } else {
            Text(text = "Location is not available")
        }



        Button(onClick = {
            if(locationUtil.hasLocationPermission(context)){
                locationUtil.requestLocationUpdate(viewModel = locationViewModel)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }) {
            Text(text = "Home")
        }



    }
}
