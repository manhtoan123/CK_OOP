# 📚 Hệ Thống Quản Lý Thư Viện - Bài tập lớn OOP

Dự án **Hệ Thống Quản Lý Thư Viện (Lib-Management)** là đồ án môn học Lập trình hướng đối tượng (OOP) do **Nhóm 4** thực hiện. Hệ thống được xây dựng theo kiến trúc **Tách biệt hoàn toàn Frontend - Backend (Decoupled Architecture)** giúp tối ưu hóa luồng xử lý dữ liệu thời gian thực và đồng bộ hóa lưu trữ tệp tin cứng cấu trúc JSON.

---

## 📊 1. Kiến Trúc Hệ Thống & Công Nghệ Sử Dụng

Dự án áp dụng mô hình phân tầng khép kín, chia tách vai trò độc lập giữa giao diện người dùng và lõi xử lý nghiệp vụ:

* **Backend (Java Spring Boot REST API):**
    * **Framework:** Spring Boot v3.2.5, OpenJDK 26, Apache Tomcat Nhúng (Port `8080`).
    * **Database Engine:** Lưu trữ phi quan hệ trực tiếp xuống các tệp cứng JSON (`data/readers.json`, `data/books.json`, `data/borrow_tickets.json`) qua thư viện **Jackson ObjectMapper**.
    * **Cơ chế OOP nâng cao:** Áp dụng **Tính Đa hình (Polymorphism)** định danh động lớp con, bẫy lỗi tập trung qua `GlobalExceptionHandler` và triển khai thuật toán tính phí phạt trễ hạn tự động.
* **Frontend (React.js Single Page Application):**
    * **Công nghệ :** React v18+, Vite Bundler (Port `5173`).
    * **Styling & UI:** Bootstrap 5, Indigo/Emerald Tailwind-inspired CSS palettes, Giao diện Dashboard Responsive.
    * **Giao tiếp dữ liệu:** Gọi các cổng xử lý HTTP Methods (`GET`, `POST`, `PUT`, `DELETE`) bất đồng bộ qua `Fetch API` xuyên qua bộ lọc chống chặn `CORS`.

---

## 🌟 2. Các Tính Năng Trong Dự Án

1. **Quản Lý Độc Giả (CRUD Đa Hình):** Thêm, sửa, xóa, hiển thị 3 loại thẻ (Sinh viên thường, Sinh viên ưu tiên, Giảng viên) với hạn mức mượn riêng. Dữ liệu phân loại lưu bằng Tiếng Việt trong JSON và tự động ánh xạ thành lớp con tương ứng trên RAM qua Jackson Polymorphic Deserialization.
2. **Quản Lý Danh Mục Sách (CRUD Kho Sách):** Thêm, sửa, xóa đầu sách. Tự động kiểm tra trùng lặp mã sách và xác thực tính hợp lệ của dữ liệu đầu vào (số lượng, đơn giá) thông qua `BookRequestDTO`.
3. **Quản Lý Giao Dịch Mượn / Trả Sách:** Xử lý cấp phiếu mượn mới (tự động trừ kho và kiểm tra hạn mức thẻ bạn đọc) và thu hồi sách hoàn trả (tự động cộng trả số lượng vào tệp JSON).
4. **Sổ Giám Sát Lưu Thông Sách:** Hiển thị thời gian thực các phiên mượn hoạt động bằng cấu trúc phẳng `BorrowRecord` (Mã độc giả, Mã sách, Số lượng, Ngày mượn, `dueDate`). Tích hợp nút "Trả nhanh" để quyết toán giao dịch trực tiếp từ bảng dữ liệu.
5. **Tự Động Tính Phí Phạt Trễ Hạn:** So sánh ngày trả thực tế với `dueDate`. Nếu quá hạn, hệ thống tự áp dụng chính sách phạt (Fine Policy) riêng của từng loại thẻ độc giả để tính số tiền phạt và hiển thị trực quan lên giao diện.
6. **Kiểm Tra Ràng Buộc Nghiệp Vụ:** Tự động từ chối xóa độc giả nếu tài khoản đó chưa hoàn trả sách hoặc chưa quyết toán hết tiền phạt, bảo đảm tính toàn vẹn liên kết dữ liệu giữa các tệp JSON.

---

## 📁 3. Cấu Trúc Thư Mục Dự Án Tiêu Chuẩn

```text
HeThongQuanLyThuVien/
- data/                           [Thư mục lưu tệp JSON Database]
- src/main/java/com/library/      [Mã nguồn Backend Java Spring Boot]
    - config/                       - Cấu hình hệ thống (SecurityConfig)
    - controller/                   - Tầng tiếp nhận API (Handler)
    - dto/                          - Đối tượng đóng gói dữ liệu
    - exception/                    - Bộ Custom Exceptions
    - model/                        - Lớp thực thể (Reader, Book, Ticket,...)
    - repository/                   - Tầng truy xuất và lưu trữ dữ liệu (Đọc/ghi file JSON)
    - service/                      - Tầng xử lý logic nghiệp vụ
- frontend/                       [Mã nguồn Frontend React SPA]
    - src/components/               - Các Form nhập liệu nghiệp vụ
    - src/App.jsx                   - Giao diện điều phối và bảng giám sát
    - vite.config.js                - Cấu hình Bundler Vite
- .gitignore                      [Bộ lọc chặn file rác node_modules]
- pom.xml                         [Tệp cấu hình dependency Maven]