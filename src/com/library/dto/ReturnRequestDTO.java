package com.library.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO đóng gói Payload gửi lên khi thực hiện hoàn trả lại sách cho thư viện.
 * 🛠️ Cải tiến đồng bộ với Nhiệm vụ 4.1: Nhận diện chính xác cặp mã độc giả và mã sách.
 */
public class ReturnRequestDTO {

    @NotBlank(message = "Yêu cầu cung cấp mã độc giả trả sách để xác minh phiếu!")
    private String readerId;

    @NotBlank(message = "Yêu cầu cung cấp mã cuốn sách phản hồi hoàn trả!")
    private String bookId;

    // ─── Constructors ────────────────────────────────────────────────────────
    public ReturnRequestDTO() {
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────
    public String getReaderId() { return readerId; }
    public void setReaderId(String readerId) { this.readerId = readerId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
}