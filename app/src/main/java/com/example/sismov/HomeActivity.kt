package com.example.sismov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import com.example.sismov.Clases.ActiveUser
import com.example.sismov.Clases.FragmentInterface
import com.example.sismov.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentProfile = UserFragment();
        val fragmentHome = HomeFragment();
        val fragmentSearch = SearchFragment();
        val fragmentNewRest = NewRestaurantFragment();

        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnProfile = findViewById<ImageButton>(R.id.btnProfile)
        val btnSearch = findViewById<ImageButton>(R.id.btnSearch)
        val btnNewRestaurant = findViewById<ImageButton>(R.id.btnNewRestaurant)

        btnHome.setOnClickListener {
            replaceFragment(fragmentHome);
        }

        btnProfile.setOnClickListener {
            replaceFragment(fragmentProfile);
        }

        btnSearch.setOnClickListener {
            replaceFragment(fragmentSearch);
        }

        btnNewRestaurant.setOnClickListener {
            replaceFragment(fragmentNewRest);
        }

        /*if(!ActiveUser.getInstance().profileOnce) {
            replaceFragment(fragmentProfile)
            ActiveUser.getInstance().profileOnce = true
        }*/

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainerHome, fragment)
        fragmentTransaction.commit()
    }

}