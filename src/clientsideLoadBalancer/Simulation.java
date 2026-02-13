package clientsideLoadBalancer;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<Server> servers;
    private SoftmaxLoadBalancer loadBalancer;
    private int serverCount;

    public Simulation(int serverCount, double temperature) {
        this.serverCount = serverCount;
        this.servers = new ArrayList<>();
        
        // 1. Sunucuları oluştur (Her biri farklı hıza sahip olacak)
        for (int i = 0; i < serverCount; i++) {
            servers.add(new Server(i));
        }
        
        // 2. Yük Dengeleyiciyi oluştur
        this.loadBalancer = new SoftmaxLoadBalancer(serverCount, temperature);
    }

    public void run(int totalRequests) {
        System.out.println("=== Simülasyon Başlıyor ===");
        System.out.println("Sunucu Sayısı: " + serverCount);
        System.out.println("Toplam İstek: " + totalRequests);
        System.out.println("--------------------------------------------------");
        
        double totalLatency = 0;
        for (int i = 0; i < totalRequests; i++) {
            // ADIM 1: Ajan bir sunucu seçer
            int selectedServerId = loadBalancer.selectServer();
            Server server = servers.get(selectedServerId);

            // ADIM 2: Sunucu isteği işler (Latency üretir)
            double latency = server.processRequest();
            totalLatency += latency;

            // ADIM 3: Ajan sonucu öğrenir ve kendini günceller (Reinforcement Learning)
            loadBalancer.update(selectedServerId, latency);

            // Raporlama: Her 1000 adımda bir durum özeti geç
            if ((i + 1) % 1000 == 0) {
                double currentAvg = totalLatency / (i + 1);
                System.out.printf("İstek: %d | Ortalama Latency: %.2f ms | Son Seçilen: Sunucu-%d\n", 
                                  (i + 1), currentAvg, selectedServerId);
            }
        }
        
        System.out.println("--------------------------------------------------");
        System.out.println("Simülasyon Tamamlandı.");
        System.out.printf("Genel Ortalama Latency: %.2f ms\n", (totalLatency / totalRequests));
    }
}