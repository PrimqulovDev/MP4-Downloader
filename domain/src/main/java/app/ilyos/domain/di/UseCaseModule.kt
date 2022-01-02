package app.ilyos.domain.di

import app.repo.Repository
import app.usecase.GetAllVideosUseCase
import app.usecase.SaveVideoUseCase
import app.ilyos.domain.repo.RepositoryImpl
import app.ilyos.domain.usecase.GetAllVideosUseCaseImpl
import app.ilyos.domain.usecase.SaveVideoUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent


@Module
@InstallIn(ActivityRetainedComponent::class)
interface UseCaseModule {

    @Binds
    fun bindSaveVideoUseCase(binder: SaveVideoUseCaseImpl): SaveVideoUseCase

    @Binds
    fun bindGetAllVideosUseCase(binder: GetAllVideosUseCaseImpl): GetAllVideosUseCase

    @Binds
    fun bindRepo(binder: RepositoryImpl): Repository
}