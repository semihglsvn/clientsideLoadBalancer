package clientsideLoadBalancer;
import java.util.Random;
import java.util.Arrays;

public class SoftmaxLoadBalancer {
    
    private int serverCount; 
    private double[] estimatedRewards; //Sunucunun tahmini performans puanı.
    private int[] selectionCounts;     // Hangi sunucuyu kaç kere seçtik
    private double temperature;        // Seçim riskini (keşif oranını) belirleyen parametre
    private Random random;

    public SoftmaxLoadBalancer(int serverCount, double temperature) {
        this.serverCount = serverCount;
        this.temperature = temperature;
        this.estimatedRewards = new double[serverCount];
        this.selectionCounts = new int[serverCount];
        this.random = new Random();
        
        // Başlangıçta tüm sunuculara 0 puan veriyoruz.
        Arrays.fill(estimatedRewards, 0.0); 
    }

    /**
     * Hangi sunucuya istek atılacağını seçer.
     * Softmax olasılıklarına göre seçim yapar.
     */
    public int selectServer() {
        double[] probabilities = calculateSoftmaxProbabilities();
        
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        // Olasılıkları kümülatif olarak toplayarak hangi aralığa düştüğünü bul
        // Örn: Sunucu A(%20) -> 0.0-0.20, Sunucu B(%50) -> 0.20-0.70 ...
        for (int i = 0; i < serverCount; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return i;
            }
        }
        
        // Matematiksel küsurat hataları olursa son sunucuyu döndür
        return serverCount - 1; 
    }

    /**
     * Seçilen sunucudan gelen yanıt süresine (latency) göre veritabanımızı günceller.
     * Reinforcement Learning'in "Öğrenme" adımı burasıdır.
     */
    public void update(int serverId, double latency) {
        selectionCounts[serverId]++;
        
        // Latency ne kadar düşükse ödül o kadar yüksek olmalı.
        // O yüzden Latency'i negatife çeviriyoruz (50ms -> -50 puan).
        double reward = -latency; 
        
        // Hareketli Ortalama Formülü (Incremental Mean Formula):
        // Yeni Puan = Eski Puan + (Anlık Hata / Seçilme Sayısı)
        // Bu formül sayesinde tüm geçmişi dizide tutmadan ortalamayı güncelleyebiliyoruz.
        double oldReward = estimatedRewards[serverId];
        estimatedRewards[serverId] = oldReward + (reward - oldReward) / selectionCounts[serverId];
    }

    /**
     * Mevcut puanlara (estimatedRewards) bakarak her sunucunun seçilme ihtimalini hesaplar.
     * Softmax formülü burada uygulanır.
     */
    private double[] calculateSoftmaxProbabilities() {
        double[] probs = new double[serverCount];
        double sumExp = 0.0;
        
        // 1. ADIM: Nümerik Stabilite (Taşmayı önleme)
        // Eğer sayılar çok büyükse Math.exp() sonsuz dönebilir. 
        // Bunu önlemek için dizideki en büyük sayıyı bulup hepsinden çıkaracağız.
        double maxReward = Double.NEGATIVE_INFINITY;
        for (double reward : estimatedRewards) {
            if (reward > maxReward) maxReward = reward;
        }

        // 2. ADIM: Softmax Formülü ( e^(değer/sıcaklık) )
        for (int i = 0; i < serverCount; i++) {
            // (Puan - MaxPuan) yaparak sayıları küçültüyoruz (negatif veya 0 oluyor).
            // Sonucu sıcaklığa bölüyoruz. Sıcaklık yüksekse farklar azalır (olasılıklar eşitlenir).
            double exponent = Math.exp((estimatedRewards[i] - maxReward) / temperature);
            probs[i] = exponent;
            sumExp += exponent;
        }

        // 3. ADIM: Normalizasyon
        // Tüm üstel değerleri toplama bölerek, toplamın 1.0 (%100) olmasını sağlıyoruz.
        for (int i = 0; i < serverCount; i++) {
            probs[i] = probs[i] / sumExp;
        }
        
        return probs;
    }
}