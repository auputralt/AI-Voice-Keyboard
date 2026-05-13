package com.aivoicekeyboard.di

import android.content.Context
import androidx.room.Room
import com.aivoicekeyboard.api.OpenRouterClient
import com.aivoicekeyboard.data.db.AppDatabase
import com.aivoicekeyboard.data.db.AppTriggerDao
import com.aivoicekeyboard.data.db.CustomModeDao
import com.aivoicekeyboard.data.local.ApiKeyManager
import com.aivoicekeyboard.engine.SttEngine
import com.aivoicekeyboard.engine.TextProcessor
import com.aivoicekeyboard.util.ModelManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "ai_voice_keyboard.db")
            .addCallback(AppDatabase.SeedCallback())
            .build()
    }
    @Provides
    fun provideModeDao(database: AppDatabase): CustomModeDao = database.customModeDao()
    @Provides
    fun provideTriggerDao(database: AppDatabase): AppTriggerDao = database.appTriggerDao()
    @Provides
    @Singleton
    fun provideApiKeyManager(@ApplicationContext context: Context) = ApiKeyManager(context)
    @Provides
    @Singleton
    fun provideOpenRouterClient(apiKeyManager: ApiKeyManager) = OpenRouterClient(apiKeyManager)
    @Provides
    @Singleton
    fun provideModelManager(@ApplicationContext context: Context) = ModelManager(context)
    @Provides
    @Singleton
    fun provideTextProcessor(client: OpenRouterClient, modeDao: CustomModeDao) = TextProcessor(client, modeDao)
    @Provides
    @Singleton
    fun provideSttEngine(@ApplicationContext context: Context, modelManager: ModelManager) = SttEngine(context, modelManager)
}
