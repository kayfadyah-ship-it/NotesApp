package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDao()

    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE: Menyimpan title dan note
    fun addNote(title: String, note: String) {
        val cleanTitle = title.trim()
        val cleanNote = note.trim()
        if (cleanTitle.isEmpty() || cleanNote.isEmpty()) return
        viewModelScope.launch {
            dao.insert(Note(title = cleanTitle, note = cleanNote))
        }
    }

    // UPDATE: Memperbarui title dan note
    fun updateNote(existingNote: Note, newTitle: String, newNote: String) {
        val cleanTitle = newTitle.trim()
        val cleanNote = newNote.trim()
        if (cleanTitle.isEmpty() || cleanNote.isEmpty()) return
        viewModelScope.launch {
            dao.update(existingNote.copy(title = cleanTitle, note = cleanNote))
        }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }
}