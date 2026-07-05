package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO tiếp nhận yêu cầu tạo mới hoặc cập nhật Độc giả từ Giao diện Frontend.
 */
public class ReaderRequestDTO {

    @NotBlank(message = "Mã độc giả không được để trống!")
    @Pattern(regexp = "^BD\\d{3}$", message = "Mã độc giả phải tuân thủ định dạng 'BDxxx' (Ví dụ: BD001)!")
    private String userId;

    @NotBlank(message = "Họ và tên bạn đọc không được để trống!")
    private String fullName;

    @NotBlank(message = "Số điện thoại liên lạc không được để trống!")
    @Pattern(regexp = "^\\d{10,11}$", message = "Số điện thoại không hợp lệ! Vui lòng nhập từ 10 đến 11 chữ số.")
    private String phoneNumber;

    @NotBlank(message = "Loại độc giả bắt buộc phải lựa chọn!")
    @Pattern(regexp = "^(STUDENT|PRIORITY_STUDENT|LECTURER)$",
            message = "Loại độc giả không hợp lệ! Chỉ chấp nhận: STUDENT, PRIORITY_STUDENT, LECTURER.")
    private String readerType;

    // ─── Constructors ────────────────────────────────────────────────────────
    public ReaderRequestDTO() {
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getReaderType() { return readerType; }
    public void setReaderType(String readerType) { this.readerType = readerType; }
}