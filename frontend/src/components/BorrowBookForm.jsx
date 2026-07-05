import React, { useState } from 'react';

export default function BorrowBookForm() {
    const [borrowData, setBorrowData] = useState({
        readerId: '',
        bookId: '',
        quantity: 1
    });

    const handleBorrow = async (e) => {
        e.preventDefault();
        try {
            const res = await fetch('http://localhost:8080/api/v1/borrow', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    readerId: borrowData.readerId.trim().toUpperCase(),
                    bookId: borrowData.bookId.trim().toUpperCase(),
                    quantity: parseInt(borrowData.quantity)
                })
            });

            const data = await res.json();

            if (!res.ok) {
                // NHIỆM VỤ 6.3: Bắt lỗi vượt hạn mức (3 cuốn, 10 cuốn) hoặc hết hàng tồn kho
                throw new Error(data.message || "Cấp phiếu mượn thất bại!");
            }

            alert(`🎉 Đăng ký mượn sách thành công!\n${data.message}`);
            setBorrowData({ readerId: '', bookId: '', quantity: 1 });
        } catch (err) {
            alert(`❌ Từ chối cấp phiếu mượn:\n${err.message}`);
        }
    };

    return (
        <div className="card p-4 shadow-sm border-success mb-4">
            <h3 className="card-title text-success mb-3">Đăng ký cấp phiếu mượn sách</h3>
            <form onSubmit={handleBorrow}>
                <div className="mb-3">
                    <label className="form-label">Mã độc giả mượn (BDxxx)</label>
                    <input
                        type="text" className="form-control" required
                        value={borrowData.readerId} onChange={e => setBorrowData({...borrowData, readerId: e.target.value})}
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Mã cuốn sách mượn (Bxxx)</label>
                    <input
                        type="text" className="form-control" required
                        value={borrowData.bookId} onChange={e => setBorrowData({...borrowData, bookId: e.target.value})}
                    />
                </div>
                <div className="mb-3">
                    <label className="form-label">Số lượng mượn</label>
                    <input
                        type="number" className="form-control" min="1" required
                        value={borrowData.quantity} onChange={e => setBorrowData({...borrowData, quantity: e.target.value})}
                    />
                </div>
                <button type="submit" className="btn btn-success w-100">Phát hành phiếu mượn</button>
            </form>
        </div>
    );
}