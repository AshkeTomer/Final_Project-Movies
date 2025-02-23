package com.example.midproject_imdb.ui.nearby_movies

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.midproject_imdb.R
import com.example.midproject_imdb.databinding.NearbyMoviesBinding
import com.example.midproject_imdb.ui.favorite_movies.MovieTMDBAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class NearbyMoviesFragment : Fragment() {
    private var _binding: NearbyMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NearbyMoviesViewModel by viewModels()
    private lateinit var movieAdapter: MovieTMDBAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    //handles the result of sending users to system settings.
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkLocationRequirements()
    }
    // Location permission launcher handles the result of permission requests.
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                checkLocationRequirements()
            }
            else -> {
                showPermissionDeniedDialog()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NearbyMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupUI()

        setupObservers()

        checkLocationRequirements()

    }
    //Configures the retry button for error scenarios.
    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            checkLocationRequirements()
        }
    }
    private fun setupUI() {
        setupNavigation()
        setupRecyclerView()
        setupRetryButton()
    }
    private fun setupNavigation() {
        binding.searchButton.setOnClickListener {
            findNavController().navigate(R.id.action_nearby_to_search)
        }
        binding.favoritesButton.setOnClickListener {
            findNavController().navigate(R.id.action_nearby_to_favorites)
        }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieTMDBAdapter(
            onFavoriteClick = { movie ->
                if (movie.isFavorite) {
                    viewModel.removeFromFavorites(movie)
                } else {
                    viewModel.addToFavorites(movie)
                }
            },
            isFavorite = { movieId ->
                viewModel.getFavoriteStatus(movieId)
            }
        )

        binding.nearbyMoviesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.locationState.observe(viewLifecycleOwner) { state ->
            handleLocationState(state)
        }

        viewModel.favoriteStatusUpdated.observe(viewLifecycleOwner) { _ ->
            movieAdapter.notifyDataSetChanged()
        }

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (viewModel.locationState.value is LocationState.LocationReady) {
                movieAdapter.setMovies(movies)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    //Manages UI state transitions based on location availability
    private fun handleLocationState(state: LocationState) {
        // First hide all containers
        binding.loadingIndicator.visibility = View.GONE
        binding.errorContainer.visibility = View.GONE
        binding.nearbyMoviesRecyclerView.visibility = View.GONE

        when (state) {
            is LocationState.Loading -> {
                binding.loadingIndicator.visibility = View.VISIBLE
            }
            is LocationState.PermissionDenied -> {
                binding.errorContainer.visibility = View.VISIBLE
                showError(
                    getString(R.string.location_permission_denied),
                    getString(R.string.grant_permission),
                    { requestLocationPermission() }
                )
            }
            is LocationState.GPSDisabled -> {
                binding.errorContainer.visibility = View.VISIBLE
                showError(
                    getString(R.string.gps_disabled_message),
                    getString(R.string.open_settings),
                    { openGPSSettings() }
                )
            }
            is LocationState.NoInternetConnection -> {
                binding.errorContainer.visibility = View.VISIBLE
                showError(
                    getString(R.string.no_internet_connection),
                    getString(R.string.retry),
                    { checkLocationRequirements() }
                )
            }
            is LocationState.LocationUnavailable -> {
                binding.errorContainer.visibility = View.VISIBLE
                showError(
                    getString(R.string.unable_to_access_location),
                    getString(R.string.retry),
                    { checkLocationRequirements() }
                )
            }
            is LocationState.LocationReady -> {
                binding.nearbyMoviesRecyclerView.visibility = View.VISIBLE
                binding.locationText.text = getString(R.string.movies_in, state.countryName)
            }
            is LocationState.Error -> {
                binding.errorContainer.visibility = View.VISIBLE
                showError(
                    state.message,
                    getString(R.string.retry),
                    { checkLocationRequirements() }
                )
            }
        }
    }
    //Verifies all requirements for location functionality including
    //  internet connectivity, permissions, and GPS status.
    private fun checkLocationRequirements() {
        viewModel.setLocationState(LocationState.Loading)

        if (!checkInternetConnection()) {
            viewModel.setLocationState(LocationState.NoInternetConnection)
            return
        }

        when {
            !hasLocationPermission() -> {
                viewModel.setLocationState(LocationState.PermissionDenied)
            }
            !isGPSEnabled() -> {
                viewModel.setLocationState(LocationState.GPSDisabled)
            }
            else -> {
                getLocation()
            }
        }
    }




    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_denied_title))
            .setMessage(getString(R.string.location_permission_denied_message))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                })
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
            }
            .show()
    }
 //Retrieves the current location and updates the UI accordingly.
    private fun getLocation() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location == null) {
                        viewModel.setLocationState(LocationState.LocationUnavailable)
                        return@addOnSuccessListener
                    }

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val countryCode = addresses?.firstOrNull()?.countryCode
                    val countryName = addresses?.firstOrNull()?.countryName

                    if (countryCode != null && countryName != null) {
                        viewModel.setLocationState(LocationState.LocationReady(countryCode, countryName))
                    } else {
                        viewModel.setLocationState(LocationState.LocationUnavailable)
                    }
                }
                .addOnFailureListener { e ->
                    viewModel.setLocationState(LocationState.Error(e.message ?: "Location error"))
                }
        } catch (e: SecurityException) {
            viewModel.setLocationState(LocationState.Error(e.message ?: "Security error"))
        }
    }
    private fun showError(message: String, buttonText: String, buttonAction: () -> Unit) {
        binding.errorMessage.text = message
        binding.retryButton.text = buttonText
        binding.retryButton.setOnClickListener { buttonAction() }
    }
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun requestLocationPermission() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_title))
            .setMessage(getString(R.string.location_permission_message))
            .setPositiveButton(getString(R.string.continue_button)) { _, _ ->
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .apply {
                // Prevent dialog from being dismissed when clicking outside
                setCanceledOnTouchOutside(false)
            }
            .show()
    }

    private fun openGPSSettings() {
        settingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun onResume() {
        super.onResume()
        checkLocationRequirements()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}