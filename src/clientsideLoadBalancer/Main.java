package clientsideLoadBalancer;

public class Main {
    public static void main(String[] args) {
        // Parametreler:
        // Sunucu Sayısı: 5
        // Sıcaklık (Temperature): 10.0 (Düşürürsen daha açgözlü/greedy olur, artırırsan daha rastgele olur)
        Simulation sim = new Simulation(5, 10.0);
        
        // 10.000 istek göndererek sistemi test et
        sim.run(100000);
    }
}