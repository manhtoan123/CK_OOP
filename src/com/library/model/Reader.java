package com.library.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.library.service.policy.FinePolicy; // Import bản chuẩn interface chiến lược phạt
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

/**
 * Lớp trừu tượng (Abstract class) đại diện cho Bạn đọc trong hệ thống thư viện.
 * Kế thừa từ lớp cơ sở User và đóng vai trò cấu hình đa hình cho Strategy Pattern.
 * ĐÃ NHÚNG CẤU HÌNH ĐA HÌNH JSON CHUẨN KIẾN TRÚC.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "readerType" // Tên thuộc tính phân loại độc giả trong file JSON dùng làm khóa ánh xạ
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StudentReader.class, name = "Sinh viên thường"),
        @JsonSubTypes.Type(value = PriorityStudentReader.class, name = "Sinh viên ưu tiên"),
        @JsonSubTypes.Type(value = LecturerReader.class, name = "Giảng viên")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Reader extends User {

    // Sử dụng @JsonIgnore để ngăn Jackson serialize/deserialize interface FinePolicy thành các trường rỗng độc lập
    @JsonIgnore
    private final FinePolicy finePolicy;

    /**
     * Constructor khởi tạo thông tin bạn đọc chung.
     * Tích hợp kiểm tra phòng vệ an toàn hệ thống.
     *
     * @param userId      Mã độc giả (Định dạng: BDxxx)
     * @param fullName    Họ và tên độc giả
     * @param phoneNumber Số điện thoại liên lạc
     * @param finePolicy  Chiến lược tính tiền phạt trễ hạn được tiêm từ lớp con
     */
    public Reader(String userId, String fullName, String phoneNumber, FinePolicy finePolicy) {
        super(userId, fullName, phoneNumber);

        // Cơ chế phòng vệ chặn lỗi tiêm chiến lược trống (Null Strategy Injection)
        if (finePolicy == null) {
            throw new IllegalArgumentException("Lỗi hệ thống: Chiến lược tính phí phạt (FinePolicy) không được phép để null!");
        }
        this.finePolicy = finePolicy;
    }

    /**
     * Getter cho phép tầng nghiệp vụ (Service) lấy ra chiến lược phạt tương ứng
     * của từng loại độc giả để tính toán đa hình khi trả sách trễ hạn.
     * * @return Đối tượng thực thi interface FinePolicy
     */
    public FinePolicy getFinePolicy() {
        return finePolicy;
    }

    // ─── Các phương thức trừu tượng buộc các lớp con phải triển khai ─────────────

    /**
     * Lấy giới hạn số lượng sách mượn tối đa cùng lúc của loại độc giả này.
     */
    public abstract int getMaxBorrowLimit();

    /**
     * Lấy tên phân loại hiển thị của độc giả (Sinh viên thường, Giảng viên...).
     */
    public abstract String getReaderType();

    /**
     * Lấy định mức số tiền phạt quá hạn cơ sở mỗi ngày (phục vụ mục đích hiển thị tra cứu).
     */
    public abstract int getFinePerDay();

    // ─── Chuẩn hóa hiển thị thông tin ──────────────────────────────────────────

    @Override
    public String toString() {
        return super.toString()
                + ", readerType='" + getReaderType() + "'"
                + ", finePerDay=" + getFinePerDay()
                + ", maxBorrowLimit=" + getMaxBorrowLimit();
    }
}