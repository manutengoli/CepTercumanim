# Cep Tercümanım (Android)

Reklamsız, internet bağlantısıyla çalışan örnek Android tercüman projesi.

## Özellikler
- Türkçe, İngilizce ve Tayca konuşmayı otomatik algılamayı dener.
- Türkçe konuşma İngilizceye; İngilizce veya Tayca konuşma Türkçeye çevrilir.
- Çeviri ekranda yazı olarak gösterilir ve Android TTS ile seslendirilir.
- Reklam SDK'sı, analiz SDK'sı veya ücretli API anahtarı içermez.

## Önemli sınırlamalar
- Otomatik dil algılama, telefondaki konuşma tanıma uygulamasının/Google konuşma hizmetinin Android 14+ dil algılama desteğine bağlıdır. Desteklenmiyorsa dil algılama başarısız olabilir.
- Dil algılama sonucu dönmezse uygulama Tayca karakterleri ve Türkçe karakterleri kontrol eder; diğer konuşmaları İngilizce varsayar. Bu nedenle her konuşmada kusursuz otomatik algılama garanti değildir.
- Çeviri MyMemory'nin herkese açık çevrimiçi API'sini kullanır. Ücretsiz hizmetin hız/kullanım sınırları ve kalite değişebilir. Hassas veya gizli konuşmalar için uygun değildir.
- Sesli okuma Android'in kurulu TTS motoruna ve Türkçe/İngilizce/Tayca ses verilerine bağlıdır. Gerekirse Ayarlar > Metin okuma çıkışı bölümünden ses verilerini indirin.
- Uygulama çevrimdışı çalışmaz.

## APK oluşturma
1. Bilgisayara Android Studio (güncel sürüm) ve JDK 17 kurun.
2. Bu klasörü Android Studio ile açın.
3. Gradle eşitlemesi tamamlandıktan sonra Build > Build Bundle(s) / APK(s) > Build APK(s) seçin.
4. APK genellikle `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşur.
5. APK'yı telefona aktarın. Yalnızca kendi oluşturduğunuz/güvendiğiniz APK'yı yükleyin.

## İlk çalıştırma
- Mikrofon iznini verin.
- Konuşma tanıma servisini ve interneti etkinleştirin.
- "KONUŞ / DİNLE" düğmesine basıp tek seferde bir kişinin konuşmasını bekleyin.
- Çeviri geldikten sonra telefon hedef dilde seslendirir.

Bu, derlenebilir bir başlangıç projesidir; bu ortamda imzalı/kurulabilir APK derlenmiş değildir.
