package com.jecsdev.papipuntos.data.db

import org.koin.core.module.Module

/**
 * Provides the platform-specific [androidx.room.RoomDatabase.Builder] for [AuthDatabase].
 * Android supplies an app [android.content.Context]; iOS resolves a documents-dir path.
 */
expect val platformAuthModule: Module
