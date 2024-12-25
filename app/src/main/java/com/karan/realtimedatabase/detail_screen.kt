package com.karan.realtimedatabase

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.navDeepLink
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.karan.realtimedatabase.databinding.FragmentDetailScreenBinding
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [detail_screen.newInstance] factory method to
 * create an instance of this fragment.
 */
class detail_screen : Fragment() {
    lateinit var binding: FragmentDetailScreenBinding

    // TODO: Rename and change types of parameters

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var Location_Permission_Request_Code = 1000
    var pgbar: ProgressBar? = null
    private var name: String? = null
    private var ClassEt: String? = null
    private var number: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailScreenBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name") ?: ""
            ClassEt = it.getString("class") ?: ""
            number = it.getString("number") ?: ""
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.progreassBar
        if (checkPermission()) {
            getLastLocation()
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), Location_Permission_Request_Code
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Location_Permission_Request_Code -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location Permission Denied",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun getLastLocation() {
        binding.progreassBar?.visibility = View.VISIBLE
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            binding.progreassBar?.visibility = View.GONE
            if (location != null) {
                var userLong = location.longitude
                var userLat = location.latitude
                var address = getCompleteAddressString(userLong, userLat)
                binding.Location?.setText(address)
                binding.Latitude?.setText(userLat.toString())
                binding.Longitude?.setText(userLong.toString())
            }
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressString = address.getAddressLine(0)
                val placeIndex = addressString.indexOf("")
                if (placeIndex != -1) {
                    return addressString.substring(placeIndex+1)

                }
                else{
                    return addressString
                }
            }
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
        return "No address found"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailName.setText(name)
        binding.detailClass.setText(ClassEt)
        binding.detailNumber.setText(number)


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment detail_screen.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            detail_screen().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}