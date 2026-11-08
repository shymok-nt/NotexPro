# Kalemlik

Kotlin, Jetpack Compose ve Material 3 ile hazırlanmış, yerel çalışan el yazısı not defteri arayüzü prototipidir.

## Proje yapısı

- `app/src/main/java/com/example/kalemlik/MainActivity.kt`: Navigation Compose, MVVM ViewModel ve kitaplık/yazı/ayarlar ekranları.
- `app/src/main/java/com/example/kalemlik/Theme.kt`: Tüm renk, ölçü ve yüzey token'ları ile koyu tema.
- `app/src/main/res/values/strings.xml`: Uygulamadaki Türkçe metinlerin tek kaynağı.
- `app/src/main/AndroidManifest.xml`: İnternet izni olmayan tek aktivite bildirimi.

Kitaplık başlangıçta gerçek bir defter listesine bağlı boş durum gösterir. Yeni defter iletişim kutusundan girilen ad, yazı ekranına geçişte kullanılır; kalıcı veri kaydı ve çizim motoru sonraki aşama için ayrılmıştır. `InkCanvas` ayrı bir composable olarak hazırdır.

## Çalıştırma

1. Android Studio'da bu klasörü açın.
2. Gradle senkronizasyonunu tamamlayın.
3. Android API 26 veya üzeri bir cihaz/emülatör seçin.
4. `app` yapılandırmasını çalıştırın.

Tablet/telefon düzeni Compose Window Size Class ile seçilir. Durumlar `rememberSaveable` ve `ViewModel` ile ekran döndürmelerinde korunur.
