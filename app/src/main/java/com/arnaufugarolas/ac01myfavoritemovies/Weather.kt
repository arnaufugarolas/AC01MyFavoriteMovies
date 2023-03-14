package com.arnaufugarolas.ac01myfavoritemovies

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.arnaufugarolas.ac01myfavoritemovies.dataClass.WeatherResponse
import com.arnaufugarolas.ac01myfavoritemovies.databinding.ActivityWeatherBinding
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModel
import com.arnaufugarolas.ac01myfavoritemovies.ui.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class Weather : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        setupObservers()
        setupListeners()
        viewModel.getConfig()
        supportActionBar?.title = "Weather"
    }

    private fun setupListeners() {
        binding.IVWeatherSearch.setOnClickListener {
            viewModel.updateCity(binding.ETWeatherQuery.text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.weather.observe(this) {
            if (it != null) {
                setWeather(it)
            }
        }

        viewModel.weatherError.observe(this) {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setTextColor(getColor(R.color.white))
                    .setBackgroundTint(getColor(R.color.cornell_red))
                    .show()
            }
        }

        viewModel.weatherLoading.observe(this) {
            if (it != null) {
                binding.PBWeatherLoading.visibility =
                    if (it) android.view.View.VISIBLE else android.view.View.GONE
                binding.ETWeatherQuery.isEnabled = !it
            }
        }

        viewModel.config.observe(this) {
            if (it != null) {
                binding.ETWeatherQuery.setText(it.city)
                viewModel.getWeather(it.city!!)
            }
        }

        viewModel.configError.observe(this) {
            if (it != null) {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setTextColor(getColor(R.color.white))
                    .setBackgroundTint(getColor(R.color.cornell_red))
                    .show()
            }
        }

        viewModel.configLoading.observe(this) {
            if (it != null) {
                if (it) {
                    binding.ETWeatherQuery.isEnabled = false
                    binding.ETWeatherQuery.hint = "Loading..."
                } else {
                    binding.ETWeatherQuery.isEnabled = true
                    binding.ETWeatherQuery.hint = "Location"
                }
            }
        }
    }

    private fun setWeather(weather: WeatherResponse) {
        binding.TVWeatherLocationNameData.text = weather.location.name
        binding.TVWeatherDataRegion.text = weather.location.region
        binding.TVWeatherDataCountry.text = weather.location.country
        binding.TVWeatherDataLat.text = weather.location.lat.toString()
        binding.TVWeatherDataLon.text = weather.location.lon.toString()
        binding.TVWeatherDataTzId.text = weather.location.tzId
        binding.TVWeatherDataLocaltimeEpoch.text = weather.location.localtimeEpoch.toString()
        binding.TVWeatherDataLocaltime.text = weather.location.localtime
        binding.TVWeatherDataLastUpdatedEpoch.text = weather.current.lastUpdatedEpoch.toString()
        binding.TVWeatherDataLastUpdated.text = weather.current.lastUpdated
        binding.TVWeatherDataTempC.text = weather.current.tempC.toString()
        binding.TVWeatherDataTempF.text = weather.current.tempF.toString()
        binding.TVWeatherDataIsDay.text = weather.current.isDay.toString()
        binding.TVWeatherDataCondition.text = weather.current.condition?.text
        binding.TVWeatherDataWindMph.text = weather.current.windMph.toString()
        binding.TVWeatherDataWindKph.text = weather.current.windKph.toString()
        binding.TVWeatherDataWindDegree.text = weather.current.windDegree.toString()
        binding.TVWeatherDataWindDir.text = weather.current.windDir
        binding.TVWeatherDataPressureMb.text = weather.current.pressureMb.toString()
        binding.TVWeatherDataPressureIn.text = weather.current.pressureIn.toString()
        binding.TVWeatherDataPrecipMm.text = weather.current.precipMm.toString()
        binding.TVWeatherDataPrecipIn.text = weather.current.precipIn.toString()
        binding.TVWeatherDataHumidity.text = weather.current.humidity.toString()
        binding.TVWeatherDataCloud.text = weather.current.cloud.toString()
        binding.TVWeatherDataFeelsLikeC.text = weather.current.feelslikeC.toString()
        binding.TVWeatherDataFeelsLikeF.text = weather.current.feelslikeF.toString()
        binding.TVWeatherDataVisKm.text = weather.current.visKm.toString()
        binding.TVWeatherDataVisMiles.text = weather.current.visMiles.toString()
        binding.TVWeatherDataUV.text = weather.current.uv.toString()
        binding.TVWeatherDataGustMph.text = weather.current.gustMph.toString()
        binding.TVWeatherDataGustKph.text = weather.current.gustKph.toString()
        binding.TVWeatherDataAirQualityCo.text = weather.current.airQuality?.co.toString()
        binding.TVWeatherDataAirQualityNo2.text = weather.current.airQuality?.no2.toString()
        binding.TVWeatherDataAirQualityO3.text = weather.current.airQuality?.o3.toString()
        binding.TVWeatherDataAirQualitySo2.text = weather.current.airQuality?.so2.toString()
        binding.TVWeatherDataAirQualityPm25.text = weather.current.airQuality?.pm25.toString()
        binding.TVWeatherDataAirQualityPm10.text = weather.current.airQuality?.pm10.toString()
        binding.TVWeatherDataAirQualityUsEpaIndex.text =
            weather.current.airQuality?.usEpaIndex.toString()
        binding.TVWeatherDataAirQualityGbDefraIndex.text =
            weather.current.airQuality?.gbDefraIndex.toString()
    }
}
