package com.customgit.presentation.user.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.customgit.R
import com.customgit.data.auth.app_auth.TokenStorage
import com.customgit.databinding.FragmentSearchBinding
import com.customgit.presentation.user.repositories.FragmentUserRepos

class FragmentSearch : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        checkUserName()
    }

    private fun setupViews() = with(binding) {

        Glide.with(requireContext())
            .load("https://www.dropbox.com/scl/fi/848qj8wu94igmfuyi6g08/1693928085956.jpg?rlkey=ml3tultpix1rfowktjald67hm&raw=1")
            .optionalCircleCrop()
            .into(gitLogo)

        //при нажатии на задний фон или крестик - фрагмент закроется
        clickInterceptor.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        //карточка остается без изменений
        card.setOnClickListener {}
    }

    private fun checkUserName() = with(binding) {

        val usernameFromUser = gitUsername
        val searchButton = search

        // обработчик для кнопки Search
        searchButton.setOnClickListener {
            progressBar.visibility - View.VISIBLE
            searchButton.visibility = View.INVISIBLE

            val enteredUsername = usernameFromUser.text.toString()

            when (enteredUsername.isEmpty()) {
                true -> Toast.makeText(requireContext(), "Enter name", Toast.LENGTH_SHORT).show()
                else -> {
                    TokenStorage.username = enteredUsername

                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, FragmentUserRepos())
                        .addToBackStack(null).commit()
                }
            }
            progressBar.visibility - View.VISIBLE
        }
    }

    companion object {
        fun newInstance3() = FragmentSearch()
    }
}