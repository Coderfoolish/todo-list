# Hướng dẫn làm việc với todo-list

Dự án Android có một module `:app`, viết bằng Kotlin và Jetpack Compose Material 3. Ứng dụng có hai màn hình Compose cho danh sách và biểu mẫu thêm/sửa công việc. Khi triển khai tính năng, đối chiếu với mã nguồn để xác định phần nào đã có.

- [Cấu trúc và luồng giao diện](docs/architecture.md)
- [Thư viện và phiên bản](docs/libraries.md)
- [Build và kiểm thử](docs/development.md)
- [Nhật ký thay đổi](CHANGLOG.md)

Cập nhật `CHANGLOG.md` theo ngày thực tế. Khi sang ngày mới, tạo tiêu đề ngày mới ở đầu nhật ký và ghi các thay đổi của ngày đó bên dưới.

## Khi thay đổi mã nguồn

- Giữ mô hình Compose và dùng `TodolistTheme` cho màn hình mới. Giữ cách truyền `Modifier` và xử lý padding của `Scaffold` để nội dung tránh thanh hệ thống.
- Khai báo phiên bản và alias thư viện trong `gradle/libs.versions.toml`. Dùng alias trong `app/build.gradle.kts` và để Compose BOM quản lý các phiên bản Compose đã khai báo qua BOM.
- Chạy các tác vụ liên quan đến thay đổi và ghi rõ những bước chưa thể xác minh.
