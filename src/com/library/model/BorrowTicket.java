package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lớp thực thể đại diện cho Phiếu mượn/trả sách của thư viện.
 * Quản lý trạng thái mượn và danh sách chi tiết các cuốn sách được mượn cùng lúc.
 * ĐÃ CHUẨN HÓA KIỂU DỮ LIỆU TIỀN TỆ SANG LONG VÀ TƯƠNG THÍCH JSON MAPPER.
 */
public class BorrowTicket {

    /**
     * Định nghĩa Enum quản lý trạng thái phiếu mượn chống sai sót dữ liệu chuỗi.
     */
    public enum TicketStatus {
        BORROWING("Đang mượn"),
        RETURNED("Đã trả");

        private final String value;

        TicketStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        /**
         * Chuyển đổi linh hoạt từ văn bản hoặc tên Enum sang trạng thái chuẩn.
         */
        public static TicketStatus fromValue(String value) {
            if (value == null) return BORROWING;
            for (TicketStatus status : TicketStatus.values()) {
                if (status.getValue().equalsIgnoreCase(value.trim()) || status.name().equalsIgnoreCase(value.trim())) {
                    return status;
                }
            }
            return BORROWING;
        }
    }

    private String ticketId;
    private String readerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private TicketStatus status;
    private long finePaid; // 🛠️ ĐÃ SỬA: Chuyển đổi từ double sang long để lưu tiền VND chuẩn xác
    private List<BorrowTicketDetail> details = new ArrayList<>();

    /**
     * [BỔ SUNG CHO GIAI ĐOẠN 2]: Constructor mặc định không tham số
     * Bắt buộc phải có để Jackson ObjectMapper có thể tự khởi tạo và nạp dữ liệu từ file tickets.json.
     */
    public BorrowTicket() {
    }

    /**
     * Constructor đầy đủ tham số dùng để khởi tạo nhanh phiếu mượn mới.
     */
    public BorrowTicket(String ticketId, String readerId, LocalDate borrowDate, LocalDate dueDate,
                        LocalDate returnDate, TicketStatus status, long finePaid, List<BorrowTicketDetail> details) {
        setTicketId(ticketId);
        setReaderId(readerId);
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        setFinePaid(finePaid);
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
    }

    // ─── Getters & Setters (Đóng gói an toàn hệ thống) ─────────────────────────

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId != null ? ticketId.trim().toUpperCase() : "";
    }

    public String getReaderId() {
        return readerId;
    }

    public void setReaderId(String readerId) {
        this.readerId = readerId != null ? readerId.trim().toUpperCase() : "";
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public long getFinePaid() {
        return finePaid;
    }

    /**
     * Sửa đổi Setter để nhận giá trị số nguyên long và chặn hoàn toàn giá trị phạt âm.
     */
    public void setFinePaid(long finePaid) {
        this.finePaid = Math.max(0L, finePaid);
    }

    /**
     * Lấy danh sách chi tiết các cuốn sách mượn.
     * @return Một bản sao danh sách Chỉ đọc (Defensive Copy) để bảo vệ an toàn dữ liệu RAM.
     */
    public List<BorrowTicketDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

    public void setDetails(List<BorrowTicketDetail> details) {
        this.details = details != null ? new ArrayList<>(details) : new ArrayList<>();
    }

    /**
     * Thêm dòng chi tiết cuốn sách được đăng ký mượn vào phiếu.
     */
    public void addDetail(BorrowTicketDetail detail) {
        if (detail != null) {
            this.details.add(detail);
        }
    }

    @Override
    public String toString() {
        return "BorrowTicket{" +
                "ticketId='" + ticketId + '\'' +
                ", readerId='" + readerId + '\'' +
                ", status=" + status +
                ", finePaid=" + finePaid +
                ", detailsCount=" + details.size() +
                '}';
    }
}