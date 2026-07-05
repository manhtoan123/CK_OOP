package com.library.service;

import com.library.exception.DataNotFoundException;
import com.library.model.Reader;
import com.library.repository.ReaderRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
/**
 * Tầng xử lý logic nghiệp vụ liên quan đến Độc giả.
 * ĐÃ CẬP NHẬT BỘ NGOẠI LỆ CHUYÊN BIỆT THEO NHIỆM VỤ 4.3 VÀ BỔ SUNG ĐẦY ĐỦ HÀM CRUD PHỤC VỤ REAC FRONTEND.
 */
public class ReaderService {
    private final ReaderRepository readerRepository;

    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    public List<Reader> getAllReaders() {
        return readerRepository.getAll();
    }

    /**
     * 🛠️ Nhiệm vụ 4.3: Áp dụng DataNotFoundException khi tìm kiếm tài khoản độc giả không tồn tại.
     */
    public Reader getReaderById(String readerId) throws DataNotFoundException {
        if (readerId == null || readerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi: Mã độc giả cần tìm không được phép để trống!");
        }
        Reader reader = readerRepository.findById(readerId.trim());
        if (reader == null) {
            throw new DataNotFoundException("Lỗi nghiệp vụ: Không tìm thấy tài khoản độc giả mang mã '" + readerId + "' trên hệ thống!");
        }
        return reader;
    }

    public void registerReader(Reader reader) {
        if (reader == null || reader.getUserId() == null || reader.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Thông tin bạn đọc đăng ký không hợp lệ!");
        }
        if (readerRepository.findById(reader.getUserId().trim()) != null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Mã bạn đọc '" + reader.getUserId() + "' đã tồn tại trên hệ thống!");
        }

        validateReaderData(reader.getFullName(), reader.getPhoneNumber());
        readerRepository.add(reader);
    }

    /**
     * 🛠️ BỔ SUNG CHO FRONTEND: Hàm cập nhật thông tin thẻ độc giả (Sửa)
     * Khớp nối chính xác 100% với tên hàm gọi từ LibraryApiController.
     */
    public void updateReader(String id, Reader reader) throws DataNotFoundException {
        if (id == null || id.trim().isEmpty() || reader == null) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Dữ liệu bạn đọc cần cập nhật không hợp lệ!");
        }
        if (readerRepository.findById(id.trim()) == null) {
            throw new DataNotFoundException("Không thể cập nhật! Không tồn tại độc giả mang mã '" + id + "' trên hệ thống JSON!");
        }

        validateReaderData(reader.getFullName(), reader.getPhoneNumber());
        // Gọi xuống tầng lưu trữ tệp JSON để ghi đè thông tin mới
        readerRepository.update(reader);
    }

    /**
     * 🛠️ BỔ SUNG CHO FRONTEND: Hàm xóa hoàn toàn tài khoản độc giả khỏi tệp JSON (Xóa)
     * Khớp nối chính xác 100% với tên hàm gọi từ LibraryApiController.
     */
    public void deleteReader(String id) throws DataNotFoundException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Mã độc giả cần xóa không hợp lệ!");
        }
        if (readerRepository.findById(id.trim()) == null) {
            throw new DataNotFoundException("Không thể xóa! Không tồn tại độc giả mang mã '" + id + "' trên hệ thống JSON!");
        }

        // Thực hiện lệnh xóa trong kho lưu trữ dữ liệu
        // 💡 Mẹo nhỏ: Nếu file ReaderRepository của nhóm đặt tên hàm xóa là .deleteById() hoặc .remove()
        // thì bạn chỉ cần sửa chữ .delete() dưới đây thành tên hàm thực tế đó của nhóm là được nhé!
        readerRepository.delete(id.trim());
    }

    /**
     * Giữ lại hàm cũ của nhóm để bảo toàn các luồng kiểm thử cũ (nếu có)
     */
    public void updateReaderInfo(Reader reader) throws DataNotFoundException {
        if (reader == null) return;
        updateReader(reader.getUserId(), reader);
    }

    private void validateReaderData(String fullName, String phoneNumber) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Họ và tên bạn đọc không được phép để trống!");
        }
        if (fullName.trim().length() < 2) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Họ và tên quá ngắn!");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Lỗi nghiệp vụ: Số điện thoại liên lạc không được phép để trống!");
        }
        if (!phoneNumber.trim().matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Lỗi định dạng: Số điện thoại phải chỉ chứa từ 10 đến 11 chữ số!");
        }
    }
}