package com.customgit.presentation.user.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.customgit.R
import com.customgit.core.data_classes.ReadmeResponse
import com.customgit.core.data_classes.Repository
import com.customgit.databinding.FragmentRepositoryDetailsBinding
import com.customgit.presentation.readme.FragmentReadme
import com.customgit.viewModels.user_info.UserInfoViewModel


class FragmentRepoDetails : Fragment() {

    private lateinit var binding: FragmentRepositoryDetailsBinding
    private var repo: Repository? = null
    private var readme: ReadmeResponse? = null
    private val viewModel: UserInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRepositoryDetailsBinding.inflate(layoutInflater)
        arguments?.let {
            repo = it.getParcelable("repo") // Получаем репоз-й из аргументов
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        addReadme()
        readmeVisability()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentReadme.removeObservers(viewLifecycleOwner)
    }

    private fun setupViews() = with(binding) {

        Glide.with(requireContext())
            .load(repo?.owner?.avatarUrl)
            .circleCrop()
            .into(image)

        nameRepo.text = repo?.fullName
        descriptionRepo.text = repo?.description
        langRepo.text = repo?.language
        owner.text = repo?.owner?.login

        //при нажатии на задний фон или крестик - фрагмент закроется
        clickInterceptor.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        //получение readme
        binding.readme.setOnClickListener {
            getInfoToReadme()
        }
    }

    //передача инфы для открытия фраг-та readme
    private fun getInfoToReadme() {
        val secondFragment = FragmentReadme()
        secondFragment.arguments = bundleOf("repo" to repo?.name)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame, secondFragment)
            .addToBackStack(null)
            .commit()
    }

    //видимость кнопки readme
    private fun readmeVisability() {
        viewModel.errorUser.observe(viewLifecycleOwner) {
            if (it == "Readme not found") {
                binding.readme.visibility = View.INVISIBLE
            }
        }
    }

    //получ-е инфы о readme
    private fun addReadme() {
        viewModel.getReadmeForRepository(repo?.name ?: "")
        viewModel.currentReadme.observe(viewLifecycleOwner) {
            readme = it
        }
    }
}
