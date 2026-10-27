package gcd.testing.firstproject.collaborativetasks.data.mapper

import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask

internal object TaskDocumentMapper {
    fun from(documentId: String, fields: Map<String, Any?>): CollaborativeTask {
        val title = fields["titulo"] as? String
            ?: throw IllegalArgumentException("Task $documentId has no valid titulo field")
        val isCompleted = fields["completada"] as? Boolean
            ?: throw IllegalArgumentException("Task $documentId has no valid completada field")

        return CollaborativeTask(
            id = documentId,
            title = title,
            isCompleted = isCompleted,
        )
    }
}
