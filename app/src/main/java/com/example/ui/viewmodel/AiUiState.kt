package com.example.ui.viewmodel

import com.example.data.ai.GeneratedBookDraft

sealed interface AiDraftState {
    data object Idle : AiDraftState
    data object Loading : AiDraftState
    data class Success(val draft: GeneratedBookDraft) : AiDraftState
    data class Error(val message: String) : AiDraftState
}

sealed interface AiReaderState {
    data object Idle : AiReaderState
    data object Loading : AiReaderState
    data class Streaming(val text: String) : AiReaderState
    data class Success(val text: String) : AiReaderState
    data class Error(val message: String) : AiReaderState
}
