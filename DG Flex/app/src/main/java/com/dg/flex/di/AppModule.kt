package com.dg.flex.di

import com.dg.flex.data.BackupRepository
import com.dg.flex.data.DatabaseBackupManager
import com.dg.flex.service.NotificationService
import com.dg.flex.data.PreferenceRepository
import com.dg.flex.data.Repository
import com.dg.flex.data.SearchesRepository
import com.dg.flex.data.V1PrefsMigration
import com.dg.flex.data.V2PrefsMigration
import com.dg.flex.data.db.WorkoutDatabase
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val PREFS_FILE = "app_prefs"


    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Singleton
    @Provides
    fun provideWorkoutPlanDatabase(
        @ApplicationContext app: Context,
        scope: CoroutineScope
    ): WorkoutDatabase = WorkoutDatabase.getInstance(app, scope)

    @Singleton
    @Provides
    fun provideRepository(
        db: WorkoutDatabase,
        @ApplicationContext context: Context
    ): Repository = Repository.getInstance(db, context)


    @Singleton
    @Provides
    fun provideSearchesRepository(@ApplicationContext context: Context) = SearchesRepository(context)

    @Singleton
    @Provides
    fun provideBackupRepository(backupManager: DatabaseBackupManager) = BackupRepository(
        backupManager
    )

    @Singleton
    @Provides
    fun provideBackupManager(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
        database: WorkoutDatabase
    ): DatabaseBackupManager = DatabaseBackupManager(context, dataStore, database)

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
//            corruptionHandler = ReplaceFileCorruptionHandler(
//                produceNewData = { emptyPreferences() }
//            ),
            // Optional: SharedPreferences migration
            migrations = listOf(V1PrefsMigration(context), V2PrefsMigration(context)),
            produceFile = { context.preferencesDataStoreFile(PREFS_FILE) }
        )
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(
        dataStore: DataStore<Preferences>,
        @ApplicationContext context: Context
    ): PreferenceRepository = PreferenceRepository(dataStore, context)

    @Provides
    fun provideCalculatorDao(db: WorkoutDatabase): com.dg.flex.data.db.dao.CalculatorDao = db.calculatorDao
}
