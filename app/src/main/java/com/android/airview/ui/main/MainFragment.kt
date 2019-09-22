package com.android.airview.ui.main

import android.Manifest
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.airview.R
import com.android.airview.controller.map.MapController
import com.android.airview.controller.map.MapControllerCallback
import com.android.airview.ui.base.BaseFragment
import com.android.airview.util.Util
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject


class MainFragment : BaseFragment(), MapControllerCallback {

    @Inject
    lateinit var mapController: MapController

    private lateinit var viewModel: MainViewModel

    private lateinit var mapFragment: SupportMapFragment

    // Needed permissions for location
    private var permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // To save rejected permissions
    private var permissionsRejected = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapController.setMapControllerCallback(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        startMap()
        observe()
        setClickListeners()
    }

    private fun startMap() {
        // Check all permissions granted
        if (Util.hasPermissions(context, permissions)) {
            if (Util.checkPlayServices(activity)) {

                // if all permissions are granted continue to process
                loading.show()
                mapFragment.getMapAsync(mapController)
            }
        } else {
            // if all permissions are not granted  ask for permissions
            Util.requestPermission(this, permissions)
        }
    }

    private fun observe() {

        viewModel.flights.observe(this, Observer {
            mapController.addPlaneMarkers(it)
        })

        //Observes loading dialog visibility
        viewModel.loading.observe(this, Observer {
            if (it) loading.show() else loading.dismiss()
        })

        viewModel.error.observe(this, Observer {
            //Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        // Observes filter
        viewModel.filter.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                txt_clear_filter.text = getString(R.string.label_filter, it)
            } else {
                txt_clear_filter.text = getString(R.string.label_clear_filter)
                mapController.getVisibleArea()?.apply {
                    getFlights(this)
                }
            }

            txt_clear_filter.visibility = View.VISIBLE
        })
    }

    private fun setClickListeners() {
        btn_search.setOnClickListener {
            loading.show()
            mapController.getVisibleArea()?.apply {
                getFlights(this)
            }
        }

        btn_focus.setOnClickListener {
            mapController.focusUserLocation()
        }

        txt_clear_filter.setOnClickListener {
            viewModel.clearFilter()
            loading.show()
            txt_clear_filter.visibility = View.GONE
        }
    }

    private fun getFlights(visibleBounds: LatLngBounds) {
        visibleBounds.apply {
            viewModel.getFlights(
                southwest.longitude,
                southwest.latitude,
                northeast.longitude,
                northeast.latitude
            )
        }
    }

    override fun searchForVisibleArea(visibleBounds: LatLngBounds) {
        getFlights(visibleBounds)
    }

    override fun onTitleClicked(title: String) {
        viewModel.filter(title)
    }

    override fun onCameraMoveStarted() {
        btn_search.visibility = View.GONE
    }

    override fun onCameraIdle() {
        btn_search.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // Check for are all permissions granted
                permissions.forEach {
                    if (!Util.hasPermission(activity, it)) {
                        permissionsRejected.add(it)
                    }
                }

                // If there is a denied permission explain why permission is necessary
                if (permissionsRejected.isNotEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            AlertDialog.Builder(activity)
                                .setMessage("Servisin çalışabilmesi için konum izinini vermeniz gerekmektedir.")
                                .setPositiveButton(
                                    "Tamam"
                                ) { _, _ ->

                                    // If clicks to "Tmama" ask for permission again
                                    Util.requestPermission(this, permissionsRejected)
                                }.setNegativeButton("İptal") { _, _ ->

                                    // If clicks "İptal" finish app
                                    activity?.finish()
                                }.create().show()

                            return
                        }
                    }
                } else {
                    // Continue to process if all permissions granted
                    startMap()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mapController.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
        const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        fun newInstance() = MainFragment()
    }
}
