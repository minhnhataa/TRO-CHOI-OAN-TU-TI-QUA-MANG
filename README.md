<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
     TRÒ CHƠI OẲN TÙ TÌ QUA MẠNG(TCP)
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
[![Java](https://img.shields.io/badge/Java-1E90FF?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com/)
[![Java Swing](https://img.shields.io/badge/Java%20Swing-1E90FF?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![TCP Socket](https://img.shields.io/badge/TCP%20Socket-1E90FF?style=for-the-badge&logo=socketdotio&logoColor=white)](https://docs.oracle.com/javase/tutorial/networking/sockets/)
[![Java Serialization](https://img.shields.io/badge/Java%20Serialization-1E90FF?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/8/docs/platform/serialization/spec/serialTOC.html)
[![JDK](https://img.shields.io/badge/JDK-1E90FF?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/downloads/)
[![Eclipse](https://img.shields.io/badge/Eclipse-1E90FF?style=for-the-badge&logo=eclipseide&logoColor=white)](https://www.eclipse.org/downloads/)
[![MySQL](https://img.shields.io/badge/MySQL-1E90FF?style=for-the-badge&logo=mysql&logoColor=white)](https://dev.mysql.com/downloads/)
[![FlatLaf](https://img.shields.io/badge/FlatLaf-1E90FF?style=for-the-badge&logo=java&logoColor=white)](https://github.com/JFormDesigner/FlatLaf/releases)

## 3. Hình ảnh các giao diễn
<p align="center">
  <img src="images/2dangnhap.jpg" alt="Ảnh 1" width="500"/>
</p>

<p align="center">
  <em>Hình 1: Giao diện Đăng nhập/Đăng ký   </em>
</p>

<p align="center">
  <img src="images/3giaodienqtv.jpg" alt="Ảnh 2" width="500"/>
</p>

<p align="center">
  <em>Hình 2: Giao diện Admin   </em>
</p>

<p align="center">
  <img src="images/1sanhcho.jpg" alt="Ảnh 3" width="500"/>
</p>

<p align="center">
  <em>Hình 3: giao diện sảnh chờ   </em>
</p>

<p align="center">
  <img src="images/4lsdcanhan.jpg" alt="Ảnh 4" width="500"/>
</p>

<p align="center">
  <em>Hình 4: Giao diện lịch sử đấu cá nhân   </em>
</p>

<p align="center">
  <img src="images/6BXH.jpg" alt="Ảnh 5" width="500"/>
</p>

<p align="center">
  <em>Hình 5: Giao diện bảng xếp hạng   </em>
</p>

<p align="center">
  <img src="images/7yeucaunamtien.jpg" alt="Ảnh 6" width="500"/>
</p>

<p align="center">
  <em>Hình 6: Giao diện yêu cầu Admin nạp tiền   </em>
</p>

<p align="center">
  <img src="images/8duyetnaptien.jpg" alt="Ảnh 7" width="500"/>
</p>

<p align="center">
  <em>Hình 7: Giao diện duyệt Nạp tiền của Admin   </em>
</p>

<p align="center">
  <img src="images/3giaodienqtv.jpg" alt="Ảnh 8" width="500"/>
</p>

<p align="center">
  <em>Hình 8: Giao dện Xem lịch sử đấu người chơi của Admin   </em>
</p>

<p align="center">
  <img src="images/10taophong.jpg" alt="Ảnh 9" width="500"/>
</p>

<p align="center">
  <em>Hình 9: Giao diện tạo phòng   </em>
</p>

<p align="center">
  <img src="images/11giaodientrandau.jpg" alt="Ảnh 10" width="500"/>
</p>

<p align="center">
  <em>Hình 10: Giao diện trận đấu   </em>
</p>



## 4. Hướng dẫn cài đặt và sử dụng
 **Java Development Kit (JDK)**: Phiên bản 8 trở lên
- **Hệ điều hành**: Windows, macOS, hoặc Linux
- **Môi trường phát triển**: IDE (IntelliJ IDEA, Eclipse, VS Code) hoặc terminal/command prompt

###  Cài đặt và triển khai
## Cài đặt
- **JDK (Java Development Kit)**  
  Bộ công cụ để biên dịch và chạy chương trình Java.  
  - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)  
  - [OpenJDK](https://openjdk.org/)  
  - [Adoptium Temurin](https://adoptium.net/)  

- **Eclipse IDE**  
  Môi trường lập trình Java phổ biến, hỗ trợ viết code, debug, quản lý project.  
  - [Eclipse Downloads](https://www.eclipse.org/downloads/)  

- **MySQL (Server & Workbench)**  
  Hệ quản trị cơ sở dữ liệu để lưu trữ thông tin người dùng, giao dịch,...  
  - [MySQL Installer (Windows)](https://dev.mysql.com/downloads/installer/)  
  - [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)  
  - [MySQL Workbench](https://dev.mysql.com/downloads/workbench/) (công cụ trực quan để quản lý database)  

- **FlatLaf**  
  Thư viện giao diện giúp ứng dụng Java Swing có giao diện hiện đại, đẹp mắt.  
  - [FlatLaf Website](https://www.formdev.com/flatlaf/)  
  - [FlatLaf GitHub Releases](https://github.com/JFormDesigner/FlatLaf/releases)  

## Triển khai
- chạy MainServer sau khi hiện console chờ Client kết nối
- đăng nhập tài khoản admin (tk: admin/ mk: admin) để sử dụng chức năng quản trị viên
- chạy GameClient 2 lần để hiện 2 giao diện giả lập để chơi
- tài khoản có thể đăng ký theo mỗi cá nhân 
## Thông tin cá nhân
- **Họ tên**: Đậu Cao Minh Nhật.
- **Lớp**: CNTT 16-03.
- **Email**: daucaominhnhat@gmail.com.

© 2025 AIoTLab, Faculty of Information Technology, DaiNam University. All rights reserved.

---
