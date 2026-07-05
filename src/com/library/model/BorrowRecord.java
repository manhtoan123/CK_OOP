package com.library.model;

/**
 * Lớp đối tượng đóng gói dữ liệu phục vụ riêng cho Bảng giám sát mượn sách thời gian thực phía React.
 * ĐÃ CẬP NHẬT HÀM KHỞI TẠO 5 THAM SỐ ĐỂ ĐẤU NỐI NGÀY HẠN TRẢ SÁCH (DUE DATE).
 */
public class BorrowRecord {
    private String readerId;
    private String bookId;
    private int quantity;
    private String borrowDate;
    private String dueDate; // Trường dữ liệu mới bổ sung

    // 1. Constructor mặc định (No-arg constructor) bắt buộc phải có để Jackson đọc/ghi JSON mượt mà
    public BorrowRecord() {
    }

    // 2. Constructor toàn quyền 5 tham số để khớp nối với BorrowReturnService
    public BorrowRecord(String readerId, String bookId, int quantity, String borrowDate, String dueDate) {
        this.readerId = readerId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    // ─── TRỌN BỘ GETTER VÀ SETTER ───
    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}