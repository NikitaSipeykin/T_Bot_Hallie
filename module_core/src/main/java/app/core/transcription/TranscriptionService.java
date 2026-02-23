package app.core.transcription;

import java.util.UUID;

public interface TranscriptionService {

  /*** Принимает команду и создаёт задачу в очереди.* @return ID созданной задачи*/
  UUID submit(TranscriptionCommand command);

  /*** Статус задачи по ID для отображения пользователю.*/
  String getStatusText(UUID jobId);
}
