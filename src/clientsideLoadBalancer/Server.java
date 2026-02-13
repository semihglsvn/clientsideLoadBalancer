package clientsideLoadBalancer;

import java.util.Random;

public class Server {
    private int id;
    private double latency;
    private Random random;

    public Server(int id) {
        this.id = id;
        this.random = new Random();
        this.latency = 50 + random.nextInt(100); 
    }

    public double processRequest() {
        driftParameters(); 
        double noise = random.nextGaussian() * 5; 
        double actualLatency = this.latency + noise;
        
        return Math.max(1, actualLatency); 
    }

    private void driftParameters() {
        double change = (random.nextGaussian()); 
        this.latency += change;
        
        // Sınır 
        if (this.latency < 20) this.latency = 20;
        if (this.latency > 300) this.latency = 300;
    }
}