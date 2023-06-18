package com.x256n.regexapplier.desktop.di

import com.x256n.regexapplier.desktop.common.DispatcherProvider
import com.x256n.regexapplier.desktop.common.StandardDispatcherProvider
import com.x256n.regexapplier.desktop.dialog.about.AboutViewModel
import com.x256n.regexapplier.desktop.screen.home.HomeViewModel
import com.x256n.regexapplier.desktop.config.ConfigManager
import com.x256n.regexapplier.desktop.dialog.regex.RegexViewModel
import com.x256n.regexapplier.desktop.dialog.settings.SettingsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object ModulesInjection {
    val viewmodelBeans = module {
        singleOf(::HomeViewModel)
        singleOf(::SettingsViewModel)
        singleOf(::RegexViewModel)
        factoryOf(::AboutViewModel)
    }
    val usecaseBeans = module {
//        singleOf(::SelectCategoryListUseCase)

    }
    val managerBeans = module {
        singleOf(::ConfigManager)
    }
    val otherBeans = module {
        factoryOf(::StandardDispatcherProvider) { bind<DispatcherProvider>() }
    }
}