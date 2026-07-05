package com.library.model;

import java.time.LocalDate;

/**
 * Lớp thực thể đại diện cho Biên bản / Nhật ký giao dịch trả sách (Return Transaction Log).
 * Giúp thư viện lưu vết chi tiết lịch sử các ca trả sách thành công và số tiền phạt đã thu.
 * ĐÃ CHUẨN HÓA KIỂU DỮ LIỆU TÀI CHÍNH SANG LONG VÀ TÍCH HỢP CONSTRUCTOR MẶC ĐỊNH.
 */
public class ReturnRecord {
    private String recordId;      // Mã giao dịch trả sách (Ví dụ: RR001, RR002...)
    private String ticketId;      // Mã phiếu mượn gốc liên kết đến ca trả sách này
    private LocalDate returnDate; // Ngày thực hiện giao dịch trả sách
    private long finePaid;        // 🛠️ ĐÃ SỬA: Số tiền phạt thực thu chuyển sang kiểu long tránh sai lệch số thập phân

    /**
     * [BỔ SUNG CHO GIAI ĐOẠN 2]: Constructor mặc định không tham số
     * Phục vụ cơ chế nạp ánh xạ dữ liệu tự động của Jackson khi mở rộng lưu file log JSON sau này.
     */
    public ReturnRecord() {
    }

    /**
     * Constructor khởi tạo một Biên bản trả sách hoàn chỉnh với cơ chế lập trình phòng vệ.
     */
    public ReturnRecord(String recordId, String ticketId, LocalDate returnDate, long finePaid) {
        this.recordId = recordId != null ? recordId.trim() : "";
        this.ticketId = ticketId != null ? ticketId.trim() : "";
        this.returnDate = returnDate != null ? returnDate : LocalDate.now();
        setFinePaid(finePaid);
    }

    // ─── Getters & Setters (Đảm bảo tính Đóng gói Encapsulation) ──────────────

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId != null ? recordId.trim() : "";
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId != null ? ticketId.trim() : "";
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public long getFinePaid() {
        return finePaid;
    }

    /**
     * Sửa đổi để áp dụng đồng bộ kiểu long và chặn tiền phạt âm an toàn tuyệt đối.
     */
    public void setFinePaid(long finePaid) {
        this.finePaid = Math.max(0L, finePaid);
    }

    @Override
    public String toString() {
        return "ReturnRecord{" +
                "recordId='" + recordId + '\'' +
                ", ticketId='" + ticketId + '\'' +
                ", returnDate=" + returnDate +
                ", finePaid=" + finePaid +
                '}';
    }
}