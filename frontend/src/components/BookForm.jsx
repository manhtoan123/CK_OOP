import React, { useState, useEffect } from 'react';

export default function BookForm({ editingBook, onSuccess, onCancel }) {
    const [book, setBook] = useState({ bookId: '', title: '', author: '', category: '', quantity: 1, price: 0 });
    const isEditMode = !!editingBook;

    // Lắng nghe trạng thái: Nếu nhấn Sửa ở bảng, lập tức nạp thông tin sách lên Form
    useEffect(() => {
        if (editingBook) {
            setBook(editingBook);
        } else {
            setBook({ bookId: '', title: '', author: '', category: '', quantity: 1, price: 0 });
        }
    }, [editingBook]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const url = isEditMode
            ? `http://localhost:8080/api/v1/books/${book.bookId}`
            : 'http://localhost:8080/api/v1/books';
        const method = isEditMode ? 'PUT' : 'POST';

        try {
            const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    ...book,
                    quantity: parseInt(book.quantity),
                    price: parseFloat(book.price)
                })
            });
            const data = await res.json();

            if (!res.ok) {
                if (data.details) throw new Error(Object.values(data.details).join('\n'));
                throw new Error(data.message || "Thao tác dữ liệu sách thất bại!");
            }

            alert(`🎉 Thành công: ${data.message}`);
            if (onSuccess) onSuccess(); // Tải lại bảng danh sách sách bên App.jsx
            if (isEditMode && onCancel) onCancel(); // Trở về chế độ thêm mới
            setBook({ bookId: '', title: '', author: '', category: '', quantity: 1, price: 0 });
        } catch (err) {
            alert(`❌ Lỗi nghiệp vụ kho sách:\n${err.message}`);
        }
    };

    return (
        <div className={`card p-4 shadow-sm bg-white border-${isEditMode ? 'warning' : 'success'}`}>
            <h4 className={`text-${isEditMode ? 'warning' : 'success'} mb-3 fw-bold`}>
                {isEditMode ? '📝 Hiệu Chỉnh Đầu Sách' : '📚 Nhập Kho Sách Mới'}
            </h4>
            <form onSubmit={handleSubmit}>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Mã đầu sách (Bxxx)</label>
                    <input
                        type="text" className="form-control" placeholder="Ví dụ: B001" required
                        disabled={isEditMode}
                        value={book.bookId} onChange={e => setBook({...book, bookId: e.target.value})}
                    />
                </div>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Tên cuốn sách</label>
                    <input type="text" className="form-control" placeholder="Nhập tên sách" required value={book.title} onChange={e => setBook({...book, title: e.target.value})} />
                </div>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Tác giả</label>
                    <input type="text" className="form-control" placeholder="Nhập tên tác giả" required value={book.author} onChange={e => setBook({...book, author: e.target.value})} />
                </div>
                <div className="mb-2">
                    <label className="form-label small fw-bold">Thể loại</label>
                    <input type="text" className="form-control" placeholder="Ví dụ: Giáo trình, Công nghệ" required value={book.category} onChange={e => setBook({...book, category: e.target.value})} />
                </div>
                <div className="row">
                    <div className="col-6 mb-3">
                        <label className="form-label small fw-bold">Số lượng tồn</label>
                        <input type="number" className="form-control" min="1" required value={book.quantity} onChange={e => setBook({...book, quantity: e.target.value})} />
                    </div>
                    <div className="col-6 mb-3">
                        <label className="form-label small fw-bold">Giá tiền (VND)</label>
                        <input type="number" className="form-control" min="0" required value={book.price} onChange={e => setBook({...book, price: e.target.value})} />
                    </div>
                </div>

                <div className="d-flex gap-2">
                    <button type="submit" className={`btn btn-${isEditMode ? 'warning text-white' : 'success'} flex-grow-1 fw-bold`}>
                        {isEditMode ? 'Cập Nhật Sách' : 'Xác Nhận Lưu Kho'}
                    </button>
                    {isEditMode && (
                        <button type="button" className="btn btn-secondary fw-bold" onClick={onCancel}>Hủy</button>
                    )}
                </div>
            </form>
        </div>
    );
}