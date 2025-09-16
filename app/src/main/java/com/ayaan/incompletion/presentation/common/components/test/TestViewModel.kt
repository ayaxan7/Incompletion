package com.ayaan.incompletion.presentation.common.components.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ayaan.incompletion.data.repository.NearestBusStopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TestResult {
    object Loading : TestResult()
    data class Success(val data: String) : TestResult()
    data class Error(val message: String) : TestResult()
}

@HiltViewModel
class TestViewModel @Inject constructor(
    private val nearestBusStopRepository: NearestBusStopRepository
) : ViewModel() {

    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult.asStateFlow()

    fun executeTest() {
        viewModelScope.launch {
            _testResult.value = TestResult.Loading
            try {
                val response = nearestBusStopRepository.test()
                if (response.isSuccessful) {
                    val data = response.body() ?: "Empty response"
                    _testResult.value = TestResult.Success(data)
                } else {
                    _testResult.value = TestResult.Error("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _testResult.value = TestResult.Error("Network Error: ${e.localizedMessage}")
            }
        }
    }

    fun clearTestResult() {
        _testResult.value = null
    }
}
