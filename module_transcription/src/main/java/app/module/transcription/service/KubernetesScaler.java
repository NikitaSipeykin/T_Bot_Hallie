package app.module.transcription.service;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KubernetesScaler {

  @Value("${transcription.k8s.namespace:tbot}")
  private String namespace;

  @Value("${transcription.k8s.deployment:whisper-server}")
  private String deploymentName;

  @Value("${transcription.k8s.enabled:true}")
  private boolean enabled;

  public void scaleUp() {
    if (!enabled) {
      log.info("K8s scaling disabled (local mode), skipping scale up");
      return;
    }
    scale(1);
  }

  public void scaleDown() {
    if (!enabled) {
      log.info("K8s scaling disabled (local mode), skipping scale down");
      return;
    }
    scale(0);
  }

  public boolean isWhisperRunning() {
    if (!enabled) return true; // локально считаем что всегда запущен
    try (KubernetesClient client = new KubernetesClientBuilder().build()) {
      var deployment = client.apps().deployments()
          .inNamespace(namespace)
          .withName(deploymentName)
          .get();

      if (deployment == null) return false;

      Integer readyReplicas = deployment.getStatus().getReadyReplicas();
      return readyReplicas != null && readyReplicas > 0;
    } catch (Exception e) {
      log.error("Failed to check Whisper status: {}", e.getMessage());
      return false;
    }
  }

  private void scale(int replicas) {
    try (KubernetesClient client = new KubernetesClientBuilder().build()) {
      client.apps().deployments()
          .inNamespace(namespace)
          .withName(deploymentName)
          .scale(replicas);
      log.info("Whisper scaled to {} replicas", replicas);
    } catch (Exception e) {
      log.error("Failed to scale Whisper to {}: {}", replicas, e.getMessage());
      throw new RuntimeException("Kubernetes scale failed: " + e.getMessage(), e);
    }
  }
}
