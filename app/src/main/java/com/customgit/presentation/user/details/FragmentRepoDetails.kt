package com.customgit.presentation.user.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.customgit.core.data_classes.Repository
import com.customgit.databinding.FragmentRepositoryDetailsBinding

class FragmentRepoDetails: Fragment() {

    private lateinit var binding: FragmentRepositoryDetailsBinding
    private var repo: Repository? = null

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

        //карточка остается без изменений
        card.setOnClickListener {}
    }
}