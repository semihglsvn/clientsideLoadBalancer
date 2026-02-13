
# Softmax Action Selection Load Balancer

Bu proje, dağıtık sistemlerde istemci taraflı (client-side) yük dengeleme problemini simüle eden bir Java uygulamasıdır. Sabit olmayan (non-stationary) ve gürültülü (noisy) sunucu performanslarına sahip bir kümede, toplam gecikme süresini (latency) minimize etmeyi hedefler.

## Proje Tanımı ve Çalışma Mantığı

Sistem, geleneksel deterministik algoritmalar (Round-Robin vb.) yerine, geçmiş performans verisine dayalı olasılıksal bir seçim mekanizması olan **Softmax Action Selection** algoritmasını kullanır. Yük dengeleyici, Reinforcement Learning (Pekiştirmeli Öğrenme) prensiplerine göre çalışır ve şu döngüyü izler:

1. **Tahmin (Estimation):** Her sunucu için geçmiş yanıt sürelerinin ortalamasına dayalı bir kalite puanı (Q-Value) tutulur.
2. **Olasılık Hesabı (Softmax):** Sunucuların Q değerleri, Softmax fonksiyonu kullanılarak seçilme olasılıklarına dönüştürülür. Performansı yüksek (düşük latency) sunuculara daha yüksek olasılık atanır.
3. **Seçim (Action):** Hesaplanan olasılık dağılımı üzerinden "Rulet Tekerleği" (Weighted Random Selection) yöntemiyle bir sunucu seçilir.
4. **Geri Bildirim ve Güncelleme (Update):** Seçilen sunucudan gelen gerçek yanıt süresi (latency) alınır ve o sunucunun kalite puanı "Artımlı Ortalama Formülü" (Incremental Mean Formula) ile güncellenir.

## Teknik Detaylar

### Nümerik Stabilite (Shifted Softmax)

Softmax fonksiyonundaki üstel hesaplamalar (), büyük değerlerde aritmetik taşma (overflow) veya sonsuzluk (Infinity/NaN) hatalarına yol açabilir. Bu projede, **Shifted Softmax** tekniği uygulanarak kararlılık sağlanmıştır. Hesaplama öncesinde dizideki maksimum değer tüm elemanlardan çıkarılır:

### Sıcaklık (Temperature - ) Parametresi

Algoritmanın **Keşif (Exploration)** ve **Sömürü (Exploitation)** dengesi  parametresi ile kontrol edilir.

* **Düşük :** Algoritma daha seçici davranır, sadece en iyi sunucuya odaklanır (Greedy yaklaşım).
* **Yüksek :** Algoritma performans farklarını minimize eder ve sunucular arasında daha eşit (Rastgele) bir dağılım yapar.
