package org.dianqk.ruslin.data

import kotlinx.coroutines.flow.SharedFlow
import uniffi.ruslin.*

interface NotesRepository {

    fun syncConfigExists(): Boolean

    suspend fun saveSyncConfig(config: SyncConfig): Result<Unit>

    suspend fun getSyncConfig(): Result<SyncConfig?>

    suspend fun sync(): Result<Unit>

    val isSyncing: SharedFlow<Boolean>
    val syncFinished: SharedFlow<Unit> // TODO: add sync result

    fun doSync(isOnStart: Boolean)

    fun newFolder(parentId: String?, title: String): FfiFolder

    suspend fun replaceFolder(folder: FfiFolder): Result<Unit>

    suspend fun loadFolders(): Result<List<FfiFolder>>

    suspend fun deleteFolder(id: String): Result<Unit>

    suspend fun loadAbbrNotes(parentId: String?): Result<List<FfiAbbrNote>>

    fun newNote(parentId: String?, title: String, body: String): FfiNote

    suspend fun loadNote(id: String): Result<FfiNote>

    suspend fun replaceNote(note: FfiNote): Result<Unit>

    suspend fun deleteNote(id: String): Result<Unit>

    suspend fun conflictNoteExists(): Result<Boolean>

    suspend fun loadAbbrConflictNotes(): Result<List<FfiAbbrNote>>

    suspend fun readLog(): String

    suspend fun readDatabaseStatus(): Result<FfiStatus>

    suspend fun search(searchTerm: String, enableHighlight: Boolean): Result<List<FfiSearchNote>>
}
