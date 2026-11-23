# Thư viện và phiên bản

Các phiên bản dưới đây lấy từ [gradle/libs.versions.toml](../gradle/libs.versions.toml). Phạm vi sử dụng được khai báo trong [app/build.gradle.kts](../app/build.gradle.kts).

| Thư viện hoặc plugin | Phiên bản | Vai trò |
| --- | --- | --- |
| Android Gradle Plugin | 9.5.2 | Build module Android. |
| Kotlin Compose compiler plugin | 2.2.10 | Biên dịch hàm composable. |
| Activity Compose | 1.13.0 | Kết nối Activity với Compose qua `setContent`. |
| Compose BOM | 2026.02.01 | Đồng bộ phiên bản Compose cho ứng dụng và instrumentation tests. |
| Compose Material 3 | Theo BOM | Cung cấp theme và thành phần như `Scaffold`, `Text`. |
| Compose UI và UI Graphics | Theo BOM | Cung cấp API giao diện và đồ họa Compose. |
| Compose UI Tooling Preview và UI Tooling | Theo BOM | Hỗ trợ `@Preview` và công cụ xem trước trong bản debug. |
| AndroidX Core KTX | 1.19.1 | Cung cấp các tiện ích Kotlin cho API Android. |
| AndroidX Lifecycle Runtime KTX | 2.11.1 | Cung cấp API lifecycle và hỗ trợ coroutine. |
| JUnit | 4.13.2 | Chạy unit tests trên JVM. |
| AndroidX Test Ext JUnit | 1.3.0 | Tích hợp JUnit với instrumentation tests. |
| Espresso Core | 3.7.0 | Hỗ trợ kiểm thử giao diện Android trên thiết bị. |
| Compose UI Test JUnit4 và UI Test Manifest | Theo BOM | Hỗ trợ kiểm thử Compose. Test manifest thuộc cấu hình debug. |

Khi thêm thư viện Compose được BOM quản lý, khai báo alias mà không gắn phiên bản riêng. Đặt dependency vào cấu hình Gradle phù hợp với nơi sử dụng.
