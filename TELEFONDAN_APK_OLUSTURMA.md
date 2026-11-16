# Telefonda APK oluşturma (bilgisayar gerekmez)

Bu proje için APK, GitHub Actions kullanılarak GitHub sunucularında derlenebilir.

## 1. GitHub'da depo oluştur
1. Telefon tarayıcısından https://github.com adresini açıp hesabına giriş yap.
2. `New repository` seçeneğine dokun.
3. Depoya `CepTercumanim` adını ver ve `Create repository` seç.

## 2. Proje dosyalarını yükle
1. ZIP dosyasını telefonda çıkar.
2. GitHub deposunda `Add file` > `Upload files` seç.
3. Çıkarılmış klasörün içindeki proje dosyalarını yükle ve `Commit changes` yap.
4. `.github/workflows/android-apk.yml` dosyasını da depoya ekle. Gizli `.github` klasörünü telefon dosya yöneticisi göstermiyorsa:
   - Depoda `Add file` > `Create new file` seç.
   - Dosya adı olarak `.github/workflows/android-apk.yml` yaz.
   - ZIP içindeki aynı adlı dosyanın içeriğini yapıştırıp `Commit changes` yap.

## 3. APK'yı derle ve indir
1. Depoda `Actions` sekmesine gir.
2. `Android APK` iş akışını seçip `Run workflow` düğmesine bas. (Dosyaları `main` dalına yüklediysen yükleme sonrası otomatik de çalışır.)
3. İşlem yeşil tik ile tamamlanınca çalıştırma sayfasının altındaki `Artifacts` bölümünden `Cep-Tercumanim-APK` dosyasını indir.
4. İndirilen ZIP'i aç. İçindeki `app-debug.apk` dosyasına dokun.
5. Android sorarsa tarayıcıya/dosya yöneticisine "Bu kaynaktan uygulama yüklemeye izin ver" iznini ver ve kurulumu tamamla.

## Notlar
- Bu, test amaçlı debug APK'dır; kişisel kullanım için telefona kurulabilir. Play Store yayını için ayrıca imzalı release APK gerekir.
- Uygulama çeviri için internet kullanır; mikrofon izni ve telefonda konuşma tanıma/TTS hizmetleri gerekir.
