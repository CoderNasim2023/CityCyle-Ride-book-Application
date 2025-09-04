package com.example.citycycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // Import for ContextCompat

public class DetailActivity extends AppCompatActivity {
    private ImageView imgBike, btnBack, favBtn; // Added favBtn
    private TextView tvName, tvType, tvPrice, tvAvailable, tvDescription;
    private Button btnRent;

    // Preference file name and key for favorites
    private static final String FAVORITES_PREFS = "favorites_prefs";
    private static final String FAVORITE_CYCLES_KEY = "favorite_cycles";

    private boolean isFavorite = false; // To track favorite status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        imgBike = findViewById(R.id.imgBike);
        btnBack = findViewById(R.id.btnBack);
        favBtn = findViewById(R.id.favBtn); // Initialize favBtn
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvPrice = findViewById(R.id.tvTotalPrice);
        tvAvailable = findViewById(R.id.tvAvailable);
        tvDescription = findViewById(R.id.tvDescription);
        btnRent = findViewById(R.id.btnRent);

        // Get data from intent
        Intent intent = getIntent();
        int cycleId = intent.getIntExtra("cycle_id", -1);
        String name = intent.getStringExtra("name");
        String type = intent.getStringExtra("type");
        double price = intent.getDoubleExtra("price", 0);
        int available = intent.getIntExtra("available", 0);
        String description = intent.getStringExtra("description");
        byte[] imageBytes = intent.getByteArrayExtra("image");

        if (cycleId == -1) {
            Toast.makeText(this, "Error: Cycle ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return; // Exit if no valid cycle ID
        }

        Log.d("DetailActivity", "Received Cycle ID: " + cycleId);

        // Set data to views
        tvName.setText(name);
        tvType.setText(type);
        tvPrice.setText(String.format("Rs. %.2f", price));
        tvAvailable.setText("Available: " + available);
        tvDescription.setText(description);

        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            if (bitmap != null) {
                imgBike.setImageBitmap(bitmap);
            } else {
                imgBike.setImageResource(R.drawable.loginimage); // Default if decoding fails
            }
        } else {
            imgBike.setImageResource(R.drawable.loginimage); // Default if no image bytes
        }

        // --- Favorite Button Logic ---
        // Check initial favorite status
        isFavorite = isCycleFavorite(cycleId);
        updateFavoriteIcon();

        favBtn.setOnClickListener(v -> {
            isFavorite = !isFavorite; // Toggle favorite status
            updateFavoriteIcon();
            if (isFavorite) {
                addCycleToFavorites(cycleId);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                removeCycleFromFavorites(cycleId);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Back Button Logic ---
        btnBack.setOnClickListener(v -> {
            // You can either go back to the previous activity on the stack
            // or explicitly navigate to AllCycleActivity.
            // Using finish() is generally preferred if you just want to go back
            // to the activity that launched this one.
            finish();

            // If you always want to go to AllCycleActivity regardless of
            // how DetailActivity was launched, use this:
            // Intent backIntent = new Intent(DetailActivity.this, AllCycleActivity.class);
            // startActivity(backIntent);
            // finish();
        });

        // Handle Rent button click
        btnRent.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);

            if (userId == -1) {
                Toast.makeText(this, "Error: User ID not found! Please log in.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent rentIntent = new Intent(DetailActivity.this, RentActivity.class);
            rentIntent.putExtra("cycle_id", cycleId);
            rentIntent.putExtra("name", name);
            rentIntent.putExtra("type", type);
            rentIntent.putExtra("price", price);
            rentIntent.putExtra("available", available);
            rentIntent.putExtra("description", description);
            rentIntent.putExtra("cycle_id", cycleId); // Redundant, already passed
            rentIntent.putExtra("user_id", userId);
            startActivity(rentIntent);
        });
    }

    private void updateFavoriteIcon() {
    }

    /**
     * Checks if a cycle is marked as favorite.
     *
     * @param cycleId The ID of the cycle to check.
     * @return true if the cycle is favorite, false otherwise.
     */
    private boolean isCycleFavorite(int cycleId) {
        SharedPreferences prefs = getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE);
        String favorites = prefs.getString(FAVORITE_CYCLES_KEY, "");
        // Simple check: does the string contain the cycleId (e.g., "1,5,10," for cycle ID 5)
        // A more robust solution for larger apps would use a Set or a JSON array.
        return favorites.contains("," + cycleId + ",");
    }

    /**
     * Adds a cycle ID to the favorites list in SharedPreferences.
     *
     * @param cycleId The ID of the cycle to add.
     */
    private void addCycleToFavorites(int cycleId) {
        SharedPreferences prefs = getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String currentFavorites = prefs.getString(FAVORITE_CYCLES_KEY, "");
        // Append with commas to avoid partial matches (e.g., ID 1 matches 10, 11)
        if (!currentFavorites.contains("," + cycleId + ",")) {
            editor.putString(FAVORITE_CYCLES_KEY, currentFavorites + cycleId + ",");
            editor.apply();
        }
    }

    /**
     * Removes a cycle ID from the favorites list in SharedPreferences.
     *
     * @param cycleId The ID of the cycle to remove.
     */
    private void removeCycleFromFavorites(int cycleId) {
        SharedPreferences prefs = getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String currentFavorites = prefs.getString(FAVORITE_CYCLES_KEY, "");
        // Replace the occurrence with an empty string
        String newFavorites = currentFavorites.replace("," + cycleId + ",", ",");
        // Remove leading/trailing commas if any after replacement
        newFavorites = newFavorites.replaceAll("^,+|,+$", ""); // Remove leading/trailing commas
        editor.putString(FAVORITE_CYCLES_KEY, newFavorites);
        editor.apply();
    }
}


//}