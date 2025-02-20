package com.example.midproject_imdb.ui.nearby_movies

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midproject_imdb.R
import com.example.midproject_imdb.core.MovieApplication
import com.example.midproject_imdb.ui.favorite_movies.MovieTMDBAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class NearbyMoviesFragment : Fragment() {
    private val viewModel: NearbyMoviesViewModel by viewModels()


    private lateinit var movieAdapter: MovieTMDBAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var locationText: TextView

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                getLocation()
            }
            else -> {
                // Handle permission denied
                locationText.text = getString(R.string.location_permission_denied)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.nearby_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        locationText = view.findViewById(R.id.locationText)

        setupNavigation(view)
        setupRecyclerView()
        setupObservers()
        checkLocationPermission()
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

        view?.findViewById<RecyclerView>(R.id.nearbyMoviesRecyclerView)?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.favoriteStatusUpdated.observe(viewLifecycleOwner) { (movieId, isFavorite) ->
            movieAdapter.notifyDataSetChanged()
        }

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            movieAdapter.setMovies(movies)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupNavigation(view: View) {
        view.findViewById<View>(R.id.searchButton).setOnClickListener {
            findNavController().navigate(R.id.action_nearby_to_search)
        }
        view.findViewById<View>(R.id.favoritesButton).setOnClickListener {
            findNavController().navigate(R.id.action_nearby_to_favorites)
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }
            else -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    val countryCode = addresses?.firstOrNull()?.countryCode

                    countryCode?.let { code ->
                        locationText.text =
                            getString(R.string.movies_in, addresses.first().countryName)
                        viewModel.fetchNearbyMovies(code)
                    }
                }
            }
        } catch (e: SecurityException) {
            locationText.text = getString(R.string.unable_to_access_location)
        }
    }
}