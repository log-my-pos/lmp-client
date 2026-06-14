package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.data.JournalEntryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FakeJournalRepositoryImpl : JournalRepository {
	override val allEntries: Flow<List<JournalEntry>> = emptyFlow()

	override suspend fun getEntryById(id: Long): JournalEntry? {
		return null
	}

	override suspend fun insert(entry: JournalEntry): Long {
		return 0L
	}

	override suspend fun update(entry: JournalEntry) {}

	override suspend fun delete(entry: JournalEntry) {}
}
