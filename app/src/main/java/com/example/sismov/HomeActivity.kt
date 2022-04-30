package com.example.sismov

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.Fragment
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

        binding.btnHome.setOnClickListener {
            replaceFragment(fragmentHome);
        }

        binding.btnProfile.setOnClickListener {
            replaceFragment(fragmentProfile);
        }

        binding.btnSearch.setOnClickListener {
            replaceFragment(fragmentSearch);
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerHome, fragment)
        fragmentTransaction.commit()
    }
}