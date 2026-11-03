package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.auth.AuthUser
import com.example.data.model.Book
import com.example.ui.viewmodel.AiDraftState
import com.example.util.AppTimezone

@Composable
fun AddEditBookDialog(
    initialBook: Book? = null,
    authUser: AuthUser? = null,
    isSaving: Boolean = false,
    aiDraftState: AiDraftState = AiDraftState.Idle,
    timezone: AppTimezone = AppTimezone.ALGIERS,
    onOpenTimezoneSelector: () -> Unit = {},
    onGenerateWithAi: (String) -> Unit = {},
    onResetAiDraftState: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 740.dp)
                .testTag("add_edit_book_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                AuthorBookUploadForm(
                    initialBook = initialBook,
                    authUser = authUser,
                    isSaving = isSaving,
                    aiDraftState = aiDraftState,
                    timezone = timezone,
                    onOpenTimezoneSelector = onOpenTimezoneSelector,
                    onGenerateWithAi = onGenerateWithAi,
                    onResetAiDraftState = onResetAiDraftState,
                    onOpenAuth = onOpenAuth,
                    onDismiss = onDismiss,
                    onSaveBookToFirestore = onSave
                )
            }
        }
    }
}
