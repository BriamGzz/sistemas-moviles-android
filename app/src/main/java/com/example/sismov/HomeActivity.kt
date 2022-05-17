package com.example.sismov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import com.example.sismov.Clases.ActiveUser
import com.example.sismov.Clases.FragmentInterface
import com.example.sismov.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val transaction = supportFragmentManager.beginTransaction();

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentProfile = UserFragment();
        val fragmentHome = HomeFragment();
        val fragmentSearch = SearchFragment();
        val fragmentNewRest = NewRestaurantFragment();

        binding.btnHome.setOnClickListener {
            replaceFragment(fragmentHome);
        }

        binding.btnProfile.setOnClickListener {
            replaceFragment(fragmentProfile);
        }

        binding.btnSearch.setOnClickListener {
            replaceFragment(fragmentSearch);
        }

        binding.btnNewRestaurant.setOnClickListener {
            replaceFragment(fragmentNewRest);
        }

        if(!ActiveUser.getInstance().profileOnce) {
            ActiveUser.getInstance().profileOnce = true
            replaceFragment(fragmentProfile)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainerHome, fragment)
        fragmentTransaction.commit()
    }

}