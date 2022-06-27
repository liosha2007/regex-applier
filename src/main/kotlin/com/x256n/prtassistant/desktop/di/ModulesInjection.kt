package com.x256n.prtassistant.desktop.di

import com.x256n.prtassistant.desktop.screen.home.HomeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object ModulesInjection {
    val viewmodelBeans = module {
        singleOf(::HomeViewModel)
//        singleOf(::CategoryListViewModel)
//        singleOf(::LessonListViewModel)
//        singleOf(::PartspeechListViewModel)
//        singleOf(::WordListViewModel)
    }
    val usecaseBeans = module {
//        singleOf(::SelectCategoryListUseCase)

    }
    val daoBeans = module {
//        singleOf(::CategoryDaoImpl) { bind<CategoryDao>() }
//        singleOf(::LessonDaoImpl) { bind<LessonDao>() }
//        singleOf(::PartspeechDaoImpl) { bind<PartspeechDao>() }
//        singleOf(::WordDaoImpl) { bind<WordDao>() }
    }
}