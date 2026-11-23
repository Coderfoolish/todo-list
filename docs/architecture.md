# Cấu trúc dự án

| Đường dẫn | Vai trò |
| --- | --- |
| [settings.gradle.kts](../settings.gradle.kts) | Khai báo module `:app` và các kho Gradle. |
| [app/build.gradle.kts](../app/build.gradle.kts) | Cấu hình Android, Compose, dependencies và instrumentation runner. |
| [AndroidManifest.xml](../app/src/main/AndroidManifest.xml) | Khai báo ứng dụng và activity. |
| [MainActivity.kt](../app/src/main/java/com/example/todo_list/MainActivity.kt) | Điểm vào của giao diện và nơi chứa màn hình mẫu. |
| [ui/theme](../app/src/main/java/com/example/todo_list/ui/theme) | Bảng màu, kiểu chữ và `TodolistTheme`. |

## Luồng giao diện hiện tại

`MainActivity.onCreate` bật edge-to-edge rồi gọi `setContent`. `TodolistTheme` bọc `Scaffold` và `Greeting` nhận padding từ `Scaffold`. `GreetingPreview` dùng cùng theme để xem trước trong Android Studio.

Tính năng tạo, sửa, hoàn thành và lưu danh sách công việc chưa được triển khai. Dự án hiện chưa khai báo Room, Navigation hay thư viện dependency injection.
