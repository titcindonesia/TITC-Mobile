# Riwayat Percakapan & Langkah Pembuatan Aplikasi Android (TITC Global Eduka)

Dokumen ini berisi rangkuman seluruh proses yang telah dilakukan oleh AI (Google Antigravity) untuk menganalisis DOM website `https://apps.titcglobaleduka.or.id/` dan mengonversinya menjadi aplikasi Android Native (*Immersive WebView App*) mengadopsi struktur dan alur kerja proyek `D:\WEB SAYA\info_saham`.

---

## 1. Analisis Ringkas DOM & Komponen Website (`apps.titcglobaleduka.or.id`)

Website target telah dianalisis struktur DOM, CSS, dan perilakunya:
- **Top Bar (Sticky Glassmorphic Header)**: 
  - Hamburger Menu (Navigasi About Us, Terms & Condition, Disclaimer).
  - Ikon Notifikasi (`/newsupdate/`) dan Ikon Akun / Login (`/login`).
- **Hero & Feature Banner**:
  - Banner TOEFL Sekolah Kedinasan & Banner Layanan TITC.
- **Carousel Slider (Swiper.js)**:
  - Slide Program: TOEFL Kedinasan, TOEIC Online, dan Courses (`coursess.png`).
- **Promo Section ("Promo Minggu ini")**:
  - Carousel banner promo mingguan (Slide 79, 78, 80, 167) dan popup modal *Diskon Kemerdekaan*.
- **Bottom Navigation Bar (uDock)**:
  - 4 Tab Utama:
    1. **Home**: `https://apps.titcglobaleduka.or.id/`
    2. **Schedule**: `https://titc.or.id/?ff_landing=21`
    3. **Courses**: `https://titc.or.id/?ff_landing=8`
    4. **PrepTest**: `https://titc.or.id/?ff_landing=15`
- **Design Tokens**:
  - Primary Color: `#6431F6`, Secondary Color: `#7345F7`
  - Typography: Font family `Inter`, sans-serif.

---

## 2. Inisiasi Project Android (Scaffolding)
- **Tool yang Digunakan**: `android-cli` (`android create empty-activity`).
- **Nama Aplikasi**: `TITC Global Eduka`
- **Package Name**: `com.example.titcglobaleduka`
- **Target SDK**: Android 14+ (API 36, Min SDK 24).

---

## 3. Konfigurasi & Modifikasi Source Code

### A. Izin Akses Internet (`AndroidManifest.xml`)
Menambahkan permission jaringan agar WebView dapat memuat data dan aset dari server TITC:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### B. Controller WebView Terprogram (`MainActivity.kt`)
Mengimplementasikan arsitektur WebView modern dengan fitur:
1. **Immersive Edge-to-Edge Fullscreen**:
   - Memanfaatkan `WindowCompat.setDecorFitsSystemWindows` dan `WindowInsetsControllerCompat` untuk menyembunyikan status bar bawaan HP, memberikan pengalaman aplikasi native yang utuh.
2. **Optimalisasi Web Settings**:
   - `javaScriptEnabled = true`: Menjamin seluruh fitur interaktif (Swiper Carousel, popup modal, animasi) berfungsi lancar.
   - `domStorageEnabled = true` & `databaseEnabled = true`: Menyimpan sesi login pengguna, preferensi, dan local storage.
   - `mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW`: Mencegah pemblokiran konten mixed-mode.
   - `LOAD_DEFAULT` cache mode: Cepat dan hemat data saat memuat ulang halaman.
3. **Internal Navigation & Progress Indicator**:
   - `WebViewClient` menangani navigasi antar-halaman internal (Home, Schedule, Courses, PrepTest, Login) tetap di dalam WebView tanpa terlempar ke browser luar.
   - Horizontal progress bar untuk indikator visual ketika halaman sedang dimuat.
4. **Hardware & Gesture Back Button Handler**:
   - Menangani tombol *Back* fisik/gesture (`onBackPressedDispatcher.addCallback`) agar menavigasi riwayat halaman web (`webView.goBack()`) sebelum menutup aplikasi jika sudah berada di halaman awal.

---

## 4. Build dan Kompilasi APK
- Perintah kompilasi:
  ```powershell
  .\gradlew assembleDebug
  ```
- **Status Build**: `BUILD SUCCESSFUL in 1m 38s`.
- **Lokasi File APK Debug**:
  ```
  d:\MAGANG\magang titc\clone web mock\app\build\outputs\apk\debug\app-debug.apk
  ```
- **Ukuran APK**: ~11.9 MB (siap diinstal di perangkat Android / emulator).

---

## 5. Cara Menjalankan & Membangun Ulang Aplikasi
1. **Untuk Build Ulang APK**:
   ```powershell
   .\gradlew assembleDebug
   ```
2. **Untuk Memasang langsung ke HP yang terhubung (USB Debugging)**:
   ```powershell
   .\gradlew installDebug
   ```

---
*Dokumen ini dibuat secara otomatis oleh AI Assistant Antigravity.*
