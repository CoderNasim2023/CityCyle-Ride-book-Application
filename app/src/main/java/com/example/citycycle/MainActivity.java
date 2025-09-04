package com.example.citycycle; // Ensure your package name matches

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.citycycle.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PERMISSION = 1;
    private ActivityMainBinding binding;
    private CycleAdapter adapter;
    private List<Cycle> cycleList;
    private DBHelper dbHelper;
    private Handler handler = new Handler();

//    Configuration cache is very important to runs the app more faster, takes less time to build an etc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DBHelper
        dbHelper = new DBHelper(this);

        // Restore availability for expired rentals (good logic)
        dbHelper.restoreAvailabilityForExpiredRentals();

        // Setup the Top Cycles RecyclerView
        setupTopCyclesRecyclerView();

        // Setup "Be in Touch" action buttons
        setupBeInTouchActions();

        // Setup "See All" click listeners
        setupSeeAllClickListeners();

        // Setup search functionality
        setupSearchBar();

        // Setup bottom navigation
        bottomNavigation();
    }

    private void setupTopCyclesRecyclerView() {
        binding.recyclerViewCycle.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));

        try {
            cycleList = dbHelper.getAllCycles();
            if (cycleList == null || cycleList.isEmpty()) {
                Toast.makeText(this, "No cycles available!", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Cycle list is empty or null.");
                // Optionally hide the RecyclerView or show an empty state message
            } else {
                adapter = new CycleAdapter(this, cycleList);
                binding.recyclerViewCycle.setAdapter(adapter);
                Log.d("MainActivity", "Cycles loaded: " + cycleList.size());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error fetching cycles: " + e.getMessage());
            Toast.makeText(this, "Error loading cycles.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBeInTouchActions() {
        // Website Button code
        binding.websiteBtn.setOnClickListener(v -> {
            String url = "https://www.github.com/CoderNasim2023"; // Replace with your actual website URL  https://www.github.com/CoderNasim2023
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No browser found to open the website.", Toast.LENGTH_SHORT).show();
            }
        });

        // Message Button (SMS)
        binding.messageBtn.setOnClickListener(v -> {
            String phoneNumber = "8371938543"; // Replace with your contact number
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + "+8371938543"));
            // Optional: Pre-fill message
            intent.putExtra("sms_body", "Hello, I'm interested to Book City Cycle services please reach out to me ");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No messaging app found.", Toast.LENGTH_SHORT).show();
            }
        });

        // Call Button
        binding.callBtn.setOnClickListener(v -> {
            makePhoneCall("8371938543"); // Replace with your contact number
        });

        // Direction Button (Google Maps)
        binding.directionBtn.setOnClickListener(v -> {
            // Replace with your desired location (latitude, longitude) or query 1st method:
            // Example for a specific address:
            String address = "1600 Amphitheatre Parkway, Mountain View, CA"; // This Address will show in directions button
            Uri gmmIntentUri = Uri.parse("https://maps.google.com/?q=" + Uri.encode(address));

            // Example for specific coordinates 2nd method:
            //Uri gmmIntentUri = Uri.parse("geo:37.422078,-122.084752(My University)");
                // Replace with actual latitude, longitude

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // Forces to open  Google Maps
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(MainActivity.this, "Google Maps app not found.", Toast.LENGTH_SHORT).show();
                // Fallback to web maps if Google Maps app is not installed
                Intent webMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + Uri.encode(address)));
                if (webMapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(webMapIntent);
                }
            }
        });

        // Share Button
        binding.shareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out City Cycle on   https://www.github.com/CoderNasim2023"); // Customize your share text
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    private void makePhoneCall(String phoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Permission already granted, make the call
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            if (callIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "No phone app found to make a call.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, retry the call
                Toast.makeText(this, "Call permission not  granted. Please try calling again.", Toast.LENGTH_SHORT).show();
                // You might want to store the number and call again, or prompt the user to re-tap
                // For simplicity, we'll just inform the user.
            } else {
                // Permission denied
                Toast.makeText(this, "Call permission denied. Cannot make calls.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSeeAllClickListeners() {
        binding.seeAllBeInTouch.setOnClickListener(v -> {
            // Logic for "See all" under "Be in Touch"
            Toast.makeText(MainActivity.this, " Coming Soon!| Not implemented yet ", Toast.LENGTH_SHORT).show();
            // You might open a new activity with more contact options or a detailed contact page
        });

        binding.seeAllTopCycles.setOnClickListener(v -> {
            // Logic for "See all" under "Top Cycles"
            // This usually means navigating to a page showing all cycles
            Intent intent = new Intent(MainActivity.this, AllCycleActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void setupSearchBar() {
        binding.etNameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for simple search
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the RecyclerView based on the search query
                filterCycles(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for simple search
            }
        });
    }

    private void filterCycles(String query) {
        List<Cycle> filteredList = new ArrayList<>();
        if (cycleList != null) {
            for (Cycle cycle : cycleList) {
                // Case-insensitive search on cycle name (or other relevant fields)
                if (cycle.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(cycle);
                }
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList); // You'll need to add this method to your CycleAdapter
        } else {
            Log.w("MainActivity", "Adapter is null, cannot filter cycles.");
        }
    }

    private void bottomNavigation() {
        ChipNavigationBar bottomNav = binding.bottomNavigation;
        bottomNav.setItemSelected(R.id.home, true); // Ensure 'home' is selected by default

        bottomNav.setOnItemSelectedListener(id -> {
            Log.d("BottomNav", "Selected ID: " + id);

            if (id == R.id.home) {
                // If home is selected, and we are already on MainActivity, do nothing.
                return;
            }

            Intent intent = null;
            if (id == R.id.all_cycle) {
                intent = new Intent(MainActivity.this, AllCycleActivity.class);
            } else if (id == R.id.activity) {
                intent = new Intent(MainActivity.this, ActivityHistory.class);
            } else if (id == R.id.profile) {
                intent = new Intent(MainActivity.this, ProfileActivity.class);
            } else {
                Log.e("BottomNav", "Unknown selection: " + id);
                return;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // Finish MainActivity only if you don't want it in the back stack when navigating to other main screens
                // Consider not finishing if users frequently navigate back to home.
                // For a typical app, you might only finish when exiting.
                // finish();
            }
        });
    }

    // Runnable for availability updates (good, keep this)
    private final Runnable availabilityUpdater = new Runnable() {
        @Override
        public void run() {
            dbHelper.restoreAvailabilityForExpiredRentals();
            // Re-fetch and update the adapter if needed
            try {
                List<Cycle> updatedCycleList = dbHelper.getAllCycles();
                if (adapter != null && updatedCycleList != null) {
                    // Assuming CycleAdapter has a method to update its data
                    adapter.updateData(updatedCycleList); // Implement this method in CycleAdapter
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error updating cycles from DB: " + e.getMessage());
            }
            handler.postDelayed(this, 60000); // 60 seconds
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Start the availability updater when activity is in foreground
        handler.post(availabilityUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the availability updater when activity goes to background
        handler.removeCallbacks(availabilityUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database helper to prevent memory leaks
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}