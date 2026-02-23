let selectedFile = null;
let currentJobId = null;
let statusPollInterval = null;

// ===== Drag & Drop =====

const dropZone = document.getElementById("dropZone");

dropZone.addEventListener("dragover", e => {
    e.preventDefault();
    dropZone.classList.add("drag-over");
});

dropZone.addEventListener("dragleave", () => {
    dropZone.classList.remove("drag-over");
});

dropZone.addEventListener("drop", e => {
    e.preventDefault();
    dropZone.classList.remove("drag-over");
    const file = e.dataTransfer.files[0];
    if (file) setFile(file);
});

document.getElementById("fileInput").addEventListener("change", e => {
    if (e.target.files[0]) setFile(e.target.files[0]);
});

function setFile(file) {
    selectedFile = file;
    document.getElementById("fileName").textContent = "📁 " + file.name + " (" + formatSize(file.size) + ")";
    document.getElementById("uploadBtn").disabled = false;
    showStatus("", "");
}

// ===== Upload =====

async function uploadFile() {
    if (!selectedFile) return;

    const btn = document.getElementById("uploadBtn");
    btn.disabled = true;

    const progressWrap = document.getElementById("progressWrap");
    const progressBar = document.getElementById("progressBar");
    const progressText = document.getElementById("progressText");

    progressWrap.style.display = "block";
    progressBar.value = 0;

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
        // XHR для отображения прогресса загрузки
        const result = await uploadWithProgress(formData, percent => {
            progressBar.value = percent;
            progressText.textContent = percent < 100
                ? `Загрузка: ${percent}%`
                : "Файл загружен, обрабатывается...";
        });

        currentJobId = result.jobId;
        showStatus("✅ " + result.message, "success");

        // Начинаем polling статуса
        startStatusPolling(currentJobId);

        // Сбрасываем форму
        selectedFile = null;
        document.getElementById("fileInput").value = "";
        document.getElementById("fileName").textContent = "";

        loadHistory();

    } catch (e) {
        showStatus("❌ Ошибка: " + e.message, "error");
        btn.disabled = false;
    } finally {
        setTimeout(() => progressWrap.style.display = "none", 2000);
    }
}

function uploadWithProgress(formData, onProgress) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();

        xhr.upload.addEventListener("progress", e => {
            if (e.lengthComputable) {
                onProgress(Math.round(e.loaded / e.total * 100));
            }
        });

        xhr.addEventListener("load", () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(JSON.parse(xhr.responseText));
            } else {
                const err = JSON.parse(xhr.responseText || "{}");
                reject(new Error(err.error || "HTTP " + xhr.status));
            }
        });

        xhr.addEventListener("error", () => reject(new Error("Ошибка сети")));

        xhr.open("POST", "/api/transcription/upload");
        xhr.send(formData);
    });
}

// ===== Status Polling =====

function startStatusPolling(jobId) {
    if (statusPollInterval) clearInterval(statusPollInterval);

    statusPollInterval = setInterval(async () => {
        try {
            const resp = await fetch("/api/transcription/status/" + jobId);
            const data = await resp.json();
            const status = data.status || "";

            showStatus("Статус: " + status, "info");

            if (status.startsWith("✅") || status.startsWith("❌")) {
                clearInterval(statusPollInterval);
                loadHistory();
                if (status.startsWith("✅")) {
                    showStatus("✅ Готово! Результат отправлен в Telegram.", "success");
                }
            }
        } catch (e) {
            // игнорируем сетевые ошибки при polling
        }
    }, 5000);
}

// ===== History =====

async function loadHistory() {
    // Используем существующий endpoint статуса из TranscribeCommandHandler
    // Здесь можно добавить отдельный API endpoint для истории если нужно
    const list = document.getElementById("historyList");
    list.innerHTML = "<div style='color:#888; font-size:14px;'>История будет показана после первой транскрибации</div>";
}

// ===== Utils =====

function showStatus(text, type) {
    const box = document.getElementById("statusBox");
    box.className = "status-box " + type;
    box.textContent = text;
    box.style.display = text ? "block" : "none";
}

function formatSize(bytes) {
    if (bytes < 1024) return bytes + " B";
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + " KB";
    return (bytes / 1024 / 1024).toFixed(1) + " MB";
}
