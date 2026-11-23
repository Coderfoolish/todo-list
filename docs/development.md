# Build và kiểm thử

Dự án dùng Gradle Wrapper 9.6.0, Android SDK 37 cho compile và target, và hỗ trợ thiết bị từ API 24. Mã Java được cấu hình với source và target compatibility 11. Cài Android SDK Platform 37 qua SDK Manager và dùng Gradle JDK tương thích với Gradle Wrapper và Android Gradle Plugin của dự án.

Chạy từ thư mục gốc trong PowerShell.

| Mục đích | Lệnh |
| --- | --- |
| Build APK debug | `.\gradlew.bat :app:assembleDebug` |
| Chạy unit tests trên JVM | `.\gradlew.bat :app:testDebugUnitTest` |
| Chạy Android Lint | `.\gradlew.bat :app:lintDebug` |
| Chạy instrumentation tests | `.\gradlew.bat :app:connectedDebugAndroidTest` |

Unit tests nằm trong `app/src/test`. Instrumentation tests nằm trong `app/src/androidTest`, dùng `AndroidJUnitRunner` và cần emulator hoặc thiết bị Android đã kết nối. Hai bộ kiểm thử hiện chứa các bài kiểm thử mẫu.

APK debug được tạo trong `app/build/outputs/apk/debug/`. Để xem giao diện hiện tại trong Android Studio, dùng `GreetingPreview` trong [MainActivity.kt](../app/src/main/java/com/example/todo_list/MainActivity.kt).
