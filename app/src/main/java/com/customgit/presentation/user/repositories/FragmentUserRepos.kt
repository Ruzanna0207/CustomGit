package com.customgit.presentation.user.repositories

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.customgit.R
import com.customgit.adapters.Clickable
import com.customgit.adapters.RepositoriesAdapter
import com.customgit.core.data_classes.Repository
import com.customgit.core.utils.launchAndCollectIn
import com.customgit.databinding.FragmentUserReposBinding
import com.customgit.presentation.auth.FragmentAuth
import com.customgit.presentation.user.details.FragmentRepoDetails
import com.customgit.presentation.user.details.FragmentUserDetails
import com.customgit.presentation.user.search.FragmentSearch
import com.customgit.viewModels.user_info.UserInfoViewModel

class FragmentUserRepos : Fragment(), Clickable {

    private lateinit var binding: FragmentUserReposBinding
    private val viewModel: UserInfoViewModel by activityViewModels()

    //обработка результатов логаута
    private val logoutResponse = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.webLogoutComplete()
        } else {
            viewModel.webLogoutComplete()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserReposBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInfo()
        aboutUser()
        aboutRepos()
        clickToSeeDetailsAboutUser()
    }

    override fun onResume() {
        super.onResume()
        logOut()
        openSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentUser.removeObservers(viewLifecycleOwner)
        viewModel.currentRepos.removeObservers(viewLifecycleOwner)
    }

    companion object {
        fun newInstance2() = FragmentUserRepos()
    }

    //выполн-е функций во вью модели для получ-я инфы
    private fun getInfo() = with(viewModel) {
        getUser()
        getAllRepos()
    }

    //получ-е инфы о пользователе
    private fun aboutUser() = with(binding) {
        progressBar.visibility = View.VISIBLE

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->

            Glide.with(requireContext())
                .load(user.avatarUrl)
                .circleCrop()
                .into(image)

            userName.text = user.name
            userLocation.text = user.location

            progressBar.visibility = View.GONE
        }
        //при ошибке польз-ль получит сообщение
        viewModel.errorUser.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

        //получ-е списка репозиториев
        private fun aboutRepos() = with(binding) {
            progressBar.visibility - View.VISIBLE

            val adapter = RepositoriesAdapter(this@FragmentUserRepos)
            repositoriesRecView.adapter = adapter

            viewModel.currentRepos.observe(viewLifecycleOwner) { repos ->
                adapter.repos = repos
                adapter.notifyDataSetChanged()
            }
            progressBar.visibility - View.GONE
        }

        //открытие фрагмента для просмотра инф-ии о пользователе при нажатии на аватар
        private fun clickToSeeDetailsAboutUser() {
            binding.image.setOnClickListener {
                viewModel.currentUser.observe(viewLifecycleOwner) { user ->
                    val secondFragment = FragmentUserDetails()
                    secondFragment.arguments = bundleOf("user" to user)

                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.frame, secondFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        //открытие фрагмента для просмотра инф-ии о репозитории
        override fun clickToSeeDetailsRepo(repo: Repository) {
            val secondFragment = FragmentRepoDetails()
            secondFragment.arguments = bundleOf("repo" to repo)

            requireActivity().supportFragmentManager.beginTransaction()
                .add(R.id.frame, secondFragment)
                .addToBackStack(null)
                .commit()
        }

        //открыть поиск других пользователей
        private fun openSearch() {
            binding.search.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.frame, FragmentSearch.newInstance3())
                    .addToBackStack(null)
                    .commit()
            }
        }

        //фун-я для логаута
        private fun logOut() = with(viewModel) {
            binding.exit.setOnClickListener {
                logout()
            }
            logoutPageFlow.launchAndCollectIn(viewLifecycleOwner) {
                logoutResponse.launch(it)
            }

            logoutCompletedFlow.launchAndCollectIn(viewLifecycleOwner) {
                requireActivity().supportFragmentManager
                    .beginTransaction().replace(R.id.frame, FragmentAuth()).commit()
            }
        }
    }

