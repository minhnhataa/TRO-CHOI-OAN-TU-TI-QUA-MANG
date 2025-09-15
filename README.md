<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
     TRÒ CHƠI OẲN TÙ TÌ QUA MẠNG
</h2>
<div align="center">
    <p align="center">
        <img alt="AIoTLab Logo" width="170" src="https://github.com/user-attachments/assets/711a2cd8-7eb4-4dae-9d90-12c0a0a208a2" />
        <img alt="AIoTLab Logo" width="180" src="https://github.com/user-attachments/assets/dc2ef2b8-9a70-4cfa-9b4b-f6c2f25f1660" />
        <img alt="DaiNam University Logo" width="200" src="https://github.com/user-attachments/assets/77fe0fd1-2e55-4032-be3c-b1a705a1b574" />
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)

</div>

## 1. Giới thiệu hệ thống
   Hệ thống Trò chơi Oẳn Tù Tì qua mạng được xây dựng nhằm cung cấp một nền tảng giải trí đơn giản nhưng hấp dẫn, cho phép hai hoặc nhiều người chơi tham gia thi đấu với nhau hoặc người với máy thông qua kết nối mạng Internet.
Ứng dụng được phát triển bằng Java Swing để tạo giao diện đồ họa thân thiện, dễ sử dụng, đồng thời sử dụng giao thức TCP để truyền tải dữ liệu giữa Client và Server một cách ổn định và tin cậy.
Hệ thống được thiết kế theo mô hình Client–Server:
- Client: Chạy trên máy người chơi, hiển thị giao diện, gửi lựa chọn (Kéo – Búa – Bao) và nhận kết quả từ Server.
- Server: Quản lý kết nối, ghép cặp người chơi, xử lý luật chơi, tính toán kết quả và lưu trữ dữ liệu trận đấu vào File hoặc Cơ sở dữ liệu (SQLite/MySQL).
Đặc điểm nổi bật:
- Chơi trực tuyến: Người chơi có thể kết nối từ nhiều máy khác nhau qua mạng LAN hoặc Internet.
- Giao diện trực quan: Sử dụng Java Swing với các form đăng nhập, sảnh chờ, phòng chơi.
- Truyền dữ liệu dạng đối tượng: Sử dụng cơ chế tuần tự hóa (Serialization) của Java để gửi/nhận các đối tượng như thông tin người chơi, kết quả trận đấu.
- Lưu trữ lịch sử: Server lưu lại thông tin các trận đấu để phục vụ thống kê hoặc tra cứu.
- Bảo đảm tính toàn vẹn dữ liệu: Giao thức TCP đảm bảo dữ liệu được truyền đầy đủ, đúng thứ tự.
Mục tiêu của hệ thống:
- Mang lại trải nghiệm giải trí nhanh gọn, dễ tiếp cận.
- Minh họa việc kết hợp lập trình giao diện Java Swing với lập trình mạng và xử lý dữ liệu.
- Tạo nền tảng để mở rộng thành các trò chơi mạng khác trong tương lai

## 2.Ngôn ngữ & Công nghệ chính
[![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Java Swing](https://img.shields.io/badge/Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![TCP Socket](https://img.shields.io/badge/TCP%20Socket-007396?style=for-the-badge&logo=socketdotio&logoColor=white)](https://docs.oracle.com/javase/tutorial/networking/sockets/)
[![Java Serialization](https://img.shields.io/badge/Java%20Serialization-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serialTOC.html)
[![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/index.html)


## 3. Hình ảnh các giao diễn


## 4. Hướng dẫn cài đặt và sử dụng
 **Java Development Kit (JDK)**: Phiên bản 8 trở lên
- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **Môi trường phát triển**: IDE (IntelliJ IDEA, Eclipse, VS Code) hoặc terminal/command prompt
- **Bộ nhớ**: Tối thiểu 128MB RAM
- 
###  Cài đặt và triển khai

- chạy MainServer sau khi hiện console chờ Client kết nối
- chạy MainClient 2 lần để hiện 2 giao diện giả lập để chơi
- tài khoản có thể đăng ký theo mỗi cá nhân 
## Thông tin cá nhân
**Họ tên**: Đậu Cao Minh Nhật.
**Lớp**: CNTT 16-03.
**Email**: daucaominhnhat@gmail.com.

© 2025 AIoTLab, Faculty of Information Technology, DaiNam University. All rights reserved.

---
