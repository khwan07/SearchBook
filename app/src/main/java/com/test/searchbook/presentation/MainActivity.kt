package com.test.searchbook.presentation

import android.os.Bundle
import com.test.searchbook.databinding.ActivityMainBinding
import com.test.searchbook.presentation.search.SearchFragment

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // test
        replaceFragmentIfNotExists(
            supportFragmentManager,
            binding.fragmentArea.id,
            SearchFragment.TAG
        ) { SearchFragment.newInstance() }
    }
}