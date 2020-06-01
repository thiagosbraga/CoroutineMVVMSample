package com.devtides.androidcoroutinesretrofit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devtides.androidcoroutinesretrofit.model.Country
import com.devtides.androidcoroutinesretrofit.model.CountryService
import kotlinx.coroutines.*
import retrofit2.Response

class ListViewModel: ViewModel() {

    private val countriesServices = CountryService.getCountriesService()

    var job: Job? = null

    private val exceptionHandle = CoroutineExceptionHandler { _, throwable ->
        onError("Ecxeption: ${throwable.localizedMessage}")

    }
    val countries = MutableLiveData<List<Country>>()
    val countryLoadError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        fetchCountries()
    }

    private fun fetchCountries() {
        loading.value = true

        job = CoroutineScope(Dispatchers.IO + exceptionHandle).launch {
            val response:Response<List<Country>> = countriesServices.getCountries()

            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    countries.value = response.body()
                    countryLoadError.value = null
                    loading.value = false
                } else{
                    onError("Error: ${response.message()}")
                }
            }
        }

    }

    private fun onError(message: String) {
        countryLoadError.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}