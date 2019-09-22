package com.android.airview


import com.android.airview.di.component.DaggerTestAppComponent
import com.android.airview.di.component.TestAppComponent
import com.android.airview.di.module.AppModule
import com.android.airview.di.module.TestRxJavaModule
import com.android.airview.di.util.ViewModelFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import java.io.File
import javax.inject.Inject
import kotlin.test.AfterTest

abstract class BaseTest {

    lateinit var mockServer: MockWebServer

    lateinit var appTestAppComponent: TestAppComponent

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Before
    open fun setUp() {
        this.configureMockServer()
        appTestAppComponent = DaggerTestAppComponent.builder()
            .appModule(AppModule(mockServer.url("/").toString()))
            .testRxJavaModule(TestRxJavaModule())
            .build()
        appTestAppComponent.inject(this)
    }

    @AfterTest
    open fun stop() {
        this.stopMockServer()
    }

    abstract fun isMockServerEnabled(): Boolean

    open fun configureMockServer() {
        if (isMockServerEnabled()) {
            mockServer = MockWebServer()
            mockServer.start()
        }
    }

    open fun stopMockServer() {
        if (isMockServerEnabled()) {
            mockServer.shutdown()
        }
    }

    open fun mockHttpResponse(fileName: String, responseCode: Int) = mockServer.enqueue(
        MockResponse()
            .setResponseCode(responseCode)
            .setBody(getJson(fileName))
    )

    private fun getJson(path: String): String {
        val uri = this.javaClass.classLoader?.getResource(path)
        val file = File(uri?.path)
        return String(file.readBytes())
    }
}