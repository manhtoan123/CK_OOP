import React, { useState } from 'react';

export default function ReturnBookForm() {
    const [payload, setPayload] = useState({
        readerId: '',
        bookId: ''
    });

    const handleReturn = async (e) => {
        e.preventDefault();
        try {
            // 🛠️ NHIỆM VỤ 6.2: Đóng gói đồng thời cả readerId và bookId truyền lên API /return
            const res = await fetch('http://localhost:8080/api/v1/return', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    readerId: payload.readerId.trim().toUpperCase(),
                    bookId: payload.bookId.trim().toUpperCase()
                })
            });

            const data = await res.json();

            // 🛠️ NHIỆM VỤ 6.3: Bóc tách lỗi chi tiết khi res.status gặp mã lỗi 400 hoặc 404
            if (!res.ok) {
                throw new Error(data.message || "Giao dịch hoàn sách bị từ chối!");
            }

            // Trả sách thành công (Có thể hiển thị tiền phạt nếu quá hạn trực tiếp từ log backend)
            alert(`🎉 Trả sách thành công!\nThông báo: ${data.message}`);
            setPayload({ readerId: '', bookId: '' }); // Xóa sạch ô nhập dữ liệu
        } catch (err) {
            alert(`❌ Vi phạm quy chế hoặc Sai lệch dữ liệu:\n${err.message}`);
        }
    };

    return (
        <div className="card p-4 shadow-sm border-warning mb-4">
            <h3 className="card-title text-warning mb-3">Quầy nhận trả sách & Tính phạt trễ hạn</h3>
            <form onSubmit={handleReturn}>
                <div className="mb-3">
                    <label className="form-label">Mã độc giả trả sách (BDxxx)</label>
                    <input
                        type="text" className="form-control" placeholder="Ví dụ: BD001" required
                        value={payload.readerId} onChange={e => setPayload({...payload, readerId: e.target.value})}
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Mã cuốn sách hoàn trả (Bxxx)</label>
                    <input
                        type="text" className="form-control" placeholder="Ví dụ: B001" required
                        value={payload.bookId} onChange={e => setPayload({...payload, bookId: e.target.value})}
                    />
                </div>
                <button type="submit" className="btn btn-warning w-100 text-whitefw-bold">
                    Xác nhận thu hồi sách & Quyết toán
                </button>
            </form>
        </div>
    );
}