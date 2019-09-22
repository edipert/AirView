package com.android.airview

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.android.airview.api.model.State
import com.android.airview.ui.main.MainViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.net.HttpURLConnection
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class MainViewModelUnitTest : BaseTest() {

    override fun isMockServerEnabled() = true

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var activity: FragmentActivity

    @Before
    override fun setUp() {
        super.setUp()
        this.activity = Robolectric.setupActivity(FragmentActivity::class.java)
        this.mainViewModel =
            ViewModelProviders.of(this.activity, viewModelFactory)[MainViewModel::class.java]
    }

    @Test
    fun getFlights_whenSuccess() {
        // Prepare data
        this.mockHttpResponse("getFlights_whenSuccess.json", HttpURLConnection.HTTP_OK)
        // Pre-test
        assertEquals(
            null,
            this.mainViewModel.flights.value,
            "Flight list should be null because stream not started yet"
        )
        // Execute View Model
        this.mainViewModel.getFlights(lomin, lamin, lomax, lamax)

        // Checks
        assertEquals(
            EXPECTED_DATA[0],
            this.mainViewModel.flights.value?.get(0),
            "Flight state must be fetched"
        )

        assertEquals(
            false,
            this.mainViewModel.loading.value,
            "Should be reset to 'false' because stream ended"
        )

        assertEquals(null, this.mainViewModel.error.value, "No error must be founded")
    }

    @Test
    fun getFlights_whenError() {
        // Prepare data
        this.mockHttpResponse("getFlights_whenSuccess.json", HttpURLConnection.HTTP_BAD_GATEWAY)
        // Pre-test
        assertEquals(
            null,
            this.mainViewModel.flights.value,
            "Flight list should be null because stream not started yet"
        )
        // Execute View Model
        this.mainViewModel.getFlights(lomin, lamin, lomax, lamax)
        // Checks
        assertEquals(null, this.mainViewModel.flights.value?.get(0), "Flight must be null because of http error")
        assertEquals(
            false,
            this.mainViewModel.loading.value,
            "Should be reset to 'false' because stream ended"
        )
        assertNotEquals(
            null,
            this.mainViewModel.error.value,
            "Error value must not be empty"
        )
    }

    companion object {
        const val lomin = 27.344531648
        const val lamin = 40.226013967
        const val lomax = 30.7411966586
        const val lamax = 41.6004635693


        private val EXPECTED_DATA = listOf(
            State(
                "3991e0",
                "AFR1390 ",
                "France",
                1569110188,
                1569110189,
                28.73,
                41.1519,
                655.32f,
                false,
                87.98f,
                358.99f,
                -4.23f,
                null,
                754.38f,
                "2110",
                false,
                0
            )
        )
    }

}
