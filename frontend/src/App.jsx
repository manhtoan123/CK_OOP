import React, { useState, useEffect } from 'react';
import ReaderForm from './components/ReaderForm';
import BorrowBookForm from './components/BorrowBookForm';
import ReturnBookForm from './components/ReturnBookForm';
import BookForm from './components/BookForm';

function App() {
    const [activeTab, setActiveTab] = useState('dashboard');

    // 👥 State phân hệ Độc giả
    const [readersList, setReadersList] = useState([]);
    const [isLoadingReaders, setIsLoadingReaders] = useState(false);
    const [editingReader, setEditingReader] = useState(null);

    // 📘 State phân hệ Kho Sách
    const [booksList, setBooksList] = useState([]);
    const [isLoadingBooks, setIsLoadingBooks] = useState(false);
    const [editingBook, setEditingBook] = useState(null);

    // 🔄 State phân hệ Giao dịch Mượn/Trả sách
    const [borrowList, setBorrowList] = useState([]);
    const [isLoadingBorrows, setIsLoadingBorrows] = useState(false);

    // 1️⃣ API: Đọc danh sách Bạn đọc từ Backend
    const fetchReaders = async () => {
        setIsLoadingReaders(true);
        try {
            const res = await fetch('http://localhost:8080/api/v1/readers');
            if (res.ok) setReadersList(await res.json());
        } catch (err) { console.error("Lỗi tải danh sách bạn đọc:", err); }
        finally { setIsLoadingReaders(false); }
    };

    // 2️⃣ API: Đọc danh sách Sách từ kho JSON
    const fetchBooks = async () => {
        setIsLoadingBooks(true);
        try {
            const res = await fetch('http://localhost:8080/api/v1/books');
            if (res.ok) setBooksList(await res.json());
        } catch (err) { console.error("Lỗi tải kho sách:", err); }
        finally { setIsLoadingBooks(false); }
    };

    // 3️⃣ API: Tải danh sách phiếu mượn đang hoạt động
    const fetchBorrows = async () => {
        setIsLoadingBorrows(true);
        try {
            const res = await fetch('http://localhost:8080/api/v1/borrow');
            if (res.ok) setBorrowList(await res.json());
        } catch (err) { console.error("Lỗi tải danh sách mượn trả:", err); }
        finally { setIsLoadingBorrows(false); }
    };

    // 🗑️ Hành động: Xóa độc giả
    const handleDeleteReader = async (userId) => {
        if (!window.confirm(`⚠️ Bạn có chắc chắn muốn xóa độc giả mang mã ${userId}?`)) return;
        try {
            const res = await fetch(`http://localhost:8080/api/v1/readers/${userId}`, { method: 'DELETE' });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || "Xóa độc giả thất bại!");
            alert(`🗑️ Thành công: ${data.message}`);
            fetchReaders();
            if (editingReader && editingReader.userId === userId) setEditingReader(null);
        } catch (err) { alert(`❌ Không thể xóa độc giả:\n${err.message}`); }
    };

    // 🗑️ Hành động: Xóa đầu sách khỏi kho
    const handleDeleteBook = async (bookId) => {
        if (!window.confirm(`⚠️ Bạn có chắc chắn muốn xóa đầu sách mang mã ${bookId} khỏi kho?`)) return;
        try {
            const res = await fetch(`http://localhost:8080/api/v1/books/${bookId}`, { method: 'DELETE' });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || "Xóa đầu sách thất bại!");
            alert(`🗑️ Thành công: ${data.message}`);
            fetchBooks();
            if (editingBook && editingBook.bookId === bookId) setEditingBook(null);
        } catch (err) { alert(`❌ Từ chối xóa sách:\n${err.message}`); }
    };

    // 🔄 Hành động: Trả sách nhanh trực tiếp từ hàng trên bảng
    const handleQuickReturn = async (readerId, bookId) => {
        if (!window.confirm(`🔄 Xác nhận thu hồi cuốn sách ${bookId} từ độc giả ${readerId}?`)) return;
        try {
            const res = await fetch('http://localhost:8080/api/v1/return', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ readerId, bookId })
            });
            const data = await res.json();
            if (!res.ok) throw new Error(data.message || "Giao dịch trả sách bị từ chối!");
            alert(`🎉 Trả sách thành công!\nThông báo quyết toán: ${data.message}`);
            fetchBorrows();
        } catch (err) { alert(`❌ Lỗi hệ thống:\n${err.message}`); }
    };

    // Luồng tự động nạp dữ liệu thông minh khi hoán đổi tab điều hướng
    useEffect(() => {
        if (activeTab === 'readers') fetchReaders();
        if (activeTab === 'books') fetchBooks();
        if (activeTab === 'transactions' || activeTab === 'dashboard') {
            fetchBorrows();
            fetchReaders();
            fetchBooks();
        }
    }, [activeTab]);

    return (
        <div className="container-fluid p-0 min-vh-100 bg-light d-flex flex-column flex-md-row">

            {/* ─── 1. SIDEBAR ĐIỀU HƯỚNG BÊN TRÁI ───────────────────────────────── */}
            <div className="bg-dark text-white p-3 d-flex flex-column" style={{ width: '100%', maxWidth: '280px', minHeight: '100vh' }}>
                <div className="d-flex align-items-center gap-2 mb-4 px-2 py-3 border-bottom border-secondary">
                    <span className="fs-4">📚</span>
                    <div>
                        <h5 className="mb-0 fw-bold text-uppercase" style={{ fontSize: '15px', color: '#a5b4fc' }}>LIB-MANAGEMENT</h5>
                        <small className="text-muted" style={{ fontSize: '11px' }}>Đồ án Nhóm 4 - Hệ Thống Thư Viện</small>
                    </div>
                </div>

                <ul className="nav nav-pills flex-column mb-auto gap-2">
                    <li className="nav-item">
                        <button className={`nav-link w-100 text-start text-white border-0 ${activeTab === 'dashboard' ? 'active fw-bold' : 'bg-transparent text-white-50'}`} style={activeTab === 'dashboard' ? { backgroundColor: '#4f46e5' } : {}} onClick={() => setActiveTab('dashboard')}>📊 Bảng điều khiển</button>
                    </li>
                    <li className="nav-item">
                        <button className={`nav-link w-100 text-start text-white border-0 ${activeTab === 'books' ? 'active fw-bold' : 'bg-transparent text-white-50'}`} style={activeTab === 'books' ? { backgroundColor: '#4f46e5' } : {}} onClick={() => setActiveTab('books')}>📘 Quản lý Kho Sách</button>
                    </li>
                    <li className="nav-item">
                        <button className={`nav-link w-100 text-start text-white border-0 ${activeTab === 'readers' ? 'active fw-bold' : 'bg-transparent text-white-50'}`} style={activeTab === 'readers' ? { backgroundColor: '#4f46e5' } : {}} onClick={() => setActiveTab('readers')}>👥 Quản lý Độc giả</button>
                    </li>
                    <li className="nav-item">
                        <button className={`nav-link w-100 text-start text-white border-0 ${activeTab === 'transactions' ? 'active fw-bold' : 'bg-transparent text-white-50'}`} style={activeTab === 'transactions' ? { backgroundColor: '#4f46e5' } : {}} onClick={() => setActiveTab('transactions')}>🔄 Mượn/Trả Sách</button>
                    </li>
                </ul>
                <div className="pt-3 border-top border-secondary text-center"><small className="text-muted" style={{ fontSize: '11px' }}>Phiên bản Kiến trúc 3 Tầng v2.0</small></div>
            </div>

            {/* ─── WORKSPACE HIỂN THỊ NỘI DUNG CHÍNH ────────────────────────────── */}
            <div className="flex-grow-1 d-flex flex-column overflow-hidden">
                <header className="navbar navbar-expand bg-white border-bottom shadow-sm px-4 py-3">
                    <div className="container-fluid p-0 d-flex justify-content-between align-items-center">
                        <h5 className="mb-0 fw-bold text-secondary">Hệ Thống Quản Lý Thư Viện</h5>
                        <div className="fw-semibold small text-primary">Nhóm 4 - Đồ án Java OOP</div>
                    </div>
                </header>

                <main className="flex-grow-1 p-4 overflow-auto">

                    {/* TAB 1: BẢNG ĐIỀU KHIỂN TỔNG QUAN */}
                    {activeTab === 'dashboard' && (
                        <div>
                            <h3 className="fw-bold text-dark mb-4">Bảng Điều Khiển Hệ Thống</h3>
                            <div className="row g-4">
                                <div className="col-12 col-sm-6 col-lg-3">
                                    <div className="card border-0 shadow-sm p-3 bg-white" style={{ borderLeft: '4px solid #4f46e5' }}>
                                        <p className="text-muted mb-1 small fw-bold text-uppercase">Số Lượng Độc Giả</p>
                                        <h4 className="fw-bold m-0 text-dark">{readersList.length} tài khoản</h4>
                                    </div>
                                </div>
                                <div className="col-12 col-sm-6 col-lg-3">
                                    <div className="card border-0 shadow-sm p-3 bg-white" style={{ borderLeft: '4px solid #10b981' }}>
                                        <p className="text-muted mb-1 small fw-bold text-uppercase">Đầu Sách Trong Kho</p>
                                        <h4 className="fw-bold m-0 text-success">{booksList.length} đầu sách</h4>
                                    </div>
                                </div>
                                <div className="col-12 col-sm-6 col-lg-3">
                                    <div className="card border-0 shadow-sm p-3 bg-white" style={{ borderLeft: '4px solid #f59e0b' }}>
                                        <p className="text-muted mb-1 small fw-bold text-uppercase">Đang Cho Mượn</p>
                                        <h4 className="fw-bold m-0 text-warning">{borrowList.length} cuốn</h4>
                                    </div>
                                </div>
                                <div className="col-12 col-sm-6 col-lg-3">
                                    <div className="card border-0 shadow-sm p-3 bg-white" style={{ borderLeft: '4px solid #ef4444' }}>
                                        <p className="text-muted mb-1 small fw-bold text-uppercase">Tình Trạng Kho</p>
                                        <h4 className="fw-bold m-0 text-dark">Ổn định</h4>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* ⚡ TAB 2: QUẢN LÝ KHO SÁCH (ĐÃ KHÔI PHỤC BẢNG FULL) */}
                    {activeTab === 'books' && (
                        <div>
                            <div className="mb-4 d-flex justify-content-between align-items-center">
                                <h3 className="fw-bold text-dark m-0">Quản Lý Danh Mục Kho Sách</h3>
                                <button className="btn btn-sm btn-outline-secondary fw-bold" onClick={fetchBooks}>🔄 Làm mới kho</button>
                            </div>
                            <div className="row g-4">
                                <div className="col-12 col-xl-4">
                                    <BookForm editingBook={editingBook} onSuccess={fetchBooks} onCancel={() => setEditingBook(null)} />
                                </div>
                                <div className="col-12 col-xl-8">
                                    <div className="card border-0 shadow-sm p-4 bg-white">
                                        <h5 className="text-dark fw-bold mb-3">📋 Danh Sách Sách Trong Tệp JSON</h5>
                                        {isLoadingBooks ? ( <div className="text-center py-4">Đang đọc kho sách...</div> ) : (
                                            <div className="table-responsive">
                                                <table className="table table-hover align-middle">
                                                    <thead className="table-light">
                                                    <tr>
                                                        <th style={{ fontSize: '11px' }}>MÃ SÁCH</th>
                                                        <th style={{ fontSize: '11px' }}>TÊN CUỐN SÁCH</th>
                                                        <th style={{ fontSize: '11px' }}>TÁC GIẢ / THỂ LOẠI</th>
                                                        <th style={{ fontSize: '11px' }} className="text-end">SỐ LƯỢNG</th>
                                                        <th style={{ fontSize: '11px' }} className="text-end">ĐƠN GIÁ</th>
                                                        <th style={{ fontSize: '11px' }} className="text-center">HÀNH ĐỘNG</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {booksList.length === 0 ? (
                                                        <tr><td colSpan="6" className="text-center text-muted py-3">Kho sách rỗng hoặc chưa kết nối Backend!</td></tr>
                                                    ) : (
                                                        booksList.map((b, idx) => (
                                                            <tr key={idx}>
                                                                <td><span className="badge bg-success-subtle text-success fw-bold px-2 py-1">{b.bookId}</span></td>
                                                                <td className="fw-bold text-secondary">{b.title}</td>
                                                                <td>
                                                                    <div className="small fw-semibold text-dark">{b.author}</div>
                                                                    <div className="text-muted" style={{ fontSize: '11px' }}>{b.category}</div>
                                                                </td>
                                                                <td className="text-end fw-bold">{b.quantity} cuốn</td>
                                                                <td className="text-end text-primary fw-semibold">{b.price?.toLocaleString('vi-VN')} đ</td>
                                                                <td className="text-center">
                                                                    <div className="d-flex justify-content-center gap-1">
                                                                        <button className="btn btn-sm btn-link text-warning p-0 px-1 fw-bold border-0 text-decoration-none" onClick={() => setEditingBook(b)}>Sửa</button>
                                                                        <span className="text-muted">|</span>
                                                                        <button className="btn btn-sm btn-link text-danger p-0 px-1 fw-bold border-0 text-decoration-none" onClick={() => handleDeleteBook(b.bookId)}>Xóa</button>
                                                                    </div>
                                                                </td>
                                                            </tr>
                                                        ))
                                                    )}
                                                    </tbody>
                                                </table>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* ⚡ TAB 3: QUẢN LÝ ĐỘC GIẢ (ĐÃ KHÔI PHỤC BẢNG FULL) */}
                    {activeTab === 'readers' && (
                        <div>
                            <div className="mb-4 d-flex justify-content-between align-items-center">
                                <h3 className="fw-bold text-dark m-0">Hồ Sơ Cấp Thẻ Bạn Đọc</h3>
                                <button className="btn btn-sm btn-outline-secondary fw-bold" onClick={fetchReaders}>🔄 Làm mới</button>
                            </div>
                            <div className="row g-4">
                                <div className="col-12 col-xl-4">
                                    <ReaderForm editingReader={editingReader} onSuccess={fetchReaders} onCancel={() => setEditingReader(null)} />
                                </div>
                                <div className="col-12 col-xl-8">
                                    <div className="card border-0 shadow-sm p-4 bg-white">
                                        <h5 className="text-dark fw-bold mb-3">📋 Danh Sách Bạn Đọc Hiện Có</h5>
                                        {isLoadingReaders ? ( <div className="text-center py-4">Đang tải dữ liệu...</div> ) : (
                                            <div className="table-responsive">
                                                <table className="table table-hover align-middle">
                                                    <thead className="table-light">
                                                    <tr>
                                                        <th style={{ fontSize: '11px' }}>MÃ ĐỘC GIẢ</th>
                                                        <th style={{ fontSize: '11px' }}>HỌ VÀ TÊN</th>
                                                        <th style={{ fontSize: '11px' }}>SỐ ĐIỆN THOẠI</th>
                                                        <th style={{ fontSize: '11px' }}>PHÂN LOẠI</th>
                                                        <th style={{ fontSize: '11px' }} className="text-center">HÀNH ĐỘNG</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {readersList.length === 0 ? (
                                                        <tr><td colSpan="5" className="text-center text-muted py-3">Danh sách rỗng hoặc lỗi kết nối!</td></tr>
                                                    ) : (
                                                        readersList.map((r, index) => {
                                                            const isLecturer = r.readerType === 'LECTURER' || r.readerType === 'Giảng viên';
                                                            const isPriority = r.readerType === 'PRIORITY_STUDENT' || r.readerType === 'Sinh viên ưu tiên';
                                                            return (
                                                                <tr key={index}>
                                                                    <td><span className="badge fw-bold px-2 py-1" style={{ backgroundColor: '#e0e7ff', color: '#4f46e5' }}>{r.userId}</span></td>
                                                                    <td className="fw-semibold text-secondary">{r.fullName}</td>
                                                                    <td>{r.phoneNumber}</td>
                                                                    <td>
                                      <span className="badge px-2 py-1" style={{
                                          backgroundColor: isLecturer ? '#d1fae5' : isPriority ? '#fef3c7' : '#dbeafe',
                                          color: isLecturer ? '#065f46' : isPriority ? '#92400e' : '#1e40af'
                                      }}>
                                        {isLecturer ? 'Giảng viên' : isPriority ? 'Sinh viên ưu tiên' : 'Sinh viên thường'}
                                      </span>
                                                                    </td>
                                                                    <td className="text-center">
                                                                        <div className="d-flex justify-content-center gap-1">
                                                                            <button className="btn btn-sm btn-link text-warning p-0 px-1 fw-bold border-0 text-decoration-none" onClick={() => setEditingReader(r)}>Sửa</button>
                                                                            <span className="text-muted">|</span>
                                                                            <button className="btn btn-sm btn-link text-danger p-0 px-1 fw-bold border-0 text-decoration-none" onClick={() => handleDeleteReader(r.userId)}>Xóa</button>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                            );
                                                        })
                                                    )}
                                                    </tbody>
                                                </table>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* TAB 4: QUẢN LÝ GIAO DỊCH MƯỢN / TRẢ SÁCH */}
                    {activeTab === 'transactions' && (
                        <div>
                            <div className="row g-4 mb-4">
                                <div className="col-12 col-lg-6">
                                    <BorrowBookForm onSuccess={fetchBorrows} />
                                </div>
                                <div className="col-12 col-lg-6">
                                    <ReturnBookForm onSuccess={fetchBorrows} />
                                </div>
                            </div>

                            <div className="card border-0 shadow-sm p-4 bg-white">
                                <div className="d-flex justify-content-between align-items-center mb-3">
                                    <h5 className="text-dark fw-bold m-0">📊 Sổ Giám Sát Lưu Thông Sách (Mượn Hiện Hành)</h5>
                                    <button className="btn btn-sm btn-outline-secondary fw-bold" onClick={fetchBorrows}>🔄 Làm mới sổ</button>
                                </div>

                                {isLoadingBorrows ? (
                                    <div className="text-center py-4">Đang đọc dữ liệu phiếu mượn...</div>
                                ) : (
                                    <div className="table-responsive">
                                        <table className="table table-hover align-middle">
                                            <thead className="table-light">
                                            <tr>
                                                <th style={{ fontSize: '11px' }}>MÃ ĐỘC GIẢ</th>
                                                <th style={{ fontSize: '11px' }}>MÃ SÁCH</th>
                                                <th style={{ fontSize: '11px' }} className="text-center">SỐ LƯỢNG MƯỢN</th>
                                                <th style={{ fontSize: '11px' }}>NGÀY PHÁT HÀNH</th>
                                                <th style={{ fontSize: '11px' }}>HẠN TRẢ SÁCH</th>
                                                <th style={{ fontSize: '11px' }} className="text-center">HÀNH ĐỘNG NHANH</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {borrowList.map((b, idx) => (
                                                <tr key={idx}>
                                                    <td><span className="badge fw-bold px-2 py-1" style={{ backgroundColor: '#e0e7ff', color: '#4f46e5' }}>{b.readerId}</span></td>
                                                    <td><span className="badge bg-success-subtle text-success fw-bold px-2 py-1">{b.bookId}</span></td>
                                                    <td className="text-center fw-bold text-secondary">{b.quantity || 1} cuốn</td>
                                                    <td className="text-muted">{b.borrowDate}</td>

                                                    {/*  HIỂN THỊ HẠN TRẢ MÀU ĐỎ/VÀNG ĐỂ THỦ THƯ DỄ THEO DÕI */}
                                                    <td className="text-danger fw-bold">{b.dueDate || 'Chưa thiết lập'}</td>

                                                    <td className="text-center">
                                                        <button
                                                            className="btn btn-sm btn-outline-warning fw-bold px-3 py-1 rounded-pill"
                                                            onClick={() => handleQuickReturn(b.readerId, b.bookId)}
                                                        >
                                                            🔄 Trả nhanh
                                                        </button>
                                                    </td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </table>
                                    </div>
                                )}
                            </div>
                        </div>
                    )}

                </main>
            </div>
        </div>
    );
}

export default App;