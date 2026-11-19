package com.example.core.ext

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.example.core.app.App

inline fun <reified VM : ViewModel> ComponentActivity.appViewModels(): Lazy<VM> =
    ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = {
            (application as App).viewModelStore
        },
        factoryProducer = {
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        }
    )

inline fun <reified VM : ViewModel> Fragment.appViewModels(): Lazy<VM> =
    ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = {
            (requireActivity().application as App).viewModelStore
        },
        factoryProducer = {
            ViewModelProvider.AndroidViewModelFactory.getInstance(
                requireActivity().application
            )
        }
    )